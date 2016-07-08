/*
 * Developed by the European Commission - Directorate General for Maritime 
 * Affairs and Fisheries © European Union, 2015-2016.
 * 
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite.
 * The IFDM Suite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or any later version.
 * The IFDM Suite is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details. You should have received a copy of the GNU General Public 
 * License along with the IFDM Suite. If not, see http://www.gnu.org/licenses/.
 */
package eu.europa.ec.mare.usm.administration.service.person.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.information.entity.PersonEntity;

public class PersonJpaDao {
	private static final Logger LOGGER = LoggerFactory.getLogger(PersonJpaDao.class);

	@PersistenceContext(unitName = "USM-Administration")
	private EntityManager em;

	/**
	 * Creates a new instance
	 */
	public PersonJpaDao() {
	}
	
	/**
	   * Reads the list of all persons from the database
	   * 
	   * @return the List of all persons from the database, null if any error encountered
	   */
	  public List<PersonEntity> findAll() 
	  {
	    LOGGER.info("findAll() - (ENTER)");

	    List<PersonEntity> ret = null;
	    
	    try {
            TypedQuery<PersonEntity> q = em.createNamedQuery(
                    "PersonEntity.findAll", PersonEntity.class);

            ret = q.getResultList();
        } catch (NoResultException exc) {
            LOGGER.debug("PersonEntity list could not be extracted");
        } catch (Exception ex) {
            handleException("read", ex);
        }

	    LOGGER.info("findAll() - (LEAVE)"+ret);
	    return ret;
	  }
	
	private void handleException(String operation, Exception ex)
			throws RuntimeException {
		String msg = "Error during " + operation + " organisation : "
				+ ex.getMessage();
		LOGGER.error(msg, ex);
		throw new RuntimeException(msg, ex);
	}
	
	/**
	   * Reads the Person with the provided (internal) unique identifier
	   * 
	   * @param personId the end point (internal) unique identifier
	   * 
	   * @return the matching end point if it exists, null otherwise
	   */
	  public PersonEntity read(Long personId) 
	  {
	    LOGGER.info("read() - (ENTER)");

	    PersonEntity ret = null;
	    
	    try {
	      ret = em.find(PersonEntity.class, personId);
	    } catch (NoResultException exc) {
	      LOGGER.debug("PersonEntity " + personId + " not found");
	    } catch (Exception ex) {
	      handleException("read", ex);
	    }

	    LOGGER.info("read() - (LEAVE)"+ret);
	    return ret;
	  }
	  
	  /**
	   * Updates user's contact details.
	   *
	   * @param person the contact details to be updated
	   * 
	   * @return the updated contact details
	   */
	  public PersonEntity update(PersonEntity person) 
	  {
	    LOGGER.info("update(" + person + ") - (ENTER)");
	    
	    PersonEntity ret = null;
	    try {
	      ret = em.merge(person);
	      em.flush();
	    } catch (Exception ex) {
	      String msg = "Failed to update contact details: " + ex.getMessage();
	      LOGGER.error(msg, ex);
	      throw new RuntimeException(msg, ex);
	    }

	    LOGGER.info("update() - (LEAVE)");
	    return ret;
	  }
	  
	  
}