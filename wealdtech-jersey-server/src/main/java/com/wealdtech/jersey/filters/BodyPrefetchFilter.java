/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.jersey.filters;

import com.wealdtech.ServerError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Prefetch and store the full body of the request so that it can be read by
 * subsequent filters without upsetting message body readers.
 * <p/>Required if authentication systems, logging filters, or anything else
 * that wants access to the body of the request.
 */
public class BodyPrefetchFilter implements Filter
{
  private static final Logger LOGGER = LoggerFactory.getLogger(BodyPrefetchFilter.class);

  @Override
  public void init(final FilterConfig filterConfig) throws ServletException
  {
  }

  @Override
  public void destroy()
  {
  }

  @Override
  public void doFilter(final ServletRequest servletRequest,
                       final ServletResponse servletResponse,
                       final FilterChain chain) throws IOException, ServletException
  {
    HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;
    HttpServletResponse httpServletResponse = (HttpServletResponse)servletResponse;
    BufferedRequestWrapper bufferedRequest = new BufferedRequestWrapper(httpServletRequest);
    chain.doFilter(bufferedRequest, httpServletResponse);
  }

  private static final class BufferedRequestWrapper extends HttpServletRequestWrapper
  {
    private byte[] body = null;

    public BufferedRequestWrapper(HttpServletRequest req) throws IOException
    {
      super(req);
      // Store the body
      final InputStream is = req.getInputStream();
      final ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte buf[] = new byte[1024];

      int bytesRead;
      while ((bytesRead = is.read(buf)) > 0)
      {
        baos.write(buf, 0, bytesRead);
      }
      this.body = baos.toByteArray();

      if (this.body.length > 0)
      {
        LOGGER.debug("Body of request is \"{}\"", new String(this.body, "UTF-8"));
      }
    }

    @Override
    public ServletInputStream getInputStream()
    {
      ByteArrayInputStream bais = new ByteArrayInputStream(this.body);
      return new BufferedServletInputStream(bais);
    }
  }

  private static final class BufferedServletInputStream extends ServletInputStream
  {
    private final ByteArrayInputStream inputStream;

    public BufferedServletInputStream(final ByteArrayInputStream inputStream)
    {
      this.inputStream = inputStream;
    }

    @Override
    public int available()
    {
      return this.inputStream.available();
    }

    @Override
    public int read()
    {
      return this.inputStream.read();
    }

    @Override
    public int read(final byte[] buf, final int off, final int len)
    {
      return this.inputStream.read(buf, off, len);
    }

    @Override
    public boolean isFinished()
    {
      throw new ServerError("Not implemented");
    }

    @Override
    public boolean isReady()
    {
      throw new ServerError("Not implemented");
    }

    @Override
    public void setReadListener(final ReadListener readListener)
    {
      // This is required for async IO in servlet 3.1.  Need to implement
      throw new ServerError("Not implemented");
    }
  }
}
