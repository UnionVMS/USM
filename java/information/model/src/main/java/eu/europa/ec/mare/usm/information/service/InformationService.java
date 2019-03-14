package eu.europa.ec.mare.usm.information.service;

import java.util.List;

import eu.europa.ec.mare.usm.information.domain.ContactDetails;
import eu.europa.ec.mare.usm.information.domain.DataSet;
import eu.europa.ec.mare.usm.information.domain.DataSetFilter;
import eu.europa.ec.mare.usm.information.domain.Organisation;
import eu.europa.ec.mare.usm.information.domain.UserContext;
import eu.europa.ec.mare.usm.information.domain.UserContextQuery;
import eu.europa.ec.mare.usm.information.domain.UserPreference;

/**
 * Provides access to <i>personal</i> user information that may be used for
 * providing an improved user experience or for out-of-band communication (via
 * e-mail or SMS) with the user.
 */
public interface InformationService {
	/**
	 * Retrieves contact information about a specific application end-user.
	 * 
	 * @param userName
	 *            the (unique) user name
	 * 
	 * @return the user contact details if the user exists, <i>null</i>
	 *         otherwise
	 * 
	 * @throws IllegalArgumentException
	 *             in case the provided input is null or empty
	 * 
	 * @throws RuntimeException
	 *             in case an internal error prevented fulfilling the request
	 */
	public ContactDetails getContactDetails(String userName) throws IllegalArgumentException, RuntimeException;

	/**
	 * Retrieves information about all organisations associated with the
	 * provided nation.
	 * 
	 * @param nation
	 *            the nation name
	 * 
	 * @return the possibly-empty list of Organisations and their associated
	 *         EndPoints
	 * 
	 * @throws IllegalArgumentException
	 *             in case the provided input is null or empty
	 * 
	 * @throws RuntimeException
	 *             in case an internal error prevented fulfilling the request
	 */
	public List<Organisation> findOrganisations(String nation) throws IllegalArgumentException, RuntimeException;

	/**
	 * Retrieves information about an organisation and its associated
	 * end-points.
	 * 
	 * @param organisationName
	 *            the (unique) organisation name
	 * 
	 * @return the organisation details if the organisation exists, <i>null</i>
	 *         otherwise
	 * 
	 * @throws IllegalArgumentException
	 *             in case the provided input is null or empty
	 * 
	 * @throws RuntimeException
	 *             in case an internal error prevented fulfilling the request
	 */
	public Organisation getOrganisation(String organisationName) throws IllegalArgumentException, RuntimeException;

	/**
	 * Retrieves a User Context for a specific application.<br/>
	 * 
	 * When a user logs in an application, the application can call the service
	 * 'GetUserContext' with the identification of the application and the
	 * username.<br/>
	 * 
	 * USM sends back all user valid context.<br/>
	 * 
	 * A valid context is a context with an active status, (for which the status
	 * of the scope and role (if any) is valid) and for which is at least one
	 * basic data (feature, dataset) related to the calling application.
	 * 
	 * @param query
	 *            the (unique) user and application names
	 * 
	 * @return the user Context if it exists, <i>null</i> otherwise
	 * 
	 * @throws IllegalArgumentException
	 *             in case the provided input is null, empty or incomplete
	 * 
	 * @throws RuntimeException
	 *             in case an internal error prevented fulfilling the request
	 */
	public UserContext getUserContext(UserContextQuery query) throws IllegalArgumentException, RuntimeException;

	/**
	 * Stores the (modified) user preferences contained in the provided user
	 * context
	 * 
	 * @param userContext
	 *            the user Context
	 * 
	 * @throws IllegalArgumentException
	 *             in case the provided input is null, empty or incomplete; or
	 *             if it references inexistent user, context or option.
	 * 
	 * @throws RuntimeException
	 *             in case an internal error prevented fulfilling the request
	 */
	public void updateUserPreferences(UserContext userContext) throws IllegalArgumentException, RuntimeException;

	void updateUserPreference(UserPreference userPreference);
	void createUserPreference(UserPreference userPreference);
	void deleteUserPreference(UserPreference userPreference);
	UserPreference getUserPreference(UserPreference userPreference);

	
	void createDataSet(DataSet dataSet);
	void deleteDataSet(DataSet dataSet);
	void updateDataSet(DataSet dataSet);
	DataSet getDataSet(String name, String applicationName);
	List<DataSet> getDataSets(DataSetFilter dataSetFilter);

}
