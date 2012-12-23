package com.btmatthews.maven.plugins.inmemdb.db.nosql.orientdb;

import java.util.concurrent.CountDownLatch;

import com.btmatthews.maven.plugins.inmemdb.Loader;
import com.btmatthews.maven.plugins.inmemdb.db.AbstractNoSQLDatabase;
import com.btmatthews.maven.plugins.inmemdb.ldr.json.JSONLoader;
import com.btmatthews.utils.monitor.Logger;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerLifecycleListener;
import com.orientechnologies.orient.server.OServerMain;
import org.codehaus.jackson.JsonNode;

/**
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 2.0.0
 */
public class OrientDBDatabase extends AbstractNoSQLDatabase implements OServerLifecycleListener {

    private OServer server;

    private CountDownLatch starting = new CountDownLatch(1);

    private CountDownLatch stopping = new CountDownLatch(1);

    @Override
    protected Loader[] getLoaders() {
        return new Loader[]{
                new JSONLoader()
        };
    }

    public void insertObject(final String collection,
                             final JsonNode object,
                             final Logger logger) {
        logger.logInfo("Inserting document into OrientDB database");
        final ODatabaseDocumentTx db = new ODatabaseDocumentTx("remote:localhost/" + getDatabaseName());
        db.open(getUsername(), getPassword());
        try {
            final ODocument doc = new ODocument(collection);
            doc.fromJSON(object.toString());
            doc.save();
        } finally {
            db.close();
        }
    }

    /**
     * Configure and launch the OrientDB database.
     *
     * @param logger Used for logging.
     */
    @Override
    public void start(final Logger logger) {
        try {
            logger.logInfo("Starting OrientDB database server");
            server = OServerMain.create();
            server.registerLifecycleListener(this);
            server.startup(getConfiguration());
            server.activate();
            starting.await();
            logger.logInfo("Started OrientDB database server");
        } catch (final InterruptedException e) {
            logger.logError("Interrupted waiting for server to start", e);
        } catch (final Exception e) {
            logger.logError("Could not configure and launch the OrientDB database", e);
        }
    }

    /**
     * Shutdown the OrientDB database.
     *
     * @param logger Used for logging.
     */
    @Override
    public void stop(final Logger logger) {
        try {
            logger.logInfo("Stopping OrientDB database server");
            server.shutdown();
            stopping.await();
            logger.logInfo("Stopped OrientDB database server");
        } catch (final InterruptedException e) {
            logger.logError("Interrupted waiting for server to stop", e);
        }
    }

    private String getConfiguration() {
        final StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        builder.append("<orient-server>");
        builder.append("<network>");
        builder.append("<protocols>");
        builder.append("<protocol name=\"binary\" implementation=\"com.orientechnologies.orient.server.network.protocol.binary.ONetworkProtocolBinary\"/>");
        builder.append("<protocol name=\"http\" implementation=\"com.orientechnologies.orient.server.network.protocol.http.ONetworkProtocolHttpDb\"/>");
        builder.append("</protocols>");
        builder.append("<listeners>");
        builder.append("<listener ip-address=\"0.0.0.0\" port-range=\"2424-2430\" protocol=\"binary\"/>");
        builder.append("<listener ip-address=\"0.0.0.0\" port-range=\"2480-2490\" protocol=\"http\"/>");
        builder.append("</listeners>");
        builder.append("</network>");
        builder.append("<users>");
        builder.append("<user name=\"");
        builder.append(getUsername());
        builder.append(")\" password=\"");
        builder.append(getPassword());
        builder.append("\" resources=\"*\"/>");
        builder.append("</users>");
        builder.append("<storages>");
        builder.append("<storage path=\"memory:");
        builder.append(getDatabaseName());
        builder.append("\" name=\"");
        builder.append(getDatabaseName());
        builder.append("\" userName=\"");
        builder.append(getUsername());
        builder.append("\" userPassword=\"");
        builder.append(getPassword());
        builder.append("\" loaded-at-startup=\"true\"/>");
        builder.append("</storages>");
        builder.append("<properties>");
        builder.append("<entry name=\"server.cache.staticResources\" value=\"false\"/>");
        builder.append("<entry name=\"log.console.level\" value=\"info\"/>");
        builder.append("<entry name=\"log.file.level\" value=\"fine\"/>");
        builder.append("</properties>");
        builder.append("</orient-server>");
        return builder.toString();
    }

    @Override
    public void onBeforeActivate() {
    }

    @Override
    public void onAfterActivate() {
        starting.countDown();
    }

    @Override
    public void onBeforeDeactivate() {
    }

    @Override
    public void onAfterDeactivate() {
        stopping.countDown();
    }
}
