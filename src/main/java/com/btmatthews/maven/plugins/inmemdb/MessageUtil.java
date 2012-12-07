/*
 * Copyright 2011 Brian Matthews
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

package com.btmatthews.maven.plugins.inmemdb;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Helpers used to load localized message strings.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public final class MessageUtil {

    /**
     * The base name of the message resource bundle.
     */
    private static final String BUNDLE_NAME = "com.btmatthews.maven.plugins.inmemdb.messages";

    /**
     * The message resource bundle.
     */
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

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
