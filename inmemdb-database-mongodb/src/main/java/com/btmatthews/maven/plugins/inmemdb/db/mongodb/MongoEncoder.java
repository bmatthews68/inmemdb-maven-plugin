package com.btmatthews.maven.plugins.inmemdb.db.mongodb;

import com.mongodb.DBObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.bson.BSON;

/**
 * <pre>
 * int32     responseFlags;  // bit vector - see details below
 * int64     cursorID;       // cursor id if client needs to do get more's
 * int32     startingFrom;   // where in the cursor this reply is starting
 * int32     numberReturned; // number of documents in the reply
 * document* documents;      // documents
 * </pre>
 */
public class MongoEncoder extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(final ChannelHandlerContext ctx,
                      final Object msg,
                      final ChannelPromise promise) {
        final MongoReplyMessage message = (MongoReplyMessage) msg;
        final ByteBuf encodedBuffer = ctx.alloc().buffer(16);
        encodedBuffer.writeInt(0);
        encodedBuffer.writeInt(message.getRequestID());
        encodedBuffer.writeInt(message.getResponseTo());
        encodedBuffer.writeInt(message.getOpCode().value());
        encodedBuffer.writeInt(message.getResponseFlags());
        encodedBuffer.writeLong(message.getCursorID());
        encodedBuffer.writeInt(message.getStartingFrom());
        encodedBuffer.writeInt(message.getNumberReturned());
        for (final DBObject document : message.getDocuments()) {
            final byte[] encodedDocument = BSON.encode(document);
            encodedBuffer.writeBytes(encodedDocument);
        }
        encodedBuffer.setInt(0, encodedBuffer.writerIndex());
        ctx.write(encodedBuffer, promise);
    }
}
