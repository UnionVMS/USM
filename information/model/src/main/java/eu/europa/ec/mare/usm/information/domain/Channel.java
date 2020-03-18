package eu.europa.ec.mare.usm.information.domain;

import java.io.Serializable;

public class Channel implements Serializable {
    private static final long serialVersionUID = 2L;

    private String dataFlow;
    private String service;
    private Integer priority;

    public Channel() {
    }

    public String getDataFlow() {
        return dataFlow;
    }

    public void setDataFlow(String dataFlow) {
        this.dataFlow = dataFlow;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "dataFlow=" + dataFlow +
                ", service=" + service +
                ", priority=" + priority +
                '}';
    }
}
