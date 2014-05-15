package com.btmatthews.maven.plugins.inmemdb.ldr.json;

import java.io.IOException;

import com.btmatthews.maven.plugins.inmemdb.Database;
import com.btmatthews.maven.plugins.inmemdb.NoSQLDatabase;
import com.btmatthews.maven.plugins.inmemdb.Source;
import com.btmatthews.maven.plugins.inmemdb.ldr.AbstractLoader;
import com.btmatthews.utils.monitor.Logger;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Load documents from a .json file into a NoSQL document database.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 2.0.0
 */
public class JSONLoader extends AbstractLoader {
    @Override
    protected String getExtension() {
        return ".json";
    }

    @Override
    public void load(final Logger logger,
                     final Database database,
                     final Source source) {
        try {
            final String collection = getTableName(source);
            final ObjectMapper mapper = new ObjectMapper();
            final JsonFactory factory = mapper.getFactory();
            final JsonParser jp = factory.createParser(getReader(source));
            JsonNode actualObj = mapper.readTree(jp);
            while (actualObj != null) {
                ((NoSQLDatabase)database).insertObject(collection, actualObj, logger);
                actualObj = mapper.readTree(jp);
            }
        } catch (final IOException e) {
            logger.logError("Error reading from " + source.getSourceFile(), e);
        }
    }
}
