package com.btmatthews.maven.plugins.inmemdb.db.fongo.messages;

import com.btmatthews.maven.plugins.inmemdb.db.fongo.codec.Request;
import com.foursquare.fongo.Fongo;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * Created with IntelliJ IDEA.
 * User: Brian
 * Date: 08/12/12
 * Time: 23:26
 * To change this template use File | Settings | File Templates.
 */
public class DeleteRequest extends Request {

    private final String fullCollectionName;

    private final int flags;

    private DBObject selector;

    public DeleteRequest(final int requestId,
                         final String fullCollectionName,
                         final int flags,
                         final DBObject selector) {
        super(requestId);
        this.fullCollectionName = fullCollectionName;
        this.flags = flags;
        this.selector = selector;
    }

    public void perform(final Fongo fongo) {
        final int pos = fullCollectionName.indexOf(".");
        final DB db = fongo.getDB(fullCollectionName.substring(0, pos));
        final DBCollection col = db.getCollection(fullCollectionName.substring(pos + 1));
        if ((flags & 1) == 0) {
            col.remove(selector);
        } else {
            col.findAndRemove(selector);
        }
    }
}