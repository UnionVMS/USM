package eu.europa.ec.mare.usm.information.domain;

import java.io.Serializable;
import java.util.Set;

public class Preferences implements Serializable {
    private static final long serialVersionUID = 1L;
    private Set<Preference> preferences;

    public Preferences() {
    }

    public Set<Preference> getPreferences() {
        return preferences;
    }

    public void setPreferences(Set<Preference> preferences) {
        this.preferences = preferences;
    }

    @Override
    public String toString() {
        return "Preferences{" + "preferences=" + preferences + '}';
    }
}
