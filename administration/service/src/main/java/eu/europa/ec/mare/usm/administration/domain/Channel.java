package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * Holds the details of a channel
 */
public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long channelId;
    private String dataflow;
    private String service;
    private Integer priority;
    private Long endpointId;

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getDataflow() {
        return dataflow;
    }

    public void setDataflow(String dataflow) {
        this.dataflow = dataflow;
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

    public Long getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(Long endpointId) {
        this.endpointId = endpointId;
    }

    @Override
    public String toString() {
        return "Channel [channelId=" + channelId + ", dataflow=" + dataflow
                + ", service=" + service + ", priority=" + priority
                + ", endpointId=" + endpointId + "]";
    }

}
