package eu.europa.ec.mare.usm.information.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@SequenceGenerator(name = "userContextSequence", sequenceName = "SQ_USER_ROLE", allocationSize = 1)
@Table(name = "USER_CONTEXT_T")
@NamedQueries({
        @NamedQuery(name = "UserContextEntity.findByUserContextId", query = "SELECT u FROM UserContextEntity u WHERE u.userContextId = :userContextId"),
        @NamedQuery(name = "UserContextEntity.findByStatusActiveAndRoleId", query = "SELECT u FROM UserContextEntity u WHERE u.role.roleId = :roleId"),
        @NamedQuery(name = "UserContextEntity.findByStatusActiveAndScopeId", query = "SELECT u FROM UserContextEntity u WHERE u.scope.scopeId = :scopeId")
})
public class UserContextEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "USER_CONTEXT_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userContextSequence")
    private Long userContextId;

    @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")
    @ManyToOne(optional = false)
    private UserEntity user;

    @JoinColumn(name = "SCOPE_ID", referencedColumnName = "SCOPE_ID")
    @ManyToOne
    private ScopeEntity scope;

    @JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID")
    @ManyToOne(optional = false)
    private RoleEntity role;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userContext")
    private List<PreferenceEntity> preferenceList;

    public UserContextEntity() {
    }

    public Long getUserContextId() {
        return userContextId;
    }

    public void setUserContextId(Long userContextId) {
        this.userContextId = userContextId;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public ScopeEntity getScope() {
        return scope;
    }

    public void setScope(ScopeEntity scope) {
        this.scope = scope;
    }

    public RoleEntity getRole() {
        return role;
    }

    public void setRole(RoleEntity role) {
        this.role = role;
    }

    public List<PreferenceEntity> getPreferenceList() {
        return preferenceList;
    }

    public void setPreferenceList(List<PreferenceEntity> preferenceList) {
        this.preferenceList = preferenceList;
    }

    @Override
    public String toString() {
        return "UserContextEntity [userContextId=" + userContextId + ", user="
                + user + ", scope=" + scope + ", role=" + role + "]";
    }

}
