package com.btmatthews.maven.plugins.inmemdb.db.fongo.messages;

import com.foursquare.fongo.Fongo;

/**
 * Created with IntelliJ IDEA.
 * User: Brian
 * Date: 08/12/12
 * Time: 23:14
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractRequest {

    private final int requestId;

    public AbstractRequest(final int requestId) {
        this.requestId = requestId;
    }

    public int getRequestId() {
        return requestId;
    }

    public abstract void perform(final Fongo fongo);
}

