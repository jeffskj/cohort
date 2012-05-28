package org.cohort.msg;

public interface ResponseHandler<T> {
    void handle(T response);
}
