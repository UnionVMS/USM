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

import java.util.HashSet;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.administration.domain.FindUserPreferenceQuery;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.USMFeature;
import eu.europa.ec.mare.usm.administration.domain.UnauthorisedException;
import eu.europa.ec.mare.usm.administration.domain.UserPreferenceResponse;
import eu.europa.ec.mare.usm.administration.service.userPreference.UserPreferenceService;

/**
 * Stateless session bean implementation of the UserPreferenceService
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class UserPreferenceServiceBean implements UserPreferenceService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(UserPreferenceServiceBean.class.getName());

	@Inject
	private PreferenceJdbcDao userPreferenceJdbcDao;

	@Inject
	private UserPreferenceValidator validator;

	@Override
	public UserPreferenceResponse getUserPrefernces(ServiceRequest<FindUserPreferenceQuery> request)
			throws IllegalArgumentException, UnauthorisedException, RuntimeException {
		LOGGER.info("getUserPrefernces(" + request + ") - (ENTER)");
		
		HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
		featureSet.add(USMFeature.viewUsers);
		featureSet.add(USMFeature.manageUsers);

		validator.assertValid(request, "query", featureSet);
		UserPreferenceResponse ret = userPreferenceJdbcDao.getUserPreferences(request.getBody().getUserName(),
				request.getBody().getGroupName());
		
		LOGGER.info("getUserPrefernces() - (LEAVE)");
		return ret;
	}

}