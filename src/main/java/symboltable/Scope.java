package symboltable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scope {
    private String name;
    private Scope parent;
    private List<Scope> children = new ArrayList<>();
    private Map<String, Symbol> symbols = new HashMap<>();

    public Scope(String name, Scope parent) {
        this.name = name;
        this.parent = parent;
        if (parent != null) {
            parent.addChild(this);
        }
    }

    public void addChild(Scope child) {
        children.add(child);
    }

    public void define(Symbol symbol) {
        symbols.put(symbol.getName(), symbol);
    }

    public Symbol resolve(String name) {
        Symbol s = symbols.get(name);
        if (s != null) return s;
        if (parent != null) return parent.resolve(name);
        return null;
    }
    
    public Symbol resolveLocal(String name) {
        return symbols.get(name);
    }

    public String getName() { return name; }
    public Scope getParent() { return parent; }
    public List<Scope> getChildren() { return children; }
    public Map<String, Symbol> getSymbols() { return symbols; }
}
