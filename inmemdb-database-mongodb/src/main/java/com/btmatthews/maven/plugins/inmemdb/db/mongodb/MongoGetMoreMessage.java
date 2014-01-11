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

/**
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 2.0.0
 */
public class MongoGetMoreMessage extends AbstractMongoMessage {

    private final String fullCollectionName;
    private final int numberToReturn;
    private final long cursorID;

    public MongoGetMoreMessage(final int requestID,
                               final int responseTo,
                               final String fullCollectionName,
                               final int numberToReturn,
                               final long cursorID) {
        super(requestID, responseTo, OpCode.OP_GET_MORE);
        this.fullCollectionName = fullCollectionName;
        this.numberToReturn = numberToReturn;
        this.cursorID = cursorID;
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

    public int getNumberToReturn() {
        return numberToReturn;
    }

    public long getCursorID() {
        return cursorID;
    }
}
