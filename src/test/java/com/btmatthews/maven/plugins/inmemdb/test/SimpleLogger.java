package com.btmatthews.maven.plugins.inmemdb.test;

import com.btmatthews.utils.monitor.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Brian
 * Date: 23/12/12
 * Time: 08:51
 * To change this template use File | Settings | File Templates.
 */
public class SimpleLogger implements Logger {


    @Override
    public void logInfo(String message) {
        System.out.println("[INFO] " + message);
    }

    @Override
    public void logError(String message) {
        System.err.println("[ERROR] " + message);
    }

    @Override
    public void logError(String message, Throwable cause) {
        System.err.println("[ERROR] " + message);
        cause.printStackTrace(System.err);
    }
}
