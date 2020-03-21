package eu.europa.ec.mare.usm.information.service.impl;

import eu.europa.ec.mare.usm.information.domain.deployment.Application;
import eu.europa.ec.mare.usm.information.domain.deployment.Dataset;
import eu.europa.ec.mare.usm.information.domain.deployment.Feature;
import eu.europa.ec.mare.usm.information.domain.deployment.Option;
import eu.europa.ec.mare.usm.information.entity.ApplicationEntity;
import eu.europa.ec.mare.usm.information.entity.DatasetEntity;
import eu.europa.ec.mare.usm.information.entity.FeatureEntity;
import eu.europa.ec.mare.usm.information.entity.OptionEntity;
import eu.europa.ec.mare.usm.information.service.DeploymentService;
import eu.europa.ec.mare.usm.policy.service.impl.PolicyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.*;
import java.util.function.Predicate;

/**
 * Stateless session bean implementation of the DeploymentService
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class DeploymentServiceBean implements DeploymentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentServiceBean.class);
    private static final String OPTION_VALUE_POLICY = "option.valueSize";
    private static final String INFORMATION_POLICY = "Information";

    private int optionValuePolicy = 50;

    @EJB
    private ApplicationJpaDao jpaDao;

    @EJB
    private DeploymentValidator validator;

    @EJB
    private PolicyProvider policyProvider;

    @Override
    public void deployApplication(Application request) throws IllegalArgumentException, RuntimeException {
        LOGGER.debug("deployApplication(" + request + ") - (ENTER)");

        validator.assertValid(request, true);

        Properties props = policyProvider.getProperties(INFORMATION_POLICY);
        optionValuePolicy = policyProvider.getIntProperty(props, OPTION_VALUE_POLICY, optionValuePolicy);

        LOGGER.info("Deploy application policy provider " + optionValuePolicy);
        String appName = request.getName();
        ApplicationEntity check = jpaDao.read(appName);
        if (check != null) {
            throw new IllegalArgumentException("Application " + appName + " exists");
        }

        ApplicationEntity entity = convert(request);
        // entity.setCreatedBy(request.getRequester());
        entity.setCreatedOn(new Date());

        jpaDao.create(entity);

        LOGGER.debug("deployApplication() - (LEAVE)");
    }

    @Override
    public void redeployApplication(Application request) throws IllegalArgumentException, RuntimeException {
        LOGGER.debug("redeployApplication(" + request + ") - (ENTER)");

        validator.assertValid(request, true);

        String appName = request.getName();
        ApplicationEntity entity = jpaDao.readApplication(appName);
        if (entity == null) {
            entity = new ApplicationEntity();
            entity.setCreatedOn(new Date());
        } else {
            entity.setModifiedOn(new Date());
        }
        entity = update(entity, request);

        checkForDuplicateDetails(entity);

        if (entity.getApplicationId() == null) {
            jpaDao.create(entity);
        } else {
            ApplicationEntity obsolete = getObsoleteDetails(entity, request);
            jpaDao.update(entity);
            jpaDao.deleteDetails(obsolete);
        }
        LOGGER.debug("redeployApplication() - (LEAVE)");
    }

    @Override
    public void deployDatasets(Application request) throws IllegalArgumentException, RuntimeException {
        LOGGER.debug("deployDatasets(" + request + ") - (ENTER)");

        validator.assertValidDatasets(request);

        ApplicationEntity entity = jpaDao.readApplication(request.getName());
        if (entity == null) {
            throw new IllegalArgumentException("Application " + request.getName() + " does not exist");
        }
        entity.setModifiedOn(new Date());

        updateDatasets(entity, request);

        checkForDuplicateDetails(entity);

        jpaDao.update(entity);

        LOGGER.debug("deployDatasets() - (LEAVE)");
    }

    @Override
    public void undeployApplication(String appName) throws IllegalArgumentException, RuntimeException {
        LOGGER.debug("undeployApplication(" + appName + ") - (ENTER)");

        validator.assertValidApplication(appName);

        ApplicationEntity entity = jpaDao.read(appName);
        if (entity == null) {
            throw new IllegalArgumentException("Application " + appName +
                    " does not exist");
        } else if ("USM".equals(appName)) {
            throw new IllegalArgumentException(appName + " may not be undeployed");
        } else if (!entity.getChildApplicationList().isEmpty()) {
            throw new IllegalArgumentException(appName + " may not be undeployed as it has " +
                    "children");
        }
        jpaDao.delete(appName);

        LOGGER.debug("undeployApplication() - (LEAVE)");
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Application getDeploymentDescriptor(String appName) throws IllegalArgumentException, RuntimeException {
        LOGGER.debug("getDeploymentDescriptor(" + appName + ") - (ENTER)");

        validator.assertValidApplication(appName);

        Application ret = null;

        ApplicationEntity entity = jpaDao.readApplication(appName);
        if (entity != null) {
            ret = convert(entity);
        }

        LOGGER.debug("getDeploymentDescriptor() - (LEAVE)");
        return ret;
    }

    private ApplicationEntity getObsoleteDetails(ApplicationEntity entity, Application src) {
        ApplicationEntity ret = new ApplicationEntity();

        if (src.isRetainDatasets() == null || !src.isRetainDatasets()) {
            if (entity.getDatasetList() != null) {
                ret.setDatasetList(new ArrayList<>());
                for (DatasetEntity e : entity.getDatasetList()) {
                    Predicate<Dataset> filter = ds -> ds.getName().equalsIgnoreCase(e.getName());
                    if (e.getDatasetId() != null && findInList(src.getDataset(), filter) == null) {
                        DatasetEntity n = new DatasetEntity();
                        n.setDatasetId(e.getDatasetId());
                        ret.getDatasetList().add(n);
                    }
                }
            }
        }

        if (entity.getFeatureList() != null) {
            ret.setFeatureList(new ArrayList<>());
            for (FeatureEntity e : entity.getFeatureList()) {
                Predicate<Feature> filter = f -> f.getName().equalsIgnoreCase(e.getName());
                if (e.getFeatureId() != null && findInList(src.getFeature(), filter) == null) {
                    FeatureEntity n = new FeatureEntity();
                    n.setFeatureId(e.getFeatureId());
                    ret.getFeatureList().add(n);
                }
            }
        }

        if (entity.getOptionList() != null) {
            ret.setOptionList(new ArrayList<>());
            for (OptionEntity e : entity.getOptionList()) {
                Predicate<Option> filter = o -> o.getName().equalsIgnoreCase(e.getName());
                if (e.getOptionId() != null && findInList(src.getOption(), filter) == null) {
                    OptionEntity n = new OptionEntity();
                    n.setOptionId(e.getOptionId());
                    ret.getOptionList().add(n);
                }
            }
        }

        return ret;
    }

    private void checkForDuplicateDetails(ApplicationEntity src) {
        if (src.getDatasetList() != null) {
            Set<String> set = new HashSet<>();

            for (int i = 0; i < src.getDatasetList().size(); i++) {
                DatasetEntity item = src.getDatasetList().get(i);
                if (set.contains(item.getName())) {
                    throw new IllegalArgumentException("Dataset " + item.getName() + " already defined");
                } else {
                    set.add(item.getName());
                }
            }
        }

        if (src.getFeatureList() != null) {
            Set<String> set = new HashSet<>();

            for (int i = 0; i < src.getFeatureList().size(); i++) {
                FeatureEntity item = src.getFeatureList().get(i);
                if (set.contains(item.getName())) {
                    throw new IllegalArgumentException("Feature " + item.getName() + " already defined");
                } else {
                    set.add(item.getName());
                }
            }
        }

        if (src.getOptionList() != null) {
            Set<String> set = new HashSet<>();

            for (int i = 0; i < src.getOptionList().size(); i++) {
                OptionEntity item = src.getOptionList().get(i);
                if (set.contains(item.getName())) {
                    throw new IllegalArgumentException("Option " + item.getName() + " already defined");
                } else {
                    set.add(item.getName());
                }
            }
        }
    }

    private Application convert(ApplicationEntity src) {
        Application ret = null;

        if (src != null) {
            ret = new Application();
            ret.setName(src.getName());
            if (src.getParentApplication() != null) {
                ret.setParent(src.getParentApplication().getName());
            }
            ret.setDescription(src.getDescription());
            if (src.getFeatureList() != null) {
                for (FeatureEntity fe : src.getFeatureList()) {
                    Feature f = new Feature();

                    f.setName(fe.getName());
                    f.setDescription(fe.getDescription());
                    f.setGroup(fe.getGroupName());
                    ret.getFeature().add(f);
                }
            }
            if (src.getDatasetList() != null) {
                for (DatasetEntity de : src.getDatasetList()) {
                    Dataset d = new Dataset();

                    d.setName(de.getName());
                    d.setDescription(de.getDescription());
                    d.setCategory(de.getCategory());
                    d.setDiscriminator(de.getDiscriminator());

                    ret.getDataset().add(d);
                }
            }
            if (src.getOptionList() != null) {
                for (OptionEntity oe : src.getOptionList()) {
                    Option o = new Option();

                    o.setName(oe.getName());
                    o.setDescription(oe.getDescription());
                    o.setDataType(oe.getDataType());
                    o.setDefaultValue(oe.getDefaultValue() != null ? new String(oe.getDefaultValue()) : null);

                    ret.getOption().add(o);
                }
            }
        }

        return ret;
    }

    private ApplicationEntity convert(Application src) {
        ApplicationEntity ret = null;

        if (src != null) {
            ret = update(new ApplicationEntity(), src);
        }

        return ret;
    }

    private ApplicationEntity update(ApplicationEntity target, Application source) {
        target.setName(source.getName());
        target.setDescription(source.getDescription());

        String parentName = source.getParent();
        if (parentName != null) {
            ApplicationEntity parentEntity = jpaDao.readApplication(parentName);
            target.setParentApplication(parentEntity);
        }

        updateFeatures(target, source);
        updateDatasets(target, source);
        updateOptions(target, source);

        return target;
    }

    private void updateFeatures(ApplicationEntity ret, Application src) {
        if (!src.getFeature().isEmpty()) {
            List<FeatureEntity> lst = ret.getFeatureList();
            ret.setFeatureList(new ArrayList<>());
            if (lst != null && !lst.isEmpty()) {
                ret.getFeatureList().addAll(lst);
            }

            for (Feature fe : src.getFeature()) {
                Predicate<FeatureEntity> filter = f -> f.getName().equalsIgnoreCase(fe.getName());
                FeatureEntity f = findInList(ret.getFeatureList(), filter);
                if (f == null) {
                    f = new FeatureEntity();
                    f.setName(fe.getName());
                    ret.getFeatureList().add(f);
                }
                f.setDescription(fe.getDescription());
                f.setGroupName(fe.getGroup());
            }
        }
    }

    private void updateOptions(ApplicationEntity ret, Application src) {
        if (!src.getOption().isEmpty()) {
            List<OptionEntity> lst = ret.getOptionList();
            ret.setOptionList(new ArrayList<>());
            if (lst != null && !lst.isEmpty()) {
                ret.getOptionList().addAll(lst);
            }

            for (Option oe : src.getOption()) {
                if (oe.getDefaultValue() != null && oe.getDefaultValue().getBytes().length >= optionValuePolicy * 1024) {
                    throw new IllegalArgumentException("The value of the option " + oe.getName() + " is greater than the accepted limit " + optionValuePolicy);
                }

                Predicate<OptionEntity> filter = o -> o.getName().equalsIgnoreCase(oe.getName());
                OptionEntity o = findInList(ret.getOptionList(), filter);
                if (o == null) {
                    o = new OptionEntity();
                    o.setName(oe.getName());
                    ret.getOptionList().add(o);
                }
                o.setDescription(oe.getDescription());
                o.setDataType(oe.getDataType());
                o.setDefaultValue(oe.getDefaultValue() != null ? oe.getDefaultValue().getBytes() : null);
                o.setGroupName(oe.getGroup());
            }
        }
    }

    private void updateDatasets(ApplicationEntity ret, Application src) {
        if (!src.getDataset().isEmpty()) {
            List<DatasetEntity> lst = ret.getDatasetList();
            ret.setDatasetList(new ArrayList<>());
            if (lst != null && !lst.isEmpty()) {
                ret.getDatasetList().addAll(lst);
            }

            for (Dataset de : src.getDataset()) {
                Predicate<DatasetEntity> filter = ds -> ds.getName().equalsIgnoreCase(de.getName());
                DatasetEntity d = findInList(ret.getDatasetList(), filter);
                if (d == null) {
                    d = new DatasetEntity();
                    d.setName(de.getName());
                    ret.getDatasetList().add(d);
                }
                d.setDescription(de.getDescription());
                d.setCategory(de.getCategory());
                d.setDiscriminator(de.getDiscriminator());
            }
        }
    }

    private <T> T findInList(List<T> list, Predicate<T> predicate) {
        return list.stream().filter(predicate).findFirst().orElse(null);
    }

}
