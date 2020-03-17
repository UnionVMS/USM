package eu.europa.ec.mare.usm.information.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@SequenceGenerator(name = "endPointContactSequence", sequenceName = "SQ_END_POINT_CONTACT", allocationSize = 1)
@Table(name = "END_POINT_CONTACT_T")
@NamedQueries({
        @NamedQuery(name = "EndPointContactEntity.findById", query = "SELECT e FROM EndPointContactEntity e WHERE e.endPointContactId = :endPointContactId"),
        @NamedQuery(name = "EndPointContactEntity.findByEndPointId", query = "SELECT e FROM EndPointContactEntity e WHERE e.endPoint.endPointId = :endPointId")})
public class EndPointContactEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "END_POINT_CONTACT_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "endPointContactSequence")
    private Long endPointContactId;

    @JoinColumn(name = "END_POINT_ID", referencedColumnName = "END_POINT_ID")
    @ManyToOne(optional = false)
    private EndPointEntity endPoint;

    @JoinColumn(name = "PERSON_ID", referencedColumnName = "PERSON_ID")
    @ManyToOne(optional = false)
    private PersonEntity person;

    public Long getEndPointContactId() {
        return endPointContactId;
    }

    public void setEndPointContactId(Long endPointContactId) {
        this.endPointContactId = endPointContactId;
    }

    public EndPointEntity getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(EndPointEntity endPoint) {
        this.endPoint = endPoint;
    }

    public PersonEntity getPerson() {
        return person;
    }

    public void setPerson(PersonEntity person) {
        this.person = person;
    }

    @Override
    public String toString() {
        return "EndPointContactEntity [endPointContactId=" + endPointContactId
                + ", endPoint=" + endPoint + ", person=" + person + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((endPoint == null) ? 0 : endPoint.hashCode());
        result = prime * result + ((person == null) ? 0 : person.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EndPointContactEntity other = (EndPointContactEntity) obj;
        if (endPoint == null) {
            if (other.endPoint != null)
                return false;
        } else if (!endPoint.equals(other.endPoint))
            return false;
        if (person == null) {
            if (other.person != null)
                return false;
        } else if (!person.equals(other.person))
            return false;
        return true;
    }

}
