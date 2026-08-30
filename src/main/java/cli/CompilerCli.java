package cli;

import ANT.*;
import ast.*;
import org.antlr.v4.runtime.*;
import parseTree.*;
import symboltable.*;
import visitor.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CompilerCli {
    
    public static void main(String[] args) {
        String pythonFile = null;
        String templatesDir = null;
        String outputDir = "generated/flask_app";
        boolean printAst = false;
        boolean printSymbols = false;
        boolean printDiagnostics = true;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--python":
                    pythonFile = args[++i];
                    break;
                case "--templates":
                    templatesDir = args[++i];
                    break;
                case "--output":
                    outputDir = args[++i];
                    break;
                case "--print-ast":
                    printAst = true;
                    break;
                case "--print-symbols":
                    printSymbols = true;
                    break;
                case "--diagnostics":
                    printDiagnostics = true;
                    break;
                default:
                    System.err.println("Unknown argument: " + args[i]);
            }
        }
        
        if (pythonFile == null) {
            System.err.println("Error: --python <file> is required.");
            System.exit(1);
        }

        try {
            System.out.println("Processing Python file: " + pythonFile);
            SymbolTable symbolTable = new SymbolTable();
            DiagnosticCollector diagnostics = new DiagnosticCollector();
            SemanticAnalyzer analyzer = new SemanticAnalyzer(symbolTable, diagnostics);

            // Step 1: Discover template files and register template names
            List<File> htmlFiles = new ArrayList<>();
            List<File> cssFiles = new ArrayList<>();
            
            if (templatesDir != null) {
                File dir = new File(templatesDir);
                if (dir.exists() && dir.isDirectory()) {
                    File[] files = dir.listFiles((d, name) -> name.endsWith(".html") || name.endsWith(".css"));
                    if (files != null) {
                        for (File f : files) {
                            if (f.getName().endsWith(".css")) {
                                cssFiles.add(f);
                            } else {
                                analyzer.addKnownTemplate(f.getName());
                                htmlFiles.add(f);
                            }
                        }
                    }
                }
            }

            // Step 2: Parse Python and build Python AST
            String pythonContent = Files.readString(Paths.get(pythonFile), StandardCharsets.UTF_8);
            BailErrorListener pythonBail = new BailErrorListener();
            PythonLexer lexer = new PythonLexer(CharStreams.fromString(pythonContent));
            lexer.removeErrorListeners();
            lexer.addErrorListener(pythonBail);
            
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            PythonParser parser = new PythonParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(pythonBail);
            
            PythonParser.ProgramContext tree = parser.program();
            
            if (pythonBail.hasErrors()) {
                System.err.println("Syntax errors found in Python file. Aborting.");
                System.exit(1);
            }

            PythonASTBuilder astBuilder = new PythonASTBuilder(new File(pythonFile).getName());
            ProgramNode pyAst = (ProgramNode) astBuilder.visit(tree);

            // Step 3: Analyze Python and extract render_template contexts
            pyAst.accept(analyzer);

            // Step 4: Parse and analyze Template ASTs
            List<ASTNode> templateAsts = new ArrayList<>();
            for (File f : htmlFiles) {
                TemplateLexer tLexer = new TemplateLexer(CharStreams.fromFileName(f.getAbsolutePath()));
                tLexer.removeErrorListeners();
                BailErrorListener tBail = new BailErrorListener();
                tLexer.addErrorListener(tBail);
                
                TemplateParser tParser = new TemplateParser(new CommonTokenStream(tLexer));
                tParser.removeErrorListeners();
                tParser.addErrorListener(tBail);
                
                TemplateParser.TemplateContext tTree = tParser.template();
                
                if (tBail.hasErrors()) {
                    System.err.println("Syntax errors found in Template file: " + f.getName() + ". Aborting.");
                    System.exit(1);
                }
                
                TemplateASTBuilder tAstBuilder = new TemplateASTBuilder(f.getName());
                ASTNode tAst = tAstBuilder.visit(tTree);
                tAst.accept(analyzer); // Run semantic analysis on template
                templateAsts.add(tAst);
                
                if (printAst) {
                    System.out.println("\n--- Template AST: " + f.getName() + " ---");
                    System.out.println(tAst.accept(new PrintVisitor()));
                }
            }

            // Step 5: Parse standalone CSS files
            java.util.Map<String, String> cssAsts = new java.util.HashMap<>();
            for (File f : cssFiles) {
                CssLexer cLexer = new CssLexer(CharStreams.fromFileName(f.getAbsolutePath()));
                cLexer.removeErrorListeners();
                BailErrorListener cBail = new BailErrorListener();
                cLexer.addErrorListener(cBail);
                
                CssParser cParser = new CssParser(new CommonTokenStream(cLexer));
                cParser.removeErrorListeners();
                cParser.addErrorListener(cBail);
                
                CssParser.StylesheetContext cTree = cParser.stylesheet();
                
                if (cBail.hasErrors()) {
                    System.err.println("Syntax errors found in CSS file: " + f.getName() + ". Aborting.");
                    System.exit(1);
                }
                
                // For standalone CSS, we validate via parsing, and then we will simply output its content directly 
                // in the generation phase. We can create a dummy AST node for it.
                cssAsts.put(f.getName(), Files.readString(f.toPath(), StandardCharsets.UTF_8));
            }

            if (printDiagnostics) {
                diagnostics.printAll();
            }

            if (diagnostics.hasErrors() || pythonBail.hasErrors()) {
                System.err.println("Compilation failed due to errors.");
                System.exit(1);
            }

            if (printAst) {
                System.out.println("\n--- Python AST ---");
                System.out.println(pyAst.accept(new PrintVisitor()));
            }

            if (printSymbols) {
                symbolTable.printSymbolTable();
            }

            System.out.println("\nGenerating project in " + outputDir + " ...");
            ProjectGenerator generator = new ProjectGenerator();
            generator.generateProject(pyAst, outputDir);
            
            if (templatesDir != null) {
                File tDirOut = new File(outputDir, "templates");
                tDirOut.mkdirs();
                
                // Generate HTML files from ASTs
                for (ASTNode tAst : templateAsts) {
                    String generatedHtml = tAst.accept(generator);
                    File outHtml = new File(tDirOut, tAst.getFile());
                    java.nio.file.Files.writeString(outHtml.toPath(), generatedHtml, StandardCharsets.UTF_8);
                }
                
                // Generate validated standalone CSS files under Flask's conventional static/css path.
                File cssDirOut = new File(outputDir, "static/css");
                cssDirOut.mkdirs();
                for (java.util.Map.Entry<String, String> entry : cssAsts.entrySet()) {
                    File outCss = new File(cssDirOut, entry.getKey());
                    java.nio.file.Files.writeString(outCss.toPath(), entry.getValue(), StandardCharsets.UTF_8);
                }
            }
            
            System.out.println("Compilation and Generation Successful!");

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
