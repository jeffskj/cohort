package org.cohort.msg;

public interface RequestHandler<REQ, RESP> {
    RESP handle(REQ messageValue);
}
