package test.com.wealdtech.jersey;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import com.wealdtech.http.JettyServer;
import com.wealdtech.jersey.guice.JerseyServletModule;

public class TestJettyServer
{
  private static final Logger LOGGER = LoggerFactory.getLogger(TestJettyServer.class);

  private transient Server server;

  private transient final Injector injector;

  @Inject
  public TestJettyServer(final Injector injector)
  {
    this.injector = injector;
  }

  public void start() throws Exception // NOPMD
  {
    final int port = 8080;
    LOGGER.info("Starting http server on port {}", port);
    this.server = new Server();

    final ServletContextHandler context = new ServletContextHandler(this.server, "/");
    context.addEventListener(new GuiceServletContextListener()
    {
      @Override
      protected Injector getInjector()
      {
        return TestJettyServer.this.injector;
      }
    });
    context.addFilter(GuiceFilter.class, "/*", null);
    context.addServlet(DefaultServlet.class, "/");

    this.server.start();
    this.server.join();
  }

  public static void main(final String[] args) throws Exception
  {
    // Create an injector with our basic configuration
    final Injector injector = Guice.createInjector(new JerseyServletModule("test.com.wealdtech.jersey.resources"));
    final JettyServer webserver = injector.getInstance(JettyServer.class);
    webserver.start();
  }
}
