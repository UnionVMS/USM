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
package eu.europa.ec.mare.usm.administration.service.user.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.administration.domain.FindUsersQuery;
import eu.europa.ec.mare.usm.administration.domain.GetUserQuery;
import eu.europa.ec.mare.usm.administration.domain.PaginationResponse;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.USMFeature;
import eu.europa.ec.mare.usm.administration.domain.UserAccount;
import eu.europa.ec.mare.usm.administration.service.role.impl.RoleValidator;
import eu.europa.ec.mare.usm.administration.service.user.ViewUsersService;

/**
 * Stateless session bean implementation of the UsersService
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ViewUsersServiceBean implements ViewUsersService {
  private static final Logger LOGGER = LoggerFactory.getLogger(ViewUsersServiceBean.class.getName());

  @Inject
  private UserJdbcDao userDao;
  
  @Inject
  private RoleValidator validator;

  @Override
  public PaginationResponse<UserAccount> findUsers(ServiceRequest<FindUsersQuery> request) {
    LOGGER.info("findUsers(" + request + ") - (ENTER)");
    HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
    featureSet.add(USMFeature.viewUsers);
    featureSet.add(USMFeature.manageUsers);
    
    validator.assertValid(request, "query", featureSet);

    PaginationResponse<UserAccount> response = userDao.findUsers(request.getBody());

    LOGGER.info("findUsers() - (LEAVE)");
    return response;
  }

  @Override
  public UserAccount getUser(ServiceRequest<GetUserQuery> request) 
  {
    LOGGER.info("getUser(" + request + ") - (ENTER)");

    HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
    featureSet.add(USMFeature.viewUsers);
    featureSet.add(USMFeature.manageUsers);

    validator.assertValid(request, "query", featureSet);

    UserAccount response = userDao.getUser(request.getBody());

    LOGGER.info("getUser() - (LEAVE)");
    return response;
  }

@Override
public List<String> getUsersNames(ServiceRequest<String> request) {
	LOGGER.info("getUsersNames(" + request + ") - (ENTER)");

    HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
    featureSet.add(USMFeature.viewUsers);
    featureSet.add(USMFeature.manageUsers);

    validator.assertValid(request, "query", featureSet);

    List<String> response = userDao.getUsersNames();

    LOGGER.info("getUsersNames() - (LEAVE)");
    return response;

}

}