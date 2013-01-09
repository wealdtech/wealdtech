/*
 *    Copyright 2013 Weald Technology Trading Limited
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.wealdtech.configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wealdtech.DataError;
import com.wealdtech.jackson.ObjectMapperFactory;
import com.wealdtech.utils.ResourceLoader;

/**
 * A ConfigurationSource carries out the work to find, parse and
 * return a configuration object.
 *
 * Configurations allow for the creation of objects containing configuration
 * properties which can be accessed by real classes.  They provide
 * a single source for multiple different types of configuration information,
 * retaining the flexibility of file-based configuration with the strict checking
 * and validation available through object-based configuration.
 */
public class ConfigurationSource<T>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationSource.class);

  private static final String DEFAULTFILENAME = "config.json";

  public T getConfiguration(final Class<T> klazz) throws DataError
  {
    return getConfiguration(DEFAULTFILENAME, klazz);
  }

  public T getConfiguration(final String filename, final Class<T> klazz) throws DataError
  {
    final ObjectMapper mapper = ObjectMapperFactory.getDefaultMapper();
    try
    {
      return mapper.readValue(readFile(filename), klazz);
    }
    catch (IOException ioe)
    {
      LOGGER.error("Failed to parse JSON configuration file: {}", ioe.getLocalizedMessage(), ioe);
      throw new DataError("Failed to parse JSON configuration file", ioe);
    }
  }

  private static String readFile(final String filename) throws DataError
  {
    final StringBuilder content = new StringBuilder();
    final String linefeed = System.getProperty("line.separator");
    try (BufferedReader reader = getReader(filename))
    {
      if (reader == null)
      {
        throw new DataError("Failed to find resource \"" + filename + "\"");
      }
      String line = null;
      while ((line = reader.readLine()) != null)
      {
        content.append(line).append(linefeed);
      }
    }
    catch (IOException e)
    {
      LOGGER.warn("Failed to read file: {}", e.getLocalizedMessage());
      LOGGER.warn("Stack trace: {}", e);
      throw new DataError("Failed to read file: " + e.getLocalizedMessage(), e);
    }
    return content.toString();
  }

  private static BufferedReader getReader(final String filename) throws DataError
  {
    final URL fileurl = ResourceLoader.getResource(filename);
    if (fileurl == null)
    {
      return null;
    }
    try
    {
      return Files.newBufferedReader(Paths.get(fileurl.toURI()), Charset.defaultCharset());
    }
    catch (IOException ioe)
    {
      LOGGER.warn("IO exception with {}: {}", fileurl, ioe);
      throw new DataError("Failed to access configuration file \"" + filename + "\"", ioe);
    }
    catch (URISyntaxException use)
    {
      LOGGER.warn("URI issue with {}: {}", fileurl, use);
      throw new DataError("Failed to access configuration file \"" + filename + "\"", use);
    }
  }
}
