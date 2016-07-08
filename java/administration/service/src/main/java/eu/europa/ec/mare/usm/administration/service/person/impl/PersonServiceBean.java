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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.audit.model.exception.AuditModelMarshallException;
import eu.europa.ec.fisheries.uvms.audit.model.mapper.AuditLogMapper;
import eu.europa.ec.mare.audit.logger.AuditLogger;
import eu.europa.ec.mare.audit.logger.AuditLoggerFactory;
import eu.europa.ec.mare.audit.logger.AuditRecord;
import eu.europa.ec.mare.usm.administration.domain.AuditObjectTypeEnum;
import eu.europa.ec.mare.usm.administration.domain.AuditOperationEnum;
import eu.europa.ec.mare.usm.administration.domain.AuditRecordFactory;
import eu.europa.ec.mare.usm.administration.domain.ContactDetails;
import eu.europa.ec.mare.usm.administration.domain.NoBody;
import eu.europa.ec.mare.usm.administration.domain.PendingContactDetails;
import eu.europa.ec.mare.usm.administration.domain.Person;
import eu.europa.ec.mare.usm.administration.domain.PolicyDefinition;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.USMApplication;
import eu.europa.ec.mare.usm.administration.domain.USMFeature;
import eu.europa.ec.mare.usm.administration.domain.UnauthorisedException;

import eu.europa.ec.mare.usm.administration.service.person.PersonService;
import eu.europa.ec.mare.usm.administration.service.policy.DefinitionService;
import eu.europa.ec.mare.usm.administration.service.user.impl.UserJpaDao;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationRequest;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationResponse;
import eu.europa.ec.mare.usm.authentication.service.AuthenticationService;
import eu.europa.ec.mare.usm.information.entity.PendingDetailsEntity;
import eu.europa.ec.mare.usm.information.entity.PersonEntity;
import eu.europa.ec.mare.usm.information.entity.UserEntity;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class PersonServiceBean implements PersonService {
  private static final Logger LOGGER = LoggerFactory.getLogger(PersonServiceBean.class);
  private static final String UPDATE_CONTACT_DETAILS = "update.contact.details.enabled";
  private static final String REVIEW_CONTACT_DETAILS = "review.contact.details.enabled";
  private static final String FEATURE_POLICY = "Feature";
  private static final String TRUE = "true";
  private static final String FALSE = "false";
  private static final String USER_UNAUTHENTICATED = "Authentication failed";
  private static final String USER_DOES_NOT_EXIST = "User does not exist";
  private static final String CHANGES_DO_NOT_EXIST = "Pending changes do not exist";


  @Inject
  private PersonJpaDao personJpaDao;

  @Inject
  private PendingDetailsJpaDao pendingJpaDao;
  
  @Inject
  private PersonValidator validator;

  @Inject
  private PersonConverter converter;

  @Inject
  private UserJpaDao userJpaDao;

  @EJB
  AuthenticationService authService;

  @EJB
  DefinitionService definition;

  
  private final AuditLogger auditLogger;

  /**
   * Creates a new instance
   */
  public PersonServiceBean() 
  {
    auditLogger = AuditLoggerFactory.getAuditLogger();
  }

  @Override
  public Person getPerson(ServiceRequest<Long> personRequest)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException 
  {
    LOGGER.info("getPerson(" + personRequest + ") - (ENTER)");

    validator.assertValid(personRequest, null, "personId");

    PersonEntity ret = personJpaDao.read(personRequest.getBody());

    Person response = converter.convertPerson(ret);

    LOGGER.info("getPerson() - (LEAVE) " + response);
    return response;
  }

  @Override
  public List<Person> getPersons() 
  {
    LOGGER.info("getPersons() - (ENTER)");

    List<PersonEntity> lst = personJpaDao.findAll();

    List<Person> ret = new ArrayList<>();
    for (PersonEntity person : lst) {
      ret.add(converter.convertPerson(person));
    }

    LOGGER.info("getPersons() - (LEAVE) " + ret.size());
    return ret;
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public ContactDetails updateContactDetails(ServiceRequest<ContactDetails> request)
  throws IllegalArgumentException, RuntimeException, IllegalStateException 
  {
    LOGGER.info("updateContactDetails(" + request + ") - (ENTER)");

    // Ensure feature is enabled
    if (!isUpdateContactDetailsEnabled()) {
      throw new IllegalStateException("This feature is disabled");
    }
    // Ensure request is valid
    validator.assertValid(request);
    
    // Ensure target user exists
    UserEntity user = userJpaDao.read(request.getRequester());
    if (user == null) {
      throw new IllegalArgumentException(USER_DOES_NOT_EXIST);
    }

    // Authenticate the requester
    AuthenticationRequest authRequest = new AuthenticationRequest();
    authRequest.setUserName(request.getRequester());
    authRequest.setPassword(request.getPassword());
    AuthenticationResponse serviceResponse = authService.authenticateUser(authRequest);
    if (!serviceResponse.isAuthenticated()) {
      String msg;
      
      switch (serviceResponse.getStatusCode()) {
        case AuthenticationResponse.ACCOUNT_DISABLED:
          msg = "Account disabled";
          break;
        case AuthenticationResponse.ACCOUNT_LOCKED:
          msg = "Account locked";
          break;
        case AuthenticationResponse.INTERNAL_ERROR:
          msg = "Internal error";
          break;
        case AuthenticationResponse.INVALID_CREDENTIALS:
          msg = "Invalid credentials";
          break;
        case AuthenticationResponse.INVALID_TIME:
          msg = "Invalid time";
          break;
        case AuthenticationResponse.OTHER:
          // Fall through
        default:
          msg = USER_UNAUTHENTICATED;
          break;
      }
      
      throw new IllegalArgumentException(msg);
    }

    ContactDetails ret;
    if (isReviewContactDetailsEnabled()) {
      ret  = createPendingDetails(user, request);
    } else {
      PersonEntity  entity = updatePerson(user, request);
      ret = converter.convertContactDetails(entity);
    }
    
		String requester = request.getRequester();
		AuditRecord auditRecord = new AuditRecord(USMApplication.USM.name(),
				AuditOperationEnum.UPDATE.getValue(), AuditObjectTypeEnum.CONTACT_DETAILS.getValue(), 
				requester, requester, requester);
		auditLogger.logEvent(auditRecord);

    LOGGER.info("updateContactDetails() - (LEAVE)");
    return ret;
  }

  private PersonEntity updatePerson(UserEntity user, 
                                      ServiceRequest<ContactDetails> request) 
  {
    PersonEntity ret;
    if (user.getPerson() != null) {
      ret = personJpaDao.read(user.getPerson().getPersonId());
      converter.update(ret, request.getBody());
      ret.setModifiedBy(request.getRequester());
      ret.setModifiedOn(new Date());

      ret = personJpaDao.update(ret);
    } else {
      ret = new PersonEntity();
      converter.update(ret, request.getBody());
      ret.setCreatedBy(request.getRequester());
      ret.setCreatedOn(new Date());
      
      user.setPerson(ret);
      userJpaDao.update(user);
    }
    
    return ret;
  }

  @Override
  public boolean isUpdateContactDetailsEnabled()
  {
    return isFeatureEnabled(UPDATE_CONTACT_DETAILS, true);
  }
  
  @Override
  public ContactDetails getContactDetails(ServiceRequest<String> request) 
  throws RuntimeException 
  {
    LOGGER.info("getContactDetails(" + request + ") - (ENTER)");
    
    validator.assertValid(request, null, "userName");
    validator.assertNotEmpty("userName", request.getBody());

    UserEntity user = userJpaDao.read(request.getBody());
    if (user == null) {
      throw new IllegalArgumentException(USER_DOES_NOT_EXIST);
    }
    
    ContactDetails ret;
    if (user.getPerson() != null) {
      PersonEntity entity = personJpaDao.read(user.getPerson().getPersonId());
      ret = converter.convertContactDetails(entity);
    } else {
      ret = new ContactDetails();
    }

    LOGGER.info("getContactDetails() - (LEAVE): " + ret);
    return ret;
  }

  @Override
  public boolean isReviewContactDetailsEnabled() 
  {
    return isFeatureEnabled(REVIEW_CONTACT_DETAILS, false);
  }

  
  @Override
  public List<PendingContactDetails> findPendingContactDetails(ServiceRequest<NoBody> request) 
  throws RuntimeException 
  {
    LOGGER.info("findPendingContactDetails(" + request + ") - (ENTER)");
    
    validator.assertValid(request, USMFeature.manageUsers);
    List<PendingContactDetails> ret = new ArrayList<>();

    List<PendingDetailsEntity> lst = pendingJpaDao.findAll();
    for (PendingDetailsEntity e : lst) {
      ret.add(converter.convertContactDetails(e));
    }
    
    LOGGER.info("findPendingContactDetails() - (LEAVE): " + ret);
    return ret;
  }

  
  @Override
  public PendingContactDetails getPendingContactDetails(ServiceRequest<String> request) 
  throws IllegalArgumentException, RuntimeException 
  {
    LOGGER.info("getPendingContactDetails(" + request + ") - (ENTER)");
    
    validator.assertValid(request, USMFeature.manageUsers, "userName");
    validator.assertNotEmpty("userName", request.getBody());
    
    PendingDetailsEntity pending = pendingJpaDao.read(request.getBody());
    PendingContactDetails ret = converter.convertContactDetails(pending);
    
    LOGGER.info("getPendingContactDetails() - (LEAVE): " + ret);
    return ret;
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public ContactDetails acceptPendingContactDetails(ServiceRequest<String> request) 
  throws IllegalArgumentException, UnauthorisedException, RuntimeException 
  {
    LOGGER.info("accepPendingContactDetails(" + request + ") - (ENTER)");
    
    validator.assertValid(request, USMFeature.manageUsers, "userName");
    validator.assertNotEmpty("userName", request.getBody());
    UserEntity user = userJpaDao.read(request.getBody());
    if (user == null) {
      throw new IllegalArgumentException(USER_DOES_NOT_EXIST);
    }
    PendingDetailsEntity pending = pendingJpaDao.read(request.getBody());
    if (pending == null) {
      throw new IllegalArgumentException(CHANGES_DO_NOT_EXIST);
    }
    
    // Apply accepted update to Person 
    ServiceRequest<ContactDetails> updatePerson = new ServiceRequest<>();
    updatePerson.setRequester(request.getRequester());
    updatePerson.setBody(converter.convertContactDetails(pending));
    PersonEntity person = updatePerson(user, updatePerson);
    ContactDetails ret = converter.convertContactDetails(person);

    // Delete the now accepted pending update
    pendingJpaDao.delete(request.getBody());
    
    LOGGER.info("accepPendingContactDetails() - (LEAVE): " + ret);
    return ret;
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public ContactDetails rejectPendingContactDetails(ServiceRequest<String> request) 
  throws IllegalArgumentException, UnauthorisedException, RuntimeException 
  {
    LOGGER.info("rejectPendingContactDetails(" + request + ") - (ENTER)");
    
    validator.assertValid(request, USMFeature.manageUsers, "userName");
    validator.assertNotEmpty("userName", request.getBody());
    
    pendingJpaDao.delete(request.getBody());
    
    ContactDetails ret = getContactDetails(request);
    
    LOGGER.info("rejectPendingContactDetails() - (LEAVE): " + ret);
    return ret;

  }


  private ContactDetails createPendingDetails(UserEntity user, 
                                                 ServiceRequest<ContactDetails> request) 
  {
    PendingDetailsEntity pending = pendingJpaDao.read(user.getUserName());
    if (pending == null) {
      pending = new PendingDetailsEntity();
      pending.setUserName(user.getUserName());
      pending.setCreatedBy(request.getRequester());
      pending.setCreatedOn(new Date());
    } else {
      pending.setModifiedBy(request.getRequester());
      pending.setModifiedOn(new Date());
    }
    pending.setEMail(request.getBody().getEmail());
    pending.setFaxNumber(request.getBody().getFaxNumber());
    pending.setFirstName(request.getBody().getFirstName());
    pending.setLastName(request.getBody().getLastName());
    pending.setMobileNumber(request.getBody().getMobileNumber());
    pending.setPhoneNumber(request.getBody().getPhoneNumber());
    
    if (pending.getPendingDetailsId() != null) {
      pendingJpaDao.update(pending);
    } else {
      pendingJpaDao.create(pending);
    }
    
    PendingContactDetails ret = converter.convertContactDetails(pending);
    return ret;
  }

  private boolean isFeatureEnabled(String feature, boolean defaultValue)
  {
    LOGGER.info("isFeatureEnabled(" + feature +", " + defaultValue + ") - (ENTER)");
    Boolean ret = defaultValue;
    
    PolicyDefinition def = definition.getDefinition(FEATURE_POLICY);
    if (def != null) {
      Properties props = def.getProperties();
      if (defaultValue) {
        ret = Boolean.parseBoolean(props.getProperty(feature, TRUE));
      } else {
        ret = Boolean.parseBoolean(props.getProperty(feature, FALSE));
      }
    }
    
    LOGGER.info("isFeatureEnabled() - (LEAVE): " + ret);
    return ret;
  }
}