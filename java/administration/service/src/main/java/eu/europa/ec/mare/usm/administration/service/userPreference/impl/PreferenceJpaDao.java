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
package eu.europa.ec.mare.usm.administration.service.userPreference.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.information.entity.PreferenceEntity;

public class PreferenceJpaDao {

  private static final Logger LOGGER = LoggerFactory.getLogger(PreferenceJpaDao.class);

  @PersistenceContext(unitName = "USM-Administration")
  private EntityManager em;

  /**
   * Creates a new instance.
   */
  public PreferenceJpaDao() {
  }

  /**
   * Retrieves the preferences for the context with the provided identifier
   *
   * @param contextId the user context identifier
   * 
   * @return the possibly-empty list of preferences
   */
  public List<PreferenceEntity> read(Long contextId) 
  {
    LOGGER.info("read(" + contextId + ") - (ENTER)");

    TypedQuery<PreferenceEntity> q = em.createNamedQuery("PreferenceEntity.findByContextId",
                                                         PreferenceEntity.class);

    q.setParameter("contextId", contextId);
    List<PreferenceEntity> ret = q.getResultList();

    LOGGER.info("read() - (LEAVE)");
    return ret;
  }
}