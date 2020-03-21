package eu.europa.ec.mare.usm.information.service.impl;

import eu.europa.ec.mare.usm.information.domain.*;
import eu.europa.ec.mare.usm.information.entity.DatasetEntity;
import eu.europa.ec.mare.usm.information.entity.PreferenceEntity;
import eu.europa.ec.mare.usm.information.service.InformationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public ContactDetails getContactDetails(String userName) throws IllegalArgumentException, RuntimeException {
        LOGGER.debug("getContactDetails(" + userName + ") - (ENTER)");

        assertNotEmpty("userName", userName);
        ContactDetails ret = dao.getContactDetails(userName);

        LOGGER.debug("getContactDetails() - (LEAVE)");
        return ret;
    }

    @Override
    public List<Organisation> findOrganisations(String nation) throws IllegalArgumentException, RuntimeException {
        LOGGER.debug("findOrganisations(" + nation + ") - (ENTER)");

        assertNotEmpty("nation", nation);
        List<Organisation> ret = dao.findOrganisations(nation);

        LOGGER.debug("findOrganisations() - (LEAVE)");
        return ret;
    }

    @Override
    public Organisation getOrganisation(String organisationName) throws IllegalArgumentException, RuntimeException {
        LOGGER.debug("getOrganisation(" + organisationName + ") - (ENTER)");

        assertNotEmpty("organisationName", organisationName);
        Organisation ret = dao.getOrganisation(organisationName);

        LOGGER.debug("getOrganisation() - (LEAVE)");
        return ret;
    }

    @Override
    public UserContext getUserContext(UserContextQuery query) throws IllegalArgumentException, RuntimeException {
        LOGGER.debug("getUserContext(" + query + ") - (ENTER)");

        assertValid(query);
        UserContext ret = dao.getUserContext(query);

        LOGGER.debug("getUserContext() - (LEAVE)");
        return ret;
    }

    @Override
    public List<Integer> getUserFeatures(String username) {
        Set<Feature> features = dao.getUserFeatures(username);
        return features.stream().map(Feature::getFeatureId).collect(Collectors.toList());
    }

    @Override
    public void updateUserPreferences(UserContext userContext) throws IllegalArgumentException, RuntimeException {
        LOGGER.debug("updateUserContext(" + userContext + ") - (ENTER)");

        // Validate input
        assertValid(userContext);

        // Execute
        if (userContext.getContextSet() != null &&
                userContext.getContextSet().getContexts() != null) {
            for (Context ctx : userContext.getContextSet().getContexts()) {
                dao.updateUserContext(userContext.getUserName(), ctx);
            }
        }

        LOGGER.debug("updateUserContext() - (LEAVE)");
    }

    @Override
    public void createUserPreference(UserPreference userPreference) {
        LOGGER.debug("createUserPreference(" + userPreference + ") - (ENTER)");
        informationJpaDao.createUserPreference(userPreference);
        LOGGER.debug("createUserPreference() - (LEAVE)");
    }

    @Override
    public void updateUserPreference(UserPreference userPreference) {
        LOGGER.debug("updateUserPreference(" + userPreference + ") - (ENTER)");
        informationJpaDao.updateUserPreference(userPreference);
        LOGGER.debug("updateUserPreference() - (LEAVE)");
    }

    @Override
    public void deleteUserPreference(UserPreference userPreference) {
        LOGGER.debug("deleteUserPreference(" + userPreference + ") - (ENTER)");
        informationJpaDao.deleteUserPreference(userPreference);
        LOGGER.debug("deleteUserPreference() - (LEAVE)");
    }

    @Override
    public UserPreference getUserPreference(UserPreference userPreference) {
        LOGGER.debug("getUserPreference(" + userPreference + ") - (ENTER)");
        PreferenceEntity entity = informationJpaDao.readUserPreference(userPreference);
        UserPreference preference = convertUserPreference(entity);
        LOGGER.debug("getUserPreference() - (LEAVE)");
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
    public void createDataSet(DataSet dataSet) {
        LOGGER.debug("createDataSet(" + dataSet + ") - (ENTER)");
        dataSetDao.createDataSet(dataSet);
        LOGGER.debug("createDataSet() - (LEAVE)");
    }

    @Override
    public void updateDataSet(DataSet dataSet) {
        LOGGER.debug("updateDataSet(" + dataSet + ") - (ENTER)");
        dataSetDao.updateDataSet(dataSet);
        LOGGER.debug("updateDataSet() - (LEAVE)");
    }

    @Override
    public void deleteDataSet(DataSet dataSet) {
        LOGGER.debug("deleteDataSet(" + dataSet + ") - (ENTER)");
        dataSetDao.deleteDataSet(dataSet);
        LOGGER.debug("deleteDataSet() - (LEAVE)");
    }

    @Override
    public DataSet getDataSet(String name, String applicationName) {
        LOGGER.debug("deleteDataSet(" + name + ", " + applicationName + " ) - (ENTER)");
        DatasetEntity entity = dataSetDao.findDataSetByNameAndApplication(name, applicationName);
        LOGGER.debug("deleteDataSet() - (LEAVE)");
        return convertDataSet(entity);
    }

    @Override
    public List<DataSet> getDataSets(DataSetFilter dataSetFilter) {
        LOGGER.debug("getDataSet(" + dataSetFilter.getName() + ", " + dataSetFilter.getApplicationName() + " ) - (ENTER)");
        List<DatasetEntity> entity = dataSetDao.findDataSets(dataSetFilter);
        LOGGER.debug("getDataSet() - (LEAVE)");
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

    private void assertValid(UserContextQuery query) {
        assertNotNull("query", query);
        assertNotEmpty("query.userName", query.getUserName());
    }

    private void assertValid(UserContext userContext) {
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

    private void assertValid(String userName, Context ctx) {
        assertNotNull("role", ctx.getRole());
        assertNotEmpty("role.name", ctx.getRole().getRoleName());
        if (ctx.getScope() != null) {
            assertNotEmpty("scope.name", ctx.getScope().getScopeName());
        }
        if (!dao.userContextExists(userName, ctx)) {
            throw new IllegalArgumentException("User context " + userName + "/" +
                    ctx.getRole().getRoleName() + "/" +
                    (ctx.getScope() == null ? "" : ctx.getScope().getScopeName()) + " does not exist");
        }
        if (ctx.getPreferences() != null) {
            assertValid(ctx.getPreferences());
        }
    }

    private void assertValid(Preferences preferences) {
        if (preferences.getPreferences() != null) {
            for (Preference p : preferences.getPreferences()) {
                assertNotEmpty("preference.applicationName", p.getApplicationName());
                assertNotEmpty("preference.optionName", p.getOptionName());
                if (!dao.optionExists(p.getApplicationName(), p.getOptionName())) {
                    throw new IllegalArgumentException("Option " + p.getApplicationName() +
                            "/" + p.getOptionName() + " does not exist");
                }
            }
        }
    }

    private void assertNotEmpty(String name, String value) {
        assertNotNull(name, value);
        if (value.trim().length() == 0) {
            throw new IllegalArgumentException(name + " must be defined");
        }
    }

    private void assertNotNull(String name, Object value) {
        if (value == null) {
            throw new IllegalArgumentException(name + " must be defined");
        }
    }

}
