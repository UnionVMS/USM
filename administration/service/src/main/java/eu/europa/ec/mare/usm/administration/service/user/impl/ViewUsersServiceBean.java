package eu.europa.ec.mare.usm.administration.service.user.impl;

import eu.europa.ec.mare.usm.administration.domain.*;
import eu.europa.ec.mare.usm.administration.service.role.impl.RoleValidator;
import eu.europa.ec.mare.usm.administration.service.user.ViewUsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;

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
        LOGGER.debug("findUsers(" + request + ") - (ENTER)");
        HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
        featureSet.add(USMFeature.viewUsers);
        featureSet.add(USMFeature.manageUsers);

        validator.assertValid(request, "query", featureSet);

        PaginationResponse<UserAccount> response = userDao.findUsers(request.getBody());

        LOGGER.debug("findUsers() - (LEAVE)");
        return response;
    }

    @Override
    public UserAccount getUser(ServiceRequest<GetUserQuery> request) {
        LOGGER.debug("getUser(" + request + ") - (ENTER)");

        HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
        featureSet.add(USMFeature.viewUsers);
        featureSet.add(USMFeature.manageUsers);

        validator.assertValid(request, "query", featureSet);

        UserAccount response = userDao.getUser(request.getBody());

        LOGGER.debug("getUser() - (LEAVE)");
        return response;
    }

    @Override
    public List<String> getUsersNames(ServiceRequest<String> request) {
        LOGGER.debug("getUsersNames(" + request + ") - (ENTER)");

        HashSet<USMFeature> featureSet = new HashSet<USMFeature>();
        featureSet.add(USMFeature.viewUsers);
        featureSet.add(USMFeature.manageUsers);

        validator.assertValid(request, "query", featureSet);

        List<String> response = userDao.getUsersNames();

        LOGGER.debug("getUsersNames() - (LEAVE)");
        return response;

    }

}
