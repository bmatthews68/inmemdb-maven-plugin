package com.btmatthews.maven.plugins.inmemdb.db;

import com.btmatthews.utils.monitor.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Brian
 * Date: 23/12/12
 * Time: 08:51
 * To change this template use File | Settings | File Templates.
 */
public class SimpleLogger implements Logger {

    public void logInfo(String message) {
        System.out.println("[INFO] " + message);
    }

    public void logError(String message) {
        System.err.println("[ERROR] " + message);
    }

    public void logError(String message, Throwable cause) {
        System.err.println("[ERROR] " + message);
        cause.printStackTrace(System.err);
    }
}
