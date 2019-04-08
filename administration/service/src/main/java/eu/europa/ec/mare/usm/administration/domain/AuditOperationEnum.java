package eu.europa.ec.mare.usm.administration.domain;

public enum AuditOperationEnum {
	
	CREATE("Create"), UPDATE("Update"), DELETE("Delete"), COPY("Copy"), RESET("Reset"),
	REMOVE("Remove");

    private String value;

    private AuditOperationEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
