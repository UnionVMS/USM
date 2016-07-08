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
package eu.europa.ec.mare.usm.information.service.impl;

import java.util.ArrayList;
import java.util.List;

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

import eu.europa.ec.mare.usm.information.domain.DataSet;
import eu.europa.ec.mare.usm.information.domain.DataSetFilter;
import eu.europa.ec.mare.usm.information.entity.ApplicationEntity;
import eu.europa.ec.mare.usm.information.entity.DatasetEntity;
import eu.europa.ec.mare.usm.information.entity.OptionEntity;
import eu.europa.ec.mare.usm.information.entity.PreferenceEntity;

public class DataSetJpaDao {
  private static final Logger LOGGER = LoggerFactory.getLogger(DataSetJpaDao.class);

  private static final String DATASET_ALREADY_EXISTS = "Dataset already exists";
  private static final String DATASET_NOT_EXISTS = "Dataset does not exists";
  private static final String APPLICATION_NOT_EXISTS = "Application does not exists";
  private static final String APPLICATION_NAME_NOT_SPECIFIED = "Application name not specified";
 
  @PersistenceContext(unitName = "USM-Administration")
  private EntityManager em;

  @Inject
  private ApplicationJpaDao applicationDao;
  
  /**
   * Creates a new instance
   */
  public DataSetJpaDao() {
  }

  public List<DatasetEntity> findDataSet(String applicationName, String category) 
  {
    LOGGER.info("findDataSet(" + applicationName + "," + category + ")- ENTER");
    
    List<DatasetEntity> ret = null;
    
    try {
    	
    	CriteriaBuilder builder= em.getCriteriaBuilder();
		CriteriaQuery<DatasetEntity> criteriaQuery =builder.createQuery(DatasetEntity.class);
		Root<DatasetEntity> dataSet = criteriaQuery.from(DatasetEntity.class); 
		Join<OptionEntity, ApplicationEntity> application = (Join)dataSet.fetch("application");
		
		List<Predicate> conditions = new ArrayList<Predicate>();
		
		if (category != null) {
			conditions.add(builder.equal(dataSet.get("category"), category));
		}
		if (applicationName != null) {
			conditions.add(builder.equal(application.get("name"), applicationName));
		}
		
		TypedQuery<DatasetEntity> typedQuery = em.createQuery(criteriaQuery
				.select(dataSet) 
		        .where(conditions.toArray(new Predicate[] {}))
		        //.distinct(true)
		);
		ret = typedQuery.getResultList();
		
    } catch (Exception ex) {
      handleException("read", ex);
    }

    LOGGER.info("findDataSet ()- LEAVE");
    return ret;
  }

  /**
   * Reads the dataset with the provided identifier
   *
   * @param datasetId the id of the dataset
   *
   * @return the matching dataset if it exists, null otherwise
   */
  public DatasetEntity read(Long datasetId) 
  {
    LOGGER.info("read(" + datasetId + ") - (ENTER)");
    
    DatasetEntity ret = null;

    try {
      TypedQuery<DatasetEntity> q = em.createNamedQuery("DatasetEntity.findByDatasetId",
              DatasetEntity.class);

      q.setParameter("datasetId", datasetId);
      ret = q.getSingleResult();
    } catch (NoResultException ex) {
      LOGGER.info("Dataset with id " + datasetId + " not found");
    } catch (Exception ex) {
      handleException("read", ex);
    }

    LOGGER.info("read() - (LEAVE)");
    return ret;
  }

  private void handleException(String operation, Exception ex)
  throws RuntimeException 
  {
	  String msg = "Failed to " + operation + " dataset: " + ex.getMessage();

    LOGGER.error(msg, ex);
    throw new RuntimeException(msg, ex);
  }
  
  
    /**
     * 
     * @param dataSet
     */
	public void createDataSet(DataSet dataSet) {
		LOGGER.info("createDataSet(" + dataSet + ") - (ENTER)");
		try {
			DatasetEntity entity = findDataSetByNameAndApplication(dataSet.getName(), dataSet.getApplicationName());
			LOGGER.info("---> entity : " + entity);
			if (entity != null) {
				throw new IllegalArgumentException(DATASET_ALREADY_EXISTS);
			}

			ApplicationEntity applicationEntity = applicationDao.read(dataSet.getApplicationName());
			if(applicationEntity == null){
				throw new IllegalArgumentException(APPLICATION_NOT_EXISTS);
			}
			
			entity = new DatasetEntity();
			entity.setApplication(applicationEntity);
			entity.setCategory(dataSet.getCategory());
			entity.setDescription(dataSet.getDescription());
			entity.setDiscriminator(dataSet.getDiscriminator());
			entity.setName(dataSet.getName());

			em.persist(entity);
			em.flush();

		} catch (Exception ex) {
			handleException("createDataSet", ex);
		}
		LOGGER.info("createDataSet() - (LEAVE): ");

	}

	public void updateDataSet(DataSet dataSet) {
		LOGGER.info("updateDataSet(" + dataSet + ") - (ENTER)");
		try {
			DatasetEntity entity = findDataSetByNameAndApplication(dataSet.getName(), dataSet.getApplicationName());
			LOGGER.info("---> entity : " + entity) ;
			if (entity == null) {
				throw new IllegalArgumentException(DATASET_NOT_EXISTS);
			}			
			if (dataSet.getCategory() != null && dataSet.getCategory().length() > 0) {
				entity.setCategory(dataSet.getCategory());
			}
			if (dataSet.getDescription() != null && dataSet.getDescription().length() > 0) {
				entity.setDescription(dataSet.getDescription());
			}
			if (dataSet.getDiscriminator() != null && dataSet.getDiscriminator().length() > 0) {
				entity.setDiscriminator(dataSet.getDiscriminator());
			}
			em.merge(entity);
			em.flush();
		} catch (Exception ex) {
			handleException("updateDataSet", ex);
		}
		LOGGER.info("updateDataSet() - (LEAVE): ");
		
	}

	/**
	 * 
	 * @param dataSet
	 */
	public void deleteDataSet(DataSet dataSet) {
		LOGGER.info("deleteDataSet(" + dataSet + ") - (ENTER)");

		try {
			DatasetEntity entity = findDataSetByNameAndApplication(dataSet.getName(), dataSet.getApplicationName());
			LOGGER.info("---> entity : " + entity);
			if (entity == null) {
				throw new IllegalArgumentException(DATASET_NOT_EXISTS);
			}
			em.remove(entity);
			em.flush();
		} catch (Exception ex) {
			handleException("deleteUserPreference", ex);
		}

		LOGGER.info("deleteDataSet() - (LEAVE)");

	}


	/**
	 * 
	 * @param name
	 * @param applicationName
	 * @return
	 */
	public DatasetEntity findDataSetByNameAndApplication(String name, String applicationName) {
		LOGGER.info("findDataSetByNameAndApplication(" + name + "," + applicationName + ")- ENTER");

		DatasetEntity entity = null;

		try {

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<DatasetEntity> criteriaQuery = builder.createQuery(DatasetEntity.class);
			Root<DatasetEntity> dataSet = criteriaQuery.from(DatasetEntity.class);
			Join<OptionEntity, ApplicationEntity> application = (Join) dataSet.fetch("application");

			List<Predicate> conditions = new ArrayList<Predicate>();
			conditions.add(builder.equal(dataSet.get("name"), name));
			conditions.add(builder.equal(application.get("name"), applicationName));

			TypedQuery<DatasetEntity> typedQuery = em.createQuery(
					criteriaQuery.select(dataSet).where(conditions.toArray(new Predicate[] {})).distinct(true));
			entity = typedQuery.getSingleResult();
		
		} catch (NoResultException ex) {
			LOGGER.info("dataset: " + name + "," + applicationName + " not found", ex);		
		} catch (Exception ex) {
			handleException("findDataSetByNameAndApplication", ex);
		}

		LOGGER.info("findDataSetByNameAndApplication ()- LEAVE");
		return entity;
	}

	/**
	 * 
	 * @param name
	 * @param discriminator
	 * @return
	 */
	public List<DatasetEntity> findDataSets(DataSetFilter dataSetFilter) {
		LOGGER.info("findDataSets(" + dataSetFilter.getName() + "," + dataSetFilter.getApplicationName() + ")- ENTER");

		if(dataSetFilter.getApplicationName() == null || dataSetFilter.getApplicationName().length() <=0 ){
			throw new IllegalArgumentException(APPLICATION_NAME_NOT_SPECIFIED);
		}
		
		List<DatasetEntity> entityList = new ArrayList<DatasetEntity>();

		try {

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<DatasetEntity> criteriaQuery = builder.createQuery(DatasetEntity.class);
			Root<DatasetEntity> dataSet = criteriaQuery.from(DatasetEntity.class);
			Join<OptionEntity, ApplicationEntity> application = (Join) dataSet.fetch("application");

			List<Predicate> conditions = new ArrayList<Predicate>();			
			conditions.add(builder.equal(application.get("name"), dataSetFilter.getApplicationName()));
			if (dataSetFilter.getName() != null && dataSetFilter.getName().length() > 0) {
				conditions.add(builder.equal(dataSet.get("name"), dataSetFilter.getName()));
			}
			if (dataSetFilter.getCategory() != null && dataSetFilter.getCategory().length() > 0) {
				conditions.add(builder.equal(dataSet.get("category"), dataSetFilter.getCategory()));
			}
			if (dataSetFilter.getDiscriminator() != null && dataSetFilter.getDiscriminator().length() > 0) {
				conditions.add(builder.equal(dataSet.get("discriminator"), dataSetFilter.getDiscriminator()));
			}

			TypedQuery<DatasetEntity> typedQuery = em.createQuery(
					criteriaQuery.select(dataSet).where(conditions.toArray(new Predicate[] {})).distinct(true));
			entityList = typedQuery.getResultList();

		} catch (Exception ex) {
			handleException("findDataSets", ex);
		}

		LOGGER.info("findDataSets ()- LEAVE");
		return entityList;
	}


}