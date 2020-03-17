package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * object to be used for pagination
 */
public class Paginator implements Serializable {
    private static final long serialVersionUID = 1L;
    private int offset;
    private int limit;
    private String sortColumn;
    private String sortDirection;

    public Paginator() {
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    @Override
    public String toString() {
        return "Option{" +
                "offset=" + offset +
                ", limit=" + limit +
                ", sortColumn=" + sortColumn +
                ", sortDirection=" + sortDirection +
                '}';
    }

}
