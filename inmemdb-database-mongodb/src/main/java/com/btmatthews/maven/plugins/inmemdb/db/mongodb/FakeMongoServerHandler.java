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
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 2.0.0
 */
public class FakeMongoServerHandler extends ChannelInboundHandlerAdapter {

    private final FakeMongoServer fakeMongoServer;


    public FakeMongoServerHandler(final FakeMongoServer fakeMongoServer) {
        this.fakeMongoServer = fakeMongoServer;
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx,
                            final Object msg) {
        try {
            if (msg instanceof AbstractMongoMessage) {
                AbstractMongoMessage mongoMessage = (AbstractMongoMessage) msg;
                switch (mongoMessage.getOpCode()) {
                    case OP_MSG:
                        fakeMongoServer.handleMsg((MongoMsgMessage) mongoMessage);
                        break;
                    case OP_UPDATE:
                        fakeMongoServer.handleUpdate((MongoUpdateMessage) mongoMessage);
                        break;
                    case OP_INSERT:
                        fakeMongoServer.handleInsert((MongoInsertMessage) mongoMessage);
                    case OP_QUERY:
                        fakeMongoServer.handleQuery((MongoQueryMessage) mongoMessage);
                        break;
                    case OP_GET_MORE:
                        fakeMongoServer.handleGetMore((MongoGetMoreMessage) mongoMessage);
                        break;
                    case OP_DELETE:
                        fakeMongoServer.handleDelete((MongoDeleteMessage) mongoMessage);
                        break;
                    case OP_KILL_CURSORS:
                        fakeMongoServer.handleKillCursors((MongoKillCursorsMessage) mongoMessage);
                        break;
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx,
                                final Throwable cause) {
        ctx.close();
    }
}
