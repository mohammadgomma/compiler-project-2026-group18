package symboltable;

import java.util.ArrayList;
import java.util.List;

public class DiagnosticCollector {
    private List<CompilerError> errors = new ArrayList<>();

    public void addError(CompilerError error) {
        errors.add(error);
    }

    public boolean hasErrors() {
        return errors.stream().anyMatch(e -> e.getSeverity() == CompilerError.Severity.ERROR);
    }

    public List<CompilerError> getErrors() {
        return errors;
    }

    public void printAll() {
        for (CompilerError e : errors) {
            if (e.getSeverity() == CompilerError.Severity.ERROR) {
                System.err.println(e);
            } else {
                System.out.println(e);
            }
        }
    }
}
