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

import com.btmatthews.maven.plugins.inmemdb.Loader;
import com.btmatthews.maven.plugins.inmemdb.db.AbstractNoSQLDatabase;
import com.btmatthews.maven.plugins.inmemdb.ldr.json.JSONLoader;
import com.btmatthews.utils.monitor.Logger;
import com.fasterxml.jackson.databind.JsonNode;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.0.0
 */
public class MongoDBDatabase extends AbstractNoSQLDatabase {

    private static final int DEFAULT_PORT = 27017;

    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private ChannelFuture serverFuture;

    public MongoDBDatabase() {
        super(DEFAULT_PORT);
    }

    @Override
    protected Loader[] getLoaders() {
        return new Loader[]{
                new JSONLoader()
        };
    }

    public void insertObject(final String collection,
                             final JsonNode object,
                             final Logger logger) {
    }

    @Override
    public void start(final Logger logger) {
        final FakeMongoServer fakeMongoServer = new FakeMongoServer(getDatabaseName());
        try {
            final ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(final SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new MongoDecoder(),
                                    new FakeMongoServerHandler(fakeMongoServer));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            serverFuture = bootstrap.bind(getPort()).sync();
        } catch (final InterruptedException e) {
        }

    }

    @Override
    public void stop(final Logger logger) {
        try {
            serverFuture.channel().closeFuture().sync();
        } catch (final InterruptedException e) {
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
