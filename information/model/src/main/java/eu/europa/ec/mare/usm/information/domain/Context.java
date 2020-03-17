package eu.europa.ec.mare.usm.information.domain;

import java.io.Serializable;

/**
 * Holds a Context.<br/>
 * A context is an association of three elements:
 * <ul>
 * <li>A scope (optional) based on a set of datasets from any
 * applications. The goal  is to limit the visibility on data;</li>
 * <li>A role (mandatory) based on a set a features from any applications.
 * The goal is  to define the actions that can be performed on data;</li>
 * <li>A set of contextual preferences (optional): These preferences are not
 * managed through the USM user interface. B;</li>
 * </ul>
 * A context is defined by an USM administrator by associating it with a user.
 * It is his responsibility to guarantee that the different elements composing
 * the context are coherent from a business point of view;<br/>
 * A user may have no context if no role has been associated to him.
 * He may have multiple contexts.;<br/>
 * The USM administrator cannot define the default context of the user.
 * If an application wants to provide such a feature it may do so using a
 * preference;<br/>
 */
public class Context implements Serializable {
    private static final long serialVersionUID = 1L;
    private Role role;
    private Scope scope;

    public Context() {
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    private Preferences preferences;

    public Preferences getPreferences() {
        return preferences;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public String toString() {
        return "Context{" +
                "role=" + role +
                ", scope=" + scope +
                ", preferences=" + preferences +
                '}';
    }
}

