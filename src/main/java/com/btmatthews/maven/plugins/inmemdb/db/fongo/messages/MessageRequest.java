package com.btmatthews.maven.plugins.inmemdb.db.fongo.messages;

import com.btmatthews.maven.plugins.inmemdb.db.fongo.codec.Request;

/**
 * Created with IntelliJ IDEA.
 * User: Brian
 * Date: 08/12/12
 * Time: 23:15
 * To change this template use File | Settings | File Templates.
 */
public class MessageRequest extends Request {

    private final String message;

    public MessageRequest(final int requestId,
                          final String message) {
        super(requestId);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
