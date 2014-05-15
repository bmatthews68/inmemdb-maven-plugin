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
public class AbstractMongoMessage {

    private final int requestID;
    private final int responseTo;
    private final OpCode opCode;

    public AbstractMongoMessage(final int requestID,
                                final int responseTo,
                                final OpCode opCode) {
        this.requestID = requestID;
        this.responseTo = responseTo;
        this.opCode = opCode;
    }

    public int getRequestID() {
        return requestID;
    }

    public int getResponseTo() {
        return responseTo;
    }

    public OpCode getOpCode() {
        return opCode;
    }
}
