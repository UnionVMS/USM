package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Holds a response that contains the response plus the total number of the results
 */
public class PaginationResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<T> results;
    private int total;

    public PaginationResponse() {
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "PaginationResponse{" +
                "results=" + results +
                "total=" + total +
                '}';
    }

}
