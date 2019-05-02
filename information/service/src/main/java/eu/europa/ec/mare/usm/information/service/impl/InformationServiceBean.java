package eu.europa.ec.mare.usm.information.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.information.domain.ContactDetails;
import eu.europa.ec.mare.usm.information.domain.Context;
import eu.europa.ec.mare.usm.information.domain.DataSet;
import eu.europa.ec.mare.usm.information.domain.DataSetFilter;
import eu.europa.ec.mare.usm.information.domain.Feature;
import eu.europa.ec.mare.usm.information.domain.Organisation;
import eu.europa.ec.mare.usm.information.domain.Preference;
import eu.europa.ec.mare.usm.information.domain.Preferences;
import eu.europa.ec.mare.usm.information.domain.UserContext;
import eu.europa.ec.mare.usm.information.domain.UserContextQuery;
import eu.europa.ec.mare.usm.information.domain.UserPreference;
import eu.europa.ec.mare.usm.information.entity.DatasetEntity;
import eu.europa.ec.mare.usm.information.entity.PreferenceEntity;
import eu.europa.ec.mare.usm.information.service.InformationService;

/**
 * Stateless session bean implementation of the InformationService
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class InformationServiceBean implements InformationService {
  private static final Logger LOGGER = LoggerFactory.getLogger(InformationServiceBean.class.getName());

  @EJB
  private InformationDao dao;
  
  @EJB 
  private InformationJpaDao informationJpaDao;
  
  @EJB
  private DataSetJpaDao dataSetDao;
  
  
  @Override
  public ContactDetails getContactDetails(String userName) 
  throws IllegalArgumentException, RuntimeException 
  {
    LOGGER.info("getContactDetails(" + userName + ") - (ENTER)");
    
    assertNotEmpty("userName", userName);
    ContactDetails ret = dao.getContactDetails(userName);
    
    LOGGER.info("getContactDetails() - (LEAVE)");
    return ret;
  }


  @Override
  public List<Organisation> findOrganisations(String nation) 
  throws IllegalArgumentException, RuntimeException 
  {
    LOGGER.info("findOrganisations(" + nation + ") - (ENTER)");
    
    assertNotEmpty("nation", nation);
    List<Organisation> ret = dao.findOrganisations(nation);
    
    LOGGER.info("findOrganisations() - (LEAVE)");
    return ret;
  }
  
  @Override
  public Organisation getOrganisation(String organisationName) 
  throws IllegalArgumentException, RuntimeException 
  {
    LOGGER.info("getOrganisation(" + organisationName + ") - (ENTER)");
    
    assertNotEmpty("organisationName", organisationName);
    Organisation ret = dao.getOrganisation(organisationName);
    
    LOGGER.info("getOrganisation() - (LEAVE)");
    return ret;
  }

  @Override
  public UserContext getUserContext(UserContextQuery query) 
  throws IllegalArgumentException, RuntimeException 
  {
    LOGGER.info("getUserContext(" + query + ") - (ENTER)");
    
    assertValid(query);
    UserContext ret = dao.getUserContext(query);
    
    LOGGER.info("getUserContext() - (LEAVE)");
    return ret;
  }

    @Override
    public List<String> getUserFeatures(String username) {
        Set<Feature> features = dao.getUserFeatures(username);
        return features.stream().map(Feature::getFeatureName).collect(Collectors.toList());
    }

  @Override
  public void updateUserPreferences(UserContext userContext) 
  throws IllegalArgumentException, RuntimeException 
  {
    LOGGER.info("updateUserContext(" + userContext + ") - (ENTER)");
    
    // Validate input
    assertValid(userContext);
    
    // Execute
    if (userContext.getContextSet() != null && 
        userContext.getContextSet().getContexts() != null) {
      for (Context ctx : userContext.getContextSet().getContexts()) {
        dao.updateUserContext(userContext.getUserName(), ctx);
      }
    }
    
    LOGGER.info("updateUserContext() - (LEAVE)");
  }
  
  
  @Override
  public void createUserPreference(UserPreference userPreference){
	  LOGGER.info("createUserPreference(" + userPreference + ") - (ENTER)");
	  informationJpaDao.createUserPreference(userPreference);	  
	  LOGGER.info("createUserPreference() - (LEAVE)");
  }
  
  @Override
  public void updateUserPreference(UserPreference userPreference){
	  LOGGER.info("updateUserPreference(" + userPreference + ") - (ENTER)");
	  informationJpaDao.updateUserPreference(userPreference);	  
	  LOGGER.info("updateUserPreference() - (LEAVE)");
  }
  
  @Override
  public void deleteUserPreference(UserPreference userPreference){
	  LOGGER.info("deleteUserPreference(" + userPreference + ") - (ENTER)");
	  informationJpaDao.deleteUserPreference(userPreference);	  
	  LOGGER.info("deleteUserPreference() - (LEAVE)");
  }
  
  @Override
  public UserPreference getUserPreference(UserPreference userPreference){	   
	  LOGGER.info("getUserPreference(" + userPreference + ") - (ENTER)");
	  PreferenceEntity entity = informationJpaDao.readUserPreference(userPreference);
	  UserPreference preference = convertUserPreference(entity);	  		
	  LOGGER.info("getUserPreference() - (LEAVE)");	  
      return preference;
  }
  
  
  private UserPreference convertUserPreference(PreferenceEntity entity) {
	  UserPreference preference = new UserPreference();	  
		if (entity != null) {
			preference.setApplicationName(entity.getOption().getApplication().getName());
			preference.setRoleName(entity.getUserContext().getRole().getName());
			if (entity.getUserContext().getScope() != null) {
				preference.setScopeName(entity.getUserContext().getScope().getName());
			}
			preference.setUserName(entity.getUserContext().getUser().getUserName());
			preference.setOptionName(entity.getOption().getName());
			preference.setOptionValue(entity.getOptionValue());
		}
	return preference;
}


@Override
  public void createDataSet(DataSet dataSet){
	  LOGGER.info("createDataSet(" + dataSet + ") - (ENTER)");
	  dataSetDao.createDataSet(dataSet);	  
	  LOGGER.info("createDataSet() - (LEAVE)");
  }
  
  @Override
  public void updateDataSet(DataSet dataSet){
	  LOGGER.info("updateDataSet(" + dataSet + ") - (ENTER)");
	  dataSetDao.updateDataSet(dataSet);	  
	  LOGGER.info("updateDataSet() - (LEAVE)");
  }
  
	@Override
	public void deleteDataSet(DataSet dataSet) {
		LOGGER.info("deleteDataSet(" + dataSet + ") - (ENTER)");
		dataSetDao.deleteDataSet(dataSet);
		LOGGER.info("deleteDataSet() - (LEAVE)");
	}

	@Override
	public DataSet getDataSet(String name, String applicationName) {
		LOGGER.info("deleteDataSet(" + name + ", " + applicationName + " ) - (ENTER)");
		DatasetEntity entity = dataSetDao.findDataSetByNameAndApplication(name, applicationName);
		LOGGER.info("deleteDataSet() - (LEAVE)");
		return convertDataSet(entity);
	}

	@Override
	public List<DataSet> getDataSets(DataSetFilter dataSetFilter) {
		LOGGER.info("getDataSet(" + dataSetFilter.getName() + ", " + dataSetFilter.getApplicationName() + " ) - (ENTER)");
		List<DatasetEntity> entity = dataSetDao.findDataSets(dataSetFilter);
		LOGGER.info("getDataSet() - (LEAVE)");
		return convertDataSets(entity);
	}
  
  
	private List<DataSet> convertDataSets(List<DatasetEntity> entityList) {
		List<DataSet> result = new ArrayList<>();
		for (DatasetEntity entity : entityList) {
			result.add(convertDataSet(entity));
		}
		return result;
	}

	private DataSet convertDataSet(DatasetEntity entity) {
		if (entity != null) {
			DataSet dataSet = new DataSet();
			dataSet.setName(entity.getName());
			dataSet.setApplicationName(entity.getApplication().getName());
			dataSet.setCategory(entity.getCategory());
			dataSet.setDescription(entity.getDescription());
			dataSet.setDiscriminator(entity.getDiscriminator());
			return dataSet;
		}
		return null;
	}


private void assertValid(UserContextQuery query) 
  {
    assertNotNull("query", query);
    assertNotEmpty("query.userName", query.getUserName());
  }

  private void assertValid(UserContext userContext) 
  {
    assertNotNull("userContext", userContext);
    assertNotEmpty("userContext.userName", userContext.getUserName());
    if (!dao.userExists(userContext.getUserName())) {
      throw new IllegalArgumentException("User does not exist");
    }
    if (userContext.getContextSet() != null && 
        userContext.getContextSet().getContexts() != null) {
      for (Context ctx : userContext.getContextSet().getContexts()) {
        assertValid(userContext.getUserName(), ctx);
      }
    }
  }

  private void assertValid(String userName, Context ctx) 
  {
    assertNotNull("role", ctx.getRole());
    assertNotEmpty("role.name", ctx.getRole().getRoleName());
    if(ctx.getScope() != null) {
      assertNotEmpty("scope.name", ctx.getScope().getScopeName());
    }
    if (!dao.userContextExists(userName, ctx)) {
      throw new IllegalArgumentException("User context " + userName + "/" + 
                                          ctx.getRole().getRoleName() + "/" +
                                          (ctx.getScope() == null ? "" : 
                                               ctx.getScope().getScopeName()) + 
                                        " does not exist");
    }
    if (ctx.getPreferences() != null) {
      assertValid(ctx.getPreferences());
    }
  }

  private void assertValid(Preferences preferences) 
  {
    if (preferences.getPreferences() != null) {
      for (Preference p : preferences.getPreferences()) {
        assertNotEmpty("preference.applicationName", p.getApplicationName());
        assertNotEmpty("preference.optionName", p.getOptionName());
      //  assertNotEmpty("preference.optionValue", p.getOptionValue());
        if (!dao.optionExists(p.getApplicationName(), p.getOptionName())) {
          throw new IllegalArgumentException("Option " + p.getApplicationName() + 
                                             "/" + p.getOptionName() + 
                                             " does not exist");
        }

      }
    }
  }

  private void assertNotEmpty(String name, String value) 
  {
    assertNotNull(name, value);
    if (value.trim().length() == 0) {
      throw new IllegalArgumentException(name + " must be defined");
    }
  }
  
  private void assertNotNull(String name, Object value) 
  {
    if (value == null) {
      throw new IllegalArgumentException(name + " must be defined");
    }
  }

}
