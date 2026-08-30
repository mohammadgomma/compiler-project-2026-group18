package symboltable;

public class SymbolTable {
    private Scope globalScope;
    private Scope currentScope;

    public SymbolTable() {
        globalScope = new Scope("Global", null);
        currentScope = globalScope;
    }

    public void enterScope(String name) {
        Scope scope = new Scope(name, currentScope);
        currentScope = scope;
    }

    public void exitScope() {
        if (currentScope.getParent() != null) {
            currentScope = currentScope.getParent();
        }
    }

    public void define(Symbol symbol) {
        currentScope.define(symbol);
    }

    public Symbol resolve(String name) {
        return currentScope.resolve(name);
    }
    
    public Symbol resolveLocal(String name) {
        return currentScope.resolveLocal(name);
    }

    public Scope getCurrentScope() {
        return currentScope;
    }

    public Scope getGlobalScope() {
        return globalScope;
    }

    public void printSymbolTable() {
        System.out.println("====== SYMBOL TABLE ======");
        printScope(globalScope, 0);
        System.out.println("==========================");
    }

    private void printScope(Scope scope, int indentLevel) {
        String indent = "  ".repeat(indentLevel);
        System.out.println(indent + "Scope: " + scope.getName());
        for (Symbol sym : scope.getSymbols().values()) {
            System.out.println(indent + "  - " + sym);
        }
        for (Scope child : scope.getChildren()) {
            printScope(child, indentLevel + 1);
        }
    }
}
