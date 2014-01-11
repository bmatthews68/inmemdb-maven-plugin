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

/**
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 2.0.0
 */
public class MongoQueryMessage extends AbstractMongoMessage {

    private final int flags;
    private final String fullCollectionName;
    private final int numberToSkip;
    private final int numberToReturn;
    private final DBObject query;
    private final DBObject returnFieldsSelector;

    public MongoQueryMessage(final int requestID,
                             final int responseTo,
                             final int flags,
                             final String fullCollectionName,
                             final int numberToSkip,
                             final int numberToReturn,
                             final DBObject query,
                             final DBObject returnFieldsSelector) {
        super(requestID, responseTo, OpCode.OP_QUERY);
        this.flags = flags;
        this.fullCollectionName = fullCollectionName;
        this.numberToSkip = numberToSkip;
        this.numberToReturn = numberToReturn;
        this.query = query;
        this.returnFieldsSelector = returnFieldsSelector;
    }

    public int getFlags() {
        return flags;
    }

    public boolean isTailableCursor() {
        return (flags & 0x2) == 0x2;
    }

    public boolean isSlaveOk() {
        return (flags & 0x4) == 0x4;
    }

    public boolean isOplogReplay() {
        return (flags & 0x8) == 0x8;
    }

    public boolean isNoCursorTimeout() {
        return (flags & 0x10) == 0x10;
    }

    public boolean isAwaitData() {
        return (flags & 0x20) == 0x20;
    }

    public boolean isExhaust() {
        return (flags & 0x40) == 0x40;
    }

    public boolean isPartial() {
        return (flags & 0x80) == 0x80;
    }

    public String getFullCollectionName() {
        return fullCollectionName;
    }

    public String getDatabaseName() {
        final int pos = fullCollectionName.indexOf('.');
        return fullCollectionName.substring(0, pos);
    }

    public String getCollectionName() {
        final int pos = fullCollectionName.indexOf('.');
        return fullCollectionName.substring(pos + 1);
    }

    public int getNumberToSkip() {
        return numberToSkip;
    }

    public int getNumberToReturn() {
        return numberToReturn;
    }

    public DBObject getQuery() {
        return query;
    }

    public DBObject getReturnFieldsSelector() {
        return returnFieldsSelector;
    }
}
