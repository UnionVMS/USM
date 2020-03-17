package eu.europa.ec.mare.usm.information.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * Abstract base class for JPA entities mapped to database tables with auditing
 * columns.
 */
@MappedSuperclass
public class AbstractAuditedEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "CREATED_BY")
    protected String createdBy;

    @Column(name = "CREATED_ON")
    @Temporal(value = TemporalType.TIMESTAMP)
    protected Date createdOn;

    @Column(name = "MODIFIED_BY")
    protected String modifiedBy;

    @Column(name = "MODIFIED_ON")
    @Temporal(value = TemporalType.TIMESTAMP)
    protected Date modifiedOn;

    public AbstractAuditedEntity() {
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }
}
