package ast;

import java.util.List;
import java.util.Map;

public class ImportNode extends PythonNode {
    private String moduleName;
    private List<String> names;
    public ImportNode(String moduleName, List<String> names) {
        this.moduleName = moduleName;
        this.names = names;
    }
    public String getModulename() { return moduleName; }
    public List<String> getNames() { return names; }
    @Override
    public <T> T accept(visitor.ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
