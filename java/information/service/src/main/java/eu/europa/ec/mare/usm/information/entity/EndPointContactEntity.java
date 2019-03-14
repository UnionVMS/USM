package eu.europa.ec.mare.usm.information.entity;

import java.io.Serializable;

import javax.persistence.*;

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

	/**
	 * @return the endPointContactId
	 */
	public Long getEndPointContactId() {
		return endPointContactId;
	}

	/**
	 * @param endPointContactId the endPointContactId to set
	 */
	public void setEndPointContactId(Long endPointContactId) {
		this.endPointContactId = endPointContactId;
	}

	/**
	 * @return the endPoint
	 */
	public EndPointEntity getEndPoint() {
		return endPoint;
	}

	/**
	 * @param endPoint the endPoint to set
	 */
	public void setEndPoint(EndPointEntity endPoint) {
		this.endPoint = endPoint;
	}

	/**
	 * @return the person
	 */
	public PersonEntity getPerson() {
		return person;
	}

	/**
	 * @param person the person to set
	 */
	public void setPerson(PersonEntity person) {
		this.person = person;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EndPointContactEntity [endPointContactId=" + endPointContactId
				+ ", endPoint=" + endPoint + ", person=" + person + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endPoint == null) ? 0 : endPoint.hashCode());
		result = prime * result + ((person == null) ? 0 : person.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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
