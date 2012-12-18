package com.btmatthews.maven.plugins.inmemdb.db.fongo.messages;

import com.btmatthews.maven.plugins.inmemdb.db.fongo.codec.Request;

/**
 * Created with IntelliJ IDEA.
 * User: Brian
 * Date: 08/12/12
 * Time: 23:23
 * To change this template use File | Settings | File Templates.
 */
public class KillCursorsRequest extends Request {

    private final long cursorIDs[];

    public KillCursorsRequest(final int requestId,
                              final long cursorIDs[]) {
        super(requestId);
        this.cursorIDs = cursorIDs;
    }
}
