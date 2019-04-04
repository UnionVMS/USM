package eu.europa.ec.mare.usm.information.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.information.domain.UserPreference;
import eu.europa.ec.mare.usm.information.entity.ApplicationEntity;
import eu.europa.ec.mare.usm.information.entity.OptionEntity;
import eu.europa.ec.mare.usm.information.entity.PreferenceEntity;
import eu.europa.ec.mare.usm.information.entity.RoleEntity;
import eu.europa.ec.mare.usm.information.entity.ScopeEntity;
import eu.europa.ec.mare.usm.information.entity.UserContextEntity;
import eu.europa.ec.mare.usm.information.entity.UserEntity;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class InformationJpaDao {
	private static final Logger LOGGER = LoggerFactory.getLogger(InformationJpaDao.class);

	private static final String PREFERENCE_ALREADY_EXISTS = "Preference already exists";
	private static final String USER_NOT_EXISTS = "The user with the specified name, scope and role does not exist";
	private static final String PREFERENCE_NOT_EXISTS = "Preference does not exists";
	

	@EJB
	ApplicationJpaDao applicationJpaDao;
	
	@PersistenceContext(unitName = "USM-Administration")
	private EntityManager em;

  /**
   * Creates a new instance
   */
  public InformationJpaDao() {
  }


  
  private void handleException(String operation, Exception ex) 
  throws RuntimeException 
  {
    String msg = "Error during " + operation + " operation: " + ex.getMessage();

    LOGGER.error(msg, ex);
    throw new RuntimeException(msg, ex);
  }

   
	public PreferenceEntity readUserPreference(UserPreference userPreference) {
		LOGGER.info("readUserPreference(" + userPreference + ") - (ENTER)");
		PreferenceEntity entity = null;
		try {
			
			LOGGER.debug(">>>>>>>>>>>>>>>>>>>>>>>readUserPreference begin select ");
			
			CriteriaBuilder builder= em.getCriteriaBuilder();
			CriteriaQuery<PreferenceEntity> criteriaQuery =builder.createQuery(PreferenceEntity.class);
			Root<PreferenceEntity> preference = criteriaQuery.from(PreferenceEntity.class);
			Join<PreferenceEntity, UserContextEntity> userContext = (Join)preference.fetch("userContext");
			Join<UserContextEntity, UserEntity> user = (Join)userContext.fetch("user");			
			Join<UserContextEntity, RoleEntity> role = (Join)userContext.fetch("role");			
			Join<PreferenceEntity, OptionEntity> option = (Join)preference.fetch("option");
			Join<OptionEntity, ApplicationEntity> application = (Join)option.fetch("application");
			
			List<Predicate> conditions = new ArrayList<Predicate>();
			conditions.add(builder.equal(user.get("userName"), userPreference.getUserName()));
			conditions.add(builder.equal(role.get("name"), userPreference.getRoleName()));
			conditions.add(builder.equal(option.get("name"), userPreference.getOptionName()));
			conditions.add(builder.equal(application.get("name"), userPreference.getApplicationName()));
			
			if ( userPreference.getScopeName() != null && userPreference.getScopeName().length() > 0 ) {
				Join<UserContextEntity, ScopeEntity> scope = (Join)userContext.fetch("scope");
				conditions.add(builder.equal(scope.get("name"), userPreference.getScopeName()));
			}
			
			TypedQuery<PreferenceEntity> typedQuery = em.createQuery(criteriaQuery			
					.select(preference)
			        .where(conditions.toArray(new Predicate[] {}))
			        //.distinct(true)
			);
			entity = typedQuery.getSingleResult();
			
			LOGGER.debug(">>>>>>>>>>>>>>>>>>>>>>>readUserPreference end select ");
			
		} catch (NoResultException ex) {
			LOGGER.info("userPreference " + userPreference + " not found", ex);
		} catch (Exception ex) {
			handleException("readUserPreference", ex);
		}
		LOGGER.info("readUserPreference() - (LEAVE): " + entity);
		return entity;
	}

	public void updateUserPreference(UserPreference userPreference) {
		LOGGER.info("updateUserPreference(" + userPreference + ") - (ENTER)");
		try {
			PreferenceEntity entity = readUserPreference(userPreference);
			LOGGER.info("---> entity : " + entity) ;
			if (entity == null) {
				throw new IllegalArgumentException(PREFERENCE_NOT_EXISTS);
			}
			entity.setOptionValue(userPreference.getOptionValue());
			em.merge(entity);
			em.flush();
		} catch (Exception ex) {
			handleException("updateUserPreference", ex);
		}
		LOGGER.info("updateUserPreference() - (LEAVE): ");
	}



	public void createUserPreference(UserPreference userPreference) {
		LOGGER.info("createUserPreference(" + userPreference + ") - (ENTER)");
		try {
			PreferenceEntity entity = readUserPreference(userPreference);
			LOGGER.info("---> entity : " + entity) ;
			if (entity != null) {
				throw new IllegalArgumentException(PREFERENCE_ALREADY_EXISTS);
			}

			entity = new PreferenceEntity();

			UserContextEntity contextEntity = getUserContextByScopeAndRole(userPreference.getUserName(),
					userPreference.getScopeName(), userPreference.getRoleName());
			if (contextEntity == null) {
				throw new IllegalArgumentException(USER_NOT_EXISTS);
			}

			entity.setUserContext(contextEntity);

			ApplicationEntity applicationEntity = applicationJpaDao
					.readApplication(userPreference.getApplicationName());
			List<OptionEntity> optionEntities = applicationEntity.getOptionList();
			for (OptionEntity optionEntity : optionEntities) {
				if (optionEntity.getName().equalsIgnoreCase(userPreference.getOptionName())) {
					entity.setOption(optionEntity);
				}
			}

			entity.setOptionValue(userPreference.getOptionValue());

			em.persist(entity);
			em.flush();
			

		} catch (Exception ex) {
			handleException("createUserPreference", ex);
		}
		LOGGER.info("createUserPreference() - (LEAVE): ");
	}
	
	  private UserContextEntity getUserContextByScopeAndRole(String userName, String scopeName, String roleName){
		  try {

			LOGGER.debug(">>>>>>>>>>>>>>>>>>>>>>>getUserContextByScopeAndRole begin select ");
		    
			CriteriaBuilder builder= em.getCriteriaBuilder();
			CriteriaQuery<UserContextEntity> criteriaQuery =builder.createQuery(UserContextEntity.class);
			Root<UserContextEntity> userContext = criteriaQuery.from(UserContextEntity.class); 
			Join<UserContextEntity, UserEntity> user = (Join)userContext.fetch("user");
			Join<UserContextEntity, RoleEntity> role = (Join)userContext.fetch("role");
			
			List<Predicate> conditions = new ArrayList<Predicate>();
			conditions.add(builder.equal(user.get("userName"), userName));
			conditions.add(builder.equal(role.get("name"), roleName));
			
			if (scopeName != null && scopeName.length() > 0) {
				Join<UserContextEntity, ScopeEntity> scope = (Join)userContext.fetch("scope");
				conditions.add(builder.equal(scope.get("name"), scopeName));
			}
			
			TypedQuery<UserContextEntity> typedQuery = em.createQuery(criteriaQuery
					.select(userContext) 
			        .where(conditions.toArray(new Predicate[] {}))
			        .distinct(true)
			);
			UserContextEntity entity = typedQuery.getSingleResult();
			
		    LOGGER.debug(">>>>>>>>>>>>>>>>>>>>>>>getUserContextByScopeAndRole end select ");
		    
		    return entity;
		    
		  } catch (Exception ex) {
		      handleException("getUserContextByScopeAndRole", ex);
		    }
		  return null;
	  }
	  
	  
	public void deleteUserPreference(UserPreference userPreference) {
		LOGGER.info("deleteUserPreference(" + userPreference + ") - (ENTER)");

		try {
			PreferenceEntity entity = readUserPreference(userPreference);
			LOGGER.info("---> entity : " + entity) ;
			if (entity == null) {
				throw new IllegalArgumentException(PREFERENCE_NOT_EXISTS);
			}
			em.remove(entity);
			em.flush();
		} catch (Exception ex) {
			handleException("deleteUserPreference", ex);
		}

		LOGGER.info("deleteUserPreference() - (LEAVE)");
	}



	

}
