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
import com.mongodb.util.JSONCallback;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.CharsetUtil;
import org.bson.BasicBSONDecoder;

import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 2.0.0
 */
public class MongoDecoder extends ReplayingDecoder<MongoDecoderState> {
    private int messageLength;
    private int requestID;
    private int responseTo;
    private OpCode opCode;

    public MongoDecoder() {
        super(MongoDecoderState.MESSAGE_LENGTH);
    }

    @Override
    protected void decode(final ChannelHandlerContext context,
                          final ByteBuf buffer,
                          final List<Object> out)
            throws Exception {
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        switch (state()) {
            case MESSAGE_LENGTH:
                messageLength = buffer.readInt();
                checkpoint(MongoDecoderState.REQUEST_ID);
            case REQUEST_ID:
                requestID = buffer.readInt();
                checkpoint(MongoDecoderState.RESPONSE_TO);
            case RESPONSE_TO:
                responseTo = buffer.readInt();
                checkpoint(MongoDecoderState.OP_CODE);
            case OP_CODE:
                opCode = OpCode.fromInt(buffer.readInt());
                checkpoint(MongoDecoderState.PAYLOAD);
            case PAYLOAD:
                try {
                    final ByteBuf payload = buffer.readBytes(messageLength - 16);
                    final AbstractMongoMessage message = decode(requestID, responseTo, payload);
                    if (message != null) {
                        out.add(message);
                    }
                } catch (final UnsupportedEncodingException e) {
                } finally {
                    checkpoint(MongoDecoderState.MESSAGE_LENGTH);
                }
        }
    }

    private AbstractMongoMessage decode(final int requestID,
                                        final int responseTo,
                                        final ByteBuf payload)
            throws UnsupportedEncodingException {
        switch (opCode) {
            case OP_REPLY:
                return decodeReply(requestID, responseTo, payload);
            case OP_MSG:
                return decodeMessage(requestID, responseTo, payload);
            case OP_UPDATE:
                return decodeUpdate(requestID, responseTo, payload);
            case OP_INSERT:
                return decodeInsert(requestID, responseTo, payload);
            case OP_QUERY:
                return decodeQuery(requestID, responseTo, payload);
            case OP_GET_MORE:
                return decodeGetMore(requestID, responseTo, payload);
            case OP_DELETE:
                return decodeDelete(requestID, responseTo, payload);
            case OP_KILL_CURSORS:
                return decodeKillCursors(requestID, responseTo, payload);
            default:
                return null;
        }
    }

    /**
     * <pre>
     * int32     responseFlags;  // bit vector - see details below
     * int64     cursorID;       // cursor id if client needs to do get more's
     * int32     startingFrom;   // where in the cursor this reply is starting
     * int32     numberReturned; // number of documents in the reply
     * document* documents;      // documents
     * </pre>
     *
     * @param requestID
     * @param responseTo
     * @param buffer
     * @return
     */
    private MongoReplyMessage decodeReply(final int requestID,
                                          final int responseTo,
                                          final ByteBuf buffer)
            throws UnsupportedEncodingException {
        final int responseFlags = buffer.readInt();
        final long cursorID = buffer.readLong();
        final int startingFrom = buffer.readInt();
        final int numberReturned = buffer.readInt();
        final List<DBObject> documents = readDocuments(buffer);
        return new MongoReplyMessage(
                requestID,
                responseTo,
                responseFlags,
                cursorID,
                startingFrom,
                numberReturned,
                documents);
    }

    /**
     * <pre>
     * cstring   message; // message for the database
     * </pre>
     *
     * @param requestID
     * @param responseTo
     * @param buffer
     * @return
     */
    private MongoMsgMessage decodeMessage(final int requestID,
                                          final int responseTo,
                                          final ByteBuf buffer)
            throws UnsupportedEncodingException {
        final String message = readCString(buffer);
        return new MongoMsgMessage(
                requestID,
                responseTo,
                message);
    }


    /**
     * <pre>
     * int32     ZERO;               // 0 - reserved for future use
     * cstring   fullCollectionName; // "dbname.collectionname"
     * int32     flags;              // bit vector. see below
     * document  selector;           // the query to select the document
     * document  update;             // specification of the update to perform
     * </pre>
     *
     * @param requestID
     * @param responseTo
     * @param buffer
     * @return
     */
    private MongoUpdateMessage decodeUpdate(final int requestID,
                                            final int responseTo,
                                            final ByteBuf buffer)
            throws UnsupportedEncodingException {
        buffer.skipBytes(4);
        final String fullCollectionName = readCString(buffer);
        final int flags = buffer.readInt();
        final DBObject selector = readDocument(buffer);
        final DBObject update = readDocument(buffer);
        return new MongoUpdateMessage(
                requestID,
                responseTo,
                fullCollectionName,
                flags,
                selector,
                update);
    }

    /**
     * <pre>
     * int32     flags;              // bit vector - see below
     * cstring   fullCollectionName; // "dbname.collectionname"
     * document* documents;          // one or more documents to insert into the collection
     * </pre>
     *
     * @param requestID
     * @param responseTo
     * @param buffer
     * @return
     */
    private MongoInsertMessage decodeInsert(final int requestID,
                                            final int responseTo,
                                            final ByteBuf buffer)
            throws UnsupportedEncodingException {
        final int flags = buffer.readInt();
        final String fullCollectionName = readCString(buffer);
        final List<DBObject> documents = readDocuments(buffer);
        return new MongoInsertMessage(
                requestID,
                responseTo,
                flags,
                fullCollectionName,
                documents);
    }

    /**
     * <pre>
     * int32     flags;                    // bit vector of query options.  See below for details.
     * cstring   fullCollectionName ;      // "dbname.collectionname"
     * int32     numberToSkip;             // number of documents to skip
     * int32     numberToReturn;           // number of documents to return
     *                                     //  in the first OP_REPLY batch
     * document  query;                    // query object.  See below for details.
     * [ document  returnFieldsSelector; ] // Optional. Selector indicating the fields
     *                                     //  to return.  See below for details.
     * </pre>
     *
     * @param requestID
     * @param responseTo
     * @param buffer
     * @return
     */
    private MongoQueryMessage decodeQuery(final int requestID,
                                          final int responseTo,
                                          final ByteBuf buffer)
            throws UnsupportedEncodingException {
        final int flags = buffer.readInt();
        final String fullCollectionName = readCString(buffer);
        final int numberToSkip = buffer.readInt();
        final int numberToReturn = buffer.readInt();
        final DBObject query = readDocument(buffer);
        final DBObject returnFieldsSelector = readDocument(buffer);
        return new MongoQueryMessage(
                requestID,
                responseTo,
                flags,
                fullCollectionName,
                numberToSkip,
                numberToReturn,
                query,
                returnFieldsSelector);
    }

    /**
     * <pre>
     * int32     ZERO;               // 0 - reserved for future use
     * cstring   fullCollectionName; // "dbname.collectionname"
     * int32     numberToReturn;     // number of documents to return
     * int64     cursorID;           // cursorID from the OP_REPLY
     * </pre>
     *
     * @param requestID
     * @param responseTo
     * @param buffer
     * @return
     */
    private MongoGetMoreMessage decodeGetMore(final int requestID,
                                              final int responseTo,
                                              final ByteBuf buffer)
            throws UnsupportedEncodingException {
        buffer.skipBytes(4);
        final String fullCollectionName = readCString(buffer);
        final int numberToReturn = buffer.readInt();
        final long cursorID = buffer.readLong();
        return new MongoGetMoreMessage(
                requestID,
                responseTo,
                fullCollectionName,
                numberToReturn,
                cursorID);
    }

    /**
     * <pre>
     * int32     ZERO;               // 0 - reserved for future use
     * cstring   fullCollectionName; // "dbname.collectionname"
     * int32     flags;              // bit vector - see below for details.
     * document  selector;           // query object.  See below for details.
     * </pre>
     *
     * @param requestID
     * @param responseTo
     * @param buffer
     * @return
     */
    private MongoDeleteMessage decodeDelete(final int requestID,
                                            final int responseTo,
                                            final ByteBuf buffer)
            throws UnsupportedEncodingException {
        buffer.skipBytes(4);
        final String fullCollectionName = readCString(buffer);
        final int flags = buffer.readInt();
        final DBObject selector = readDocument(buffer);
        return new MongoDeleteMessage(
                requestID,
                responseTo,
                fullCollectionName,
                flags,
                selector);
    }

    /**
     * <pre>
     * int32     ZERO;              // 0 - reserved for future use
     * int32     numberOfCursorIDs; // number of cursorIDs in message
     * int64*    cursorIDs;         // sequence of cursorIDs to close
     * </pre>
     *
     * @param requestID
     * @param responseTo
     * @param buffer
     * @return
     */
    private MongoKillCursorsMessage decodeKillCursors(final int requestID,
                                                      final int responseTo,
                                                      final ByteBuf buffer) {
        buffer.skipBytes(4);
        final int numberOfCursorIDs = buffer.readInt();
        final long[] cursorIDs = new long[numberOfCursorIDs];
        for (int i = 0; i < numberOfCursorIDs; ++i) {
            cursorIDs[i] = buffer.readLong();
        }
        return new MongoKillCursorsMessage(
                requestID,
                responseTo,
                cursorIDs);
    }

    private String readCString(final ByteBuf buffer)
            throws UnsupportedEncodingException {
        final ByteBuf stringBytes = Unpooled.buffer();
        for (byte nextByte = buffer.readByte();
             nextByte != 0x00;
             nextByte = buffer.readByte()) {
            stringBytes.writeByte(nextByte);
        }
        return new String(stringBytes.array(), CharsetUtil.UTF_8.name());
    }

    private DBObject readDocument(final ByteBuf buffer)
            throws UnsupportedEncodingException {
        if (buffer.readableBytes() == 0) {
            return null;
        } else if (buffer.readableBytes() >= 4) {
            final int documentLength = buffer.getInt(buffer.readerIndex());
            final ByteBuf documentBuffer = buffer.readBytes(documentLength);
            final BasicBSONDecoder decoder = new BasicBSONDecoder();
            final JSONCallback jsonCallback = new JSONCallback();
            decoder.decode(documentBuffer.array(), jsonCallback);
            return (DBObject) jsonCallback.get();
        }
        // TODO Error!
        return null;
    }

    private List<DBObject> readDocuments(final ByteBuf buffer)
            throws UnsupportedEncodingException {
        final List<DBObject> documents = new ArrayList<DBObject>();
        while (buffer.readableBytes() > 0) {
            final DBObject document = readDocument(buffer);
            documents.add(document);
        }
        return documents;
    }
}
