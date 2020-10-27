package eu.europa.ec.mare.usm.administration.service.person.impl;

import eu.europa.ec.mare.usm.administration.domain.ContactDetails;
import eu.europa.ec.mare.usm.administration.domain.PendingContactDetails;
import eu.europa.ec.mare.usm.administration.domain.Person;
import eu.europa.ec.mare.usm.information.entity.PendingDetailsEntity;
import eu.europa.ec.mare.usm.information.entity.PersonEntity;

import javax.ejb.Stateless;

/**
 * Converts entity to domain objects and vice-versa
 */
@Stateless
public class PersonConverter {

    public PersonConverter() {
    }

    /**
     * Converts from an entity representation to a domain-object
     * representation.
     *
     * @param entity the entity
     * @return the domain-object
     */
    public Person convertPerson(PersonEntity entity) {
        Person ret = new Person();
        if (entity != null) {
            ret = new Person();
            ret.setEmail(entity.getEMail());
            ret.setFaxNumber(entity.getFaxNumber());
            ret.setFirstName(entity.getFirstName());
            ret.setLastName(entity.getLastName());
            ret.setMobileNumber(entity.getMobileNumber());
            ret.setPersonId(entity.getPersonId());
            ret.setPhoneNumber(entity.getPhoneNumber());
        }

        return ret;
    }

    /**
     * Updates an entity with the attributes of the domain-object.
     *
     * @param ret the entity representation
     * @param src the domain-object representation
     */
    public void update(PersonEntity ret, ContactDetails src) {
        if (ret != null && src != null) {
            ret.setEMail(src.getEmail());
            ret.setFaxNumber(src.getFaxNumber());
            ret.setMobileNumber(src.getMobileNumber());
            ret.setPhoneNumber(src.getPhoneNumber());
        }
    }

    /**
     * Converts from an entity representation to a domain-object representation.
     *
     * @param src the entity
     * @return the domain-object
     */
    public ContactDetails convertContactDetails(PersonEntity src) {
        ContactDetails ret = null;

        if (src != null) {
            ret = new ContactDetails();

            ret.setEmail(src.getEMail());
            ret.setFaxNumber(src.getFaxNumber());
            ret.setMobileNumber(src.getMobileNumber());
            ret.setPhoneNumber(src.getPhoneNumber());
            ret.setFirstName(src.getFirstName());
            ret.setLastName(src.getLastName());
        }

        return ret;
    }

    /**
     * Converts from an entity representation to a domain-object representation.
     *
     * @param src the entity
     * @return the domain-object
     */
    public PendingContactDetails convertContactDetails(PendingDetailsEntity src) {
        PendingContactDetails ret = null;

        if (src != null) {
            ret = new PendingContactDetails();

            ret.setEmail(src.getEMail());
            ret.setFaxNumber(src.getFaxNumber());
            ret.setMobileNumber(src.getMobileNumber());
            ret.setPhoneNumber(src.getPhoneNumber());
            ret.setFirstName(src.getFirstName());
            ret.setLastName(src.getLastName());
            ret.setUserName(src.getUserName());
        }

        return ret;
    }
}
