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
public enum OpCode {

    OP_REPLY(1),
    OP_MSG(1000),
    OP_UPDATE(2001),
    OP_INSERT(2002),
    OP_QUERY(2004),
    OP_GET_MORE(2005),
    OP_DELETE(2006),
    OP_KILL_CURSORS(2007);

    private final int value;

    OpCode(final int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static OpCode fromInt(final int value) {
        switch (value) {
            case 1:
                return OP_REPLY;
            case 1000:
                return OP_MSG;
            case 2001:
                return OP_UPDATE;
            case 2002:
                return OP_INSERT;
            case 2004:
                return OP_QUERY;
            case 2005:
                return OP_GET_MORE;
            case 2006:
                return OP_DELETE;
            case 2007:
                return OP_KILL_CURSORS;
        }
        return null;
    }
}
