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