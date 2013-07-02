package com.wealdtech.jersey.filters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Prefetch and store the full body of the request so that it can be read by
 * subsequent filters without upsetting message body readers.
 * <p/>Required if authentication systems, logging filters, or anything else
 * wnats access to the body of the request.
 */
public class BodyPrefetchFilter implements Filter
{
  private static final Logger LOGGER = LoggerFactory.getLogger(BodyPrefetchFilter.class);

  @Override
  public void init(final FilterConfig filterConfig) throws ServletException
  {
    // TODO details of which content types and file sizes should be read
  }

  @Override
  public void destroy()
  {
    // TODO
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
    private final ByteArrayInputStream bais;

    public BufferedServletInputStream(final ByteArrayInputStream bais)
    {
      this.bais = bais;
    }

    @Override
    public int available()
    {
      return this.bais.available();
    }

    @Override
    public int read()
    {
      return this.bais.read();
    }

    @Override
    public int read(byte[] buf, int off, int len)
    {
      return this.bais.read(buf, off, len);
    }
  }
}
