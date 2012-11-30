package com.btmatthews.maven.plugins.inmemdb;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: Brian
 * Date: 29/11/12
 * Time: 11:16
 * To change this template use File | Settings | File Templates.
 */
public final class MessageUtil {

    /**
     * The base name of the message resource bundle.
     */
    private static final String BUNDLE_NAME = "com.btmatthews.maven.plugins.inmemdb.messages";

    /**
     * The message resource bundle.
     */
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
            .getBundle(BUNDLE_NAME);

    /**
     * Retrieve the message pattern for a given message key from the resource
     * bundle and format the message replacing the place holders with the
     * message arguments.
     *
     * @param messageKey The key of the message in the resource bundle.
     * @param arguments  The message arguments.
     * @return The expanded message string.
     */
    public static String getMessage(final String messageKey,
                                    final Object... arguments) {
        final String messagePattern = RESOURCE_BUNDLE.getString(messageKey);
        return MessageFormat.format(messagePattern, arguments);
    }
}
