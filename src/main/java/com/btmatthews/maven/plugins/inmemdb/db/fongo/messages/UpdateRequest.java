package com.btmatthews.maven.plugins.inmemdb.db.fongo.messages;

import com.btmatthews.maven.plugins.inmemdb.db.fongo.messages.AbstractCollectionRequest;
import com.foursquare.fongo.Fongo;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * Created with IntelliJ IDEA.
 * User: Brian
 * Date: 08/12/12
 * Time: 22:34
 * To change this template use File | Settings | File Templates.
 */
public final class UpdateRequest extends AbstractCollectionRequest {

    private final int flags;

    private final DBObject selector;

    private final DBObject update;

    public UpdateRequest(final int requestId,
                         final String fullCollectionName,
                         final int flags,
                         final DBObject selector,
                         final DBObject update) {
        super(requestId, fullCollectionName);
        this.flags = flags;
        this.selector = selector;
        this.update = update;
    }

    @Override
    public void perform(final Fongo fongo) {
        final DBCollection collection = getCollection(fongo);
        collection.update(selector, update, (flags & 1) == 1, (flags & 2) == 2);
    }
}
