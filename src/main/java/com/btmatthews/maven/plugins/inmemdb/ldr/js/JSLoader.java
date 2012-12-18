package com.btmatthews.maven.plugins.inmemdb.ldr.js;

import com.btmatthews.maven.plugins.inmemdb.Database;
import com.btmatthews.maven.plugins.inmemdb.Source;
import com.btmatthews.maven.plugins.inmemdb.ldr.AbstractLoader;
import com.btmatthews.utils.monitor.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Brian
 * Date: 07/12/12
 * Time: 19:04
 * To change this template use File | Settings | File Templates.
 */
public class JSLoader extends AbstractLoader {
    @Override
    protected String getExtension() {
        return ".js";
    }

    @Override
    public void load(final Logger logger,
                     final Database database,
                     final Source source) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
