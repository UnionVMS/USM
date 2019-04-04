package eu.europa.ec.mare.usm.administration.domain;

/**
 * Defines all known features 
 */
public enum USMFeature {
  /** View the system users */
  viewUsers,  
  /** Manage (create, update, enable, disable) the system users */
  manageUsers,  
  /** View the system organisations and their end-points */
  viewOrganisations,  
  /** Manage (create, update, delete) the system organisations and their end-points */
  manageOrganisations,  
  /** View the system applications */
  viewApplications,  
  /** Manage (create, update, delete) the system applications */
  manageApplications,  
  /** Copy roles, scopes and preferences from one user to another */
  copyUserProfile,  
  /** View the application roles */
  viewRoles,  
  /** Manage (create, update, delete, enable, disable) the application roles */
  manageRoles,  
  /** View the application scopes */
  viewScopes,  
  /** Manage (create, update, delete, enable, disable) the application scopes */
  manageScopes,  
  /** Configure password, account, etc. policies */
  configurePolicies;  
}
