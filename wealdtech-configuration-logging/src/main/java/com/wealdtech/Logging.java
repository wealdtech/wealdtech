/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import com.wealdtech.config.LoggingConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.Map;
import java.util.logging.LogManager;

import static com.wealdtech.Preconditions.checkState;

/**
 *
 */
public class Logging
{
  /**
   * Set the logging level.
   * @param configuration the configuration for the logging system
   */
  public static void setLogging(final LoggingConfiguration configuration)
  {
    checkState(configuration != null,  "Logging configuration is required");

    // Get rid of j.u.l. and install SLF4J
    LogManager.getLogManager().reset();
    SLF4JBridgeHandler.install();

    // Remove any existing appenders
    final ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    rootLogger.setLevel(Level.DEBUG);
    rootLogger.detachAndStopAllAppenders();

    // Set up a console appender with our pattern
    final LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
    final ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
    appender.setContext(loggerContext);
    appender.setName("console");

    // Set up a pattern
    final PatternLayoutEncoder pattern = new PatternLayoutEncoder();
    pattern.setContext(loggerContext);
    pattern.setPattern(configuration.getPattern());

    // Add our appender to the root logger
    pattern.start();
    appender.setEncoder(pattern);
    appender.start();
    rootLogger.addAppender(appender);
    rootLogger.setLevel(configuration.getLevel());

    for (final Map.Entry<String, Level> entry : configuration.getOverrides().entrySet())
    {
      final ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(entry.getKey());
      logger.setLevel(entry.getValue());
    }
  }
}
