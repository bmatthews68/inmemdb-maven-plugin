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

import com.mongodb.DBObject;

import java.util.List;

/**
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 2.0.0
 */
public class MongoReplyMessage extends AbstractMongoMessage {

    private final int responseFlags;
    private final long cursorID;
    private final int startingFrom;
    private final int numberReturned;
    private final List<DBObject> documents;

    public MongoReplyMessage(final int requestID,
                             final int responseTo,
                             final int responseFlags,
                             final long cursorID,
                             final int startingFrom,
                             final int numberReturned,
                             final List<DBObject> documents) {
        super(requestID, responseTo, OpCode.OP_REPLY);
        this.responseFlags = responseFlags;
        this.cursorID = cursorID;
        this.startingFrom = startingFrom;
        this.numberReturned = numberReturned;
        this.documents = documents;
    }

    public int getResponseFlags() {
        return responseFlags;
    }

    public boolean isCursorNotFound() {
        return (responseFlags & 0x1) == 0x1;
    }

    public boolean isQueryFailure() {
        return (responseFlags & 0x2) == 0x2;
    }

    public boolean isShardConfigStale() {
        return (responseFlags & 0x4) == 0x4;
    }

    public boolean isAwaitCapable() {
        return (responseFlags & 0x8) == 0x8;
    }

    public long getCursorID() {
        return cursorID;
    }

    public int getStartingFrom() {
        return startingFrom;
    }

    public int getNumberReturned() {
        return numberReturned;
    }

    public List<DBObject> getDocuments() {
        return documents;
    }
}
