package eu.europa.ec.mare.usm.information.domain;

import java.io.Serializable;
import java.util.Set;

/**
 * Holds a set of Contexts.
 */
public class ContextSet implements Serializable {
    private static final long serialVersionUID = 1L;
    private Set<Context> contexts;

    public ContextSet() {
    }

    public Set<Context> getContexts() {
        return contexts;
    }

    public void setContexts(Set<Context> contexts) {
        this.contexts = contexts;
    }

    @Override
    public String toString() {
        return "ContextSet{" +
                "contexts=" + contexts +
                '}';
    }
}
