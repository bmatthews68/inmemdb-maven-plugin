package com.btmatthews.maven.plugins.inmemdb.db.fongo.messages;

import com.btmatthews.maven.plugins.inmemdb.db.fongo.codec.Request;
import com.foursquare.fongo.Fongo;
import com.mongodb.DB;
import com.mongodb.DBCollection;

/**
 * Created with IntelliJ IDEA.
 * User: Brian
 * Date: 09/12/12
 * Time: 00:01
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractCollectionRequest extends Request {

    private final String dbName;

    private final String collectionName;

    protected AbstractCollectionRequest(final int requestId,
                                        final String fullCollectionName) {
        super(requestId);
        int pos = fullCollectionName.indexOf('.');
        dbName = fullCollectionName.substring(0, pos);
        collectionName = fullCollectionName.substring(pos + 1);
    }

    protected final DBCollection getCollection(final Fongo fongo) {
        final DB db = fongo.getDB(dbName);
        return db.getCollection(collectionName);
    }
}
