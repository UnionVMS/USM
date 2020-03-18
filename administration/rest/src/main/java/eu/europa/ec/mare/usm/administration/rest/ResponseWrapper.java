package eu.europa.ec.mare.usm.administration.rest;

import java.io.Serializable;

/**
 * class to be used as wrapper class in case
 * we need to wrap a single class into json object for the service response
 */
public class ResponseWrapper<T> implements Serializable {
    private static final long serialVersionUID = 1;

    private T result;

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

}
