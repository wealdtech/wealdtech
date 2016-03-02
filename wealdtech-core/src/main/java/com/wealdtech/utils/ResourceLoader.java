/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.utils;

import com.wealdtech.DataError;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * The ResourceLoader attempts to obtain a resource from
 * a number of locations, from the most local/relevant to
 * the least.
 */
public enum ResourceLoader
{
  INSTANCE;

  /**
   * Obtain a URL to a resource, falling back from more specific
   * to more generic loaders if not found.
   * @param resource the name of the resource to find
   * @return a URL to the resource; can be NULL if the resource is not found
   */
  public static URL getResource(final String resource)
  {
    URL url = null;

    // Obtain from thread
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    if (classLoader != null)
    {
      url = classLoader.getResource(resource);
    }

    if (url == null)
    {
      // No luck; obtain from class
      classLoader = ResourceLoader.class.getClassLoader();
      if (classLoader != null)
      {
        url = classLoader.getResource(resource);
      }
    }

    if (url == null)
    {
      // Still no luck; obtain from classpath
      url = ClassLoader.getSystemResource(resource);
    }

    return url;
  }

  public static String readResource(final String resource)
  {
    final StringBuilder content = new StringBuilder();
    final String linefeed = System.getProperty("line.separator");
    try (BufferedReader reader = getReader(resource))
    {
      if (reader == null)
      {
        throw new DataError("Failed to find resource \"" + resource + "\"");
      }
      String line;
      while ((line = reader.readLine()) != null)
      {
        content.append(line).append(linefeed);
      }
    }
    catch (IOException e)
    {
      throw new DataError("Failed to read file: " + e.getLocalizedMessage(), e);
    }
    return content.toString();
  }

  private static BufferedReader getReader(final String filename)
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
      throw new DataError("Failed to access configuration file \"" + filename + "\"", ioe);
    }
    catch (URISyntaxException use)
    {
      throw new DataError("Failed to access configuration file \"" + filename + "\"", use);
    }
  }
}
