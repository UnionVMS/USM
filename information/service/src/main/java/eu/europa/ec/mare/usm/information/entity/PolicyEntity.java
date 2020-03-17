package eu.europa.ec.mare.usm.information.entity;

import javax.persistence.*;

@Entity
@SequenceGenerator(name = "policySequence", sequenceName = "SQ_POLICY", allocationSize = 1)
@Table(name = "POLICY_T")
@NamedQueries({
        @NamedQuery(name = "PolicyEntity.findByPolicyId", query = "SELECT p FROM PolicyEntity p WHERE p.policyId = :policyId"),
        @NamedQuery(name = "PolicyEntity.findBySubject", query = "SELECT p FROM PolicyEntity p WHERE p.subject = :subject")})
public class PolicyEntity extends AbstractAuditedEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "POLICY_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "policySequence")
    private Long policyId;

    @Basic(optional = false)
    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Basic(optional = false)
    @Column(name = "SUBJECT")
    private String subject;

    @Basic(optional = false)
    @Column(name = "VALUE")
    private String value;

    public PolicyEntity() {
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "PolicyEntity{" +
                "policyId=" + policyId +
                ", name=" + name +
                ", description=" + description +
                ", subject=" + subject +
                ", value=" + value +
                '}';
    }

}
