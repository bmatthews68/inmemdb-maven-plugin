package com.btmatthews.maven.plugins.inmemdb.db.nosql.orientdb;

import com.btmatthews.utils.monitor.Server;
import com.btmatthews.utils.monitor.ServerFactory;

/**
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 2.0.0
 */
public class OrientDBDocumentDatabaseFactory implements ServerFactory {
    @Override
    public String getServerName() {
        return "orientdb-document";
    }

    @Override
    public Server createServer() {
        return new OrientDBDatabase();
    }
}
