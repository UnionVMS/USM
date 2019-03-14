package eu.europa.ec.mare.usm.administration.domain;

import eu.europa.ec.mare.audit.logger.AuditRecord;

public class AuditRecordFactory {

	public static <T> AuditRecord createAuditRecord(String actionName, String componentName, 
			String requester, String resourceName, String rawData) {
		
		AuditRecord ar = new AuditRecord();
		
		ar.setActionName(actionName);
	    ar.setApplicationName(USMApplication.USM.name());
	    ar.setComponentName(componentName);
	    ar.setFailure(false);
	    ar.setUserId(requester);
	    ar.setResourceName(resourceName);
	    ar.setRawData(rawData);

        return ar;
    }

}
