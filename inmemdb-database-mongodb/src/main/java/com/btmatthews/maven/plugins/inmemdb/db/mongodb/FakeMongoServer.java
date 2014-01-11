/*
 * Copyright 2011-2014 Brian Matthews
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.btmatthews.maven.plugins.inmemdb.db.mongodb;

import com.github.fakemongo.Fongo;
import com.mongodb.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 2.0.0
 */
public class FakeMongoServer {

    private Fongo fongo;

    private Map<Long, DBCursor> openCursors = new HashMap<Long, DBCursor>();

    public FakeMongoServer(final String databaseName) {
        fongo = new Fongo(databaseName);
    }

    public void handleMsg(final MongoMsgMessage message) {
    }

    public void handleUpdate(final MongoUpdateMessage message) {
        final DB db = fongo.getDB(message.getDatabaseName());
        final DBCollection collection = db.getCollection(message.getCollectionName());
        collection.update(message.getSelector(), message.getUpdate(), message.isUpsert(), message.isMultiUpdate());
    }

    public void handleInsert(final MongoInsertMessage message) {
        final DB db = fongo.getDB(message.getDatabaseName());
        final DBCollection collection = db.getCollection(message.getCollectionName());
        if (message.isContinueOnError()) {
            collection.insert(message.getDocuments(), WriteConcern.ERRORS_IGNORED);
        } else {
            collection.insert(message.getDocuments());
        }
    }

    public MongoReplyMessage handleQuery(final MongoQueryMessage message) {
        final DB db = fongo.getDB(message.getDatabaseName());
        final DBCollection collection = db.getCollection(message.getCollectionName());
        final DBCursor cursor = collection.find(message.getQuery(), message.getReturnFieldsSelector());
        //cursor.skip(message.getNumberToSkip());
        //cursor.limit(message.getNumberToReturn());
        return new MongoReplyMessage(
                0,
                message.getRequestID(),
                0,
                cursor.getCursorId(),
                message.getNumberToSkip(),
                message.getNumberToReturn(),
                null);
    }

    public MongoReplyMessage handleGetMore(final MongoGetMoreMessage message) {
        final DBCursor cursor = openCursors.get(Long.valueOf(message.getCursorID()));
        final List<DBObject> results = new ArrayList<DBObject>();
        int count = message.getNumberToReturn();
        while (count-- > 0 && cursor.hasNext()) {
            results.add(cursor.next());
        }
        return new MongoReplyMessage(
                0,
                message.getRequestID(),
                0,
                cursor.getCursorId(),
                0,
                0,
                null);
    }

    public void handleDelete(final MongoDeleteMessage message) {
        final DB db = fongo.getDB(message.getDatabaseName());
        final DBCollection collection = db.getCollection(message.getCollectionName());
        collection.remove(message.getSelector());
    }

    public void handleKillCursors(final MongoKillCursorsMessage message) {
        for (final long cursorID : message.getCursorIDs()) {
            openCursors.remove(Long.valueOf(cursorID));
        }
    }
}
