package eu.europa.ec.mare.usm.information.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * Stateless session bean implementation of the DeploymentService
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class DeploymentServiceBean implements DeploymentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentServiceBean.class);
    private static final String OPTION_VALUE_POLICY="option.valueSize";
    private static final String INFORMATION_POLICY="Information";
    
    private int optionValuePolicy=50;
    
    @EJB
    private ApplicationJpaDao jpaDao;

    @EJB
    private DeploymentValidator validator;
    
    @EJB
    private PolicyProvider policyProvider;

    @Override
    public void deployApplication(Application request)
            throws IllegalArgumentException, RuntimeException {
        LOGGER.info("deployApplication(" + request + ") - (ENTER)");

        validator.assertValid(request,  true);

        Properties props = policyProvider.getProperties(INFORMATION_POLICY);
        optionValuePolicy = policyProvider.getIntProperty(props, OPTION_VALUE_POLICY, optionValuePolicy);
     
        LOGGER.info("Deploy application policy provider "+optionValuePolicy);
        String appName = request.getName();
        ApplicationEntity check = jpaDao.read(appName);
        if (check != null) {
            throw new IllegalArgumentException("Application " + appName + " exists");
        }

        ApplicationEntity entity = convert(request);
       // entity.setCreatedBy(request.getRequester());
        entity.setCreatedOn(new Date());

        jpaDao.create(entity);

        LOGGER.info("deployApplication() - (LEAVE)");
    }

    @Override
    public void redeployApplication(Application request)
            throws IllegalArgumentException,  RuntimeException {
        LOGGER.info("redeployApplication(" + request + ") - (ENTER)");

        validator.assertValid(request,  true);

        String appName = request.getName();
        ApplicationEntity entity = jpaDao.readApplication(appName);
        if (entity == null) {
            entity = new ApplicationEntity();
           // entity.setCreatedBy(request.getRequester());
            entity.setCreatedOn(new Date());
        } else {
          //  entity.setModifiedBy(request.getRequester());
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

        LOGGER.info("redeployApplication() - (LEAVE)");
    }

    @Override
    public void deployDatasets(Application request)
            throws IllegalArgumentException, RuntimeException {
        LOGGER.info("deployDatasets(" + request + ") - (ENTER)");

        validator.assertValidDatasets(request);

       
        ApplicationEntity entity = jpaDao.readApplication(request.getName());
        if (entity == null) {
            throw new IllegalArgumentException("Application " + request.getName() +
                    " does not exist");
        }
       // entity.setModifiedBy(request.getRequester());
        entity.setModifiedOn(new Date());

        entity = updateDatasets(entity, request);

        checkForDuplicateDetails(entity);

        jpaDao.update(entity);

        LOGGER.info("deployDatasets() - (LEAVE)");
    }

    @Override
    public void undeployApplication(String appName)
            throws IllegalArgumentException, RuntimeException {
        LOGGER.info("undeployApplication(" + appName + ") - (ENTER)");

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

        LOGGER.info("undeployApplication() - (LEAVE)");
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Application getDeploymentDescriptor(String appName)
            throws IllegalArgumentException,  RuntimeException {
        LOGGER.info("getDeploymentDescriptor(" + appName + ") - (ENTER)");

        validator.assertValidApplication(appName);

        Application ret = null;

        ApplicationEntity entity = jpaDao.readApplication(appName);
        if (entity != null) {
            ret = convert(entity);
        }

        LOGGER.info("getDeploymentDescriptor() - (LEAVE)");
        return ret;
    }


    private ApplicationEntity getObsoleteDetails(ApplicationEntity entity,
                                                 Application src) {
        ApplicationEntity ret = new ApplicationEntity();

        if(src.isRetainDatasets() == null || !src.isRetainDatasets()){
        	if (entity.getDatasetList() != null) {
                ret.setDatasetList(new ArrayList<DatasetEntity>());
                Iterator<DatasetEntity> i = entity.getDatasetList().iterator();
                while (i.hasNext()) {
                    DatasetEntity e = i.next();
                    if (e.getDatasetId() != null &&
                            findDataset(e.getName(), src.getDataset()) == null) {
                        DatasetEntity n = new DatasetEntity();
                        n.setDatasetId(e.getDatasetId());
                        ret.getDatasetList().add(n);
                    }
                }
            }
        }

        if (entity.getFeatureList() != null) {
            ret.setFeatureList(new ArrayList<FeatureEntity>());
            Iterator<FeatureEntity> i = entity.getFeatureList().iterator();
            while (i.hasNext()) {
                FeatureEntity e = i.next();
                if (e.getFeatureId() != null &&
                        findFeature(e.getName(), src.getFeature()) == null) {
                    FeatureEntity n = new FeatureEntity();
                    n.setFeatureId(e.getFeatureId());
                    ret.getFeatureList().add(n);
                }
            }
        }

        if (entity.getOptionList() != null) {
            ret.setOptionList(new ArrayList<OptionEntity>());
            Iterator<OptionEntity> i = entity.getOptionList().iterator();
            while (i.hasNext()) {
                OptionEntity e = i.next();
                if (e.getOptionId() != null &&
                        findOption(e.getName(), src.getOption()) == null) {
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
                    throw new IllegalArgumentException("Dataset " + item.getName() +
                            " already defined");
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
                    throw new IllegalArgumentException("Feature " + item.getName() +
                            " already defined");
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
                    throw new IllegalArgumentException("Option " + item.getName() +
                            " already defined");
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
                    o.setDefaultValue(oe.getDefaultValue()!=null?new String(oe.getDefaultValue()):null);

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

    private ApplicationEntity update(ApplicationEntity ret, Application src) {
        ret.setName(src.getName());
        ret.setDescription(src.getDescription());

        String parentName = src.getParent();
        if (parentName != null) {
            ApplicationEntity parentEntity = jpaDao.readApplication(parentName);
            ret.setParentApplication(parentEntity);
        }

        ret = updateFeatures(ret, src);
        ret = updateDatasets(ret, src);
        ret = updateOptions(ret, src);

        return ret;
    }

    private ApplicationEntity updateFeatures(ApplicationEntity ret, Application src) {
        if (!src.getFeature().isEmpty()) {
            List<FeatureEntity> lst = ret.getFeatureList();
            ret.setFeatureList(new ArrayList<FeatureEntity>());
            if (lst != null && !lst.isEmpty()) {
                ret.getFeatureList().addAll(lst);
            }

            for (Feature fe : src.getFeature()) {
                FeatureEntity f = findFeatureEntity(fe.getName(), ret.getFeatureList());
                if (f == null) {
                    f = new FeatureEntity();
                    f.setName(fe.getName());
                    ret.getFeatureList().add(f);
                }
                f.setDescription(fe.getDescription());
                f.setGroupName(fe.getGroup());
            }
        }

        return ret;
    }

    private ApplicationEntity updateOptions(ApplicationEntity ret, Application src) {
        if (!src.getOption().isEmpty()) {
            List<OptionEntity> lst = ret.getOptionList();
            ret.setOptionList(new ArrayList<OptionEntity>());
            if (lst != null && !lst.isEmpty()) {
                ret.getOptionList().addAll(lst);
            }

            for (Option oe : src.getOption()) {
            	if (oe.getDefaultValue()!=null &&oe.getDefaultValue().getBytes().length>=optionValuePolicy*1024)
            	{
            		throw new IllegalArgumentException ("The value of the option "+oe.getName()+ " is greater than the accepted limit "+optionValuePolicy);
            	}
            	
            	
                OptionEntity o = findOptionEntity(oe.getName(), ret.getOptionList());
                if (o == null) {
                    o = new OptionEntity();
                    o.setName(oe.getName());
                    ret.getOptionList().add(o);
                }
                o.setDescription(oe.getDescription());
                o.setDataType(oe.getDataType());
                o.setDefaultValue(oe.getDefaultValue()!=null?oe.getDefaultValue().getBytes():null);
                o.setGroupName(oe.getGroup());
            }
        }

        return ret;
    }

    private ApplicationEntity updateDatasets(ApplicationEntity ret,
                                             Application src) {
        if (!src.getDataset().isEmpty()) {
            List<DatasetEntity> lst = ret.getDatasetList();
            ret.setDatasetList(new ArrayList<DatasetEntity>());
            if (lst != null && !lst.isEmpty()) {
                ret.getDatasetList().addAll(lst);
            }

            for (Dataset de : src.getDataset()) {
                DatasetEntity d = findDatasetEntity(de.getName(), ret.getDatasetList());
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

        return ret;
    }

    private FeatureEntity findFeatureEntity(String name, List<FeatureEntity> list) {
        FeatureEntity ret = null;

        for (FeatureEntity item : list) {
            if (item.getName().equals(name)) {
                ret = item;
                break;
            }
        }

        return ret;
    }

    private DatasetEntity findDatasetEntity(String name, List<DatasetEntity> list) {
        DatasetEntity ret = null;

        for (DatasetEntity item : list) {
            if (item.getName().equals(name)) {
                ret = item;
                break;
            }
        }

        return ret;
    }

    private OptionEntity findOptionEntity(String name, List<OptionEntity> list) {
        OptionEntity ret = null;

        for (OptionEntity item : list) {
            if (item.getName().equals(name)) {
                ret = item;
                break;
            }
        }

        return ret;
    }

    private Feature findFeature(String name, List<Feature> list) {
        Feature ret = null;

        for (Feature item : list) {
            if (item.getName().equals(name)) {
                ret = item;
                break;
            }
        }

        return ret;
    }

    private Dataset findDataset(String name, List<Dataset> list) {
        Dataset ret = null;

        for (Dataset item : list) {
            if (item.getName().equals(name)) {
                ret = item;
                break;
            }
        }

        return ret;
    }

    private Option findOption(String name, List<Option> list) {
        Option ret = null;

        for (Option item : list) {
            if (item.getName().equals(name)) {
                ret = item;
                break;
            }
        }

        return ret;
    }

}
