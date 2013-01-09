package test.com.wealdtech.configuration.guice;

import test.com.wealdtech.configuration.SampleConfiguration;
import test.com.wealdtech.configuration.SampleSubConfiguration;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * Make configuration available to Guice through named values
 */
public class SampleConfigurationModule extends AbstractModule
{
  private final SampleConfiguration configuration;

  public SampleConfigurationModule(final SampleConfiguration configuration)
  {
    super();
    this.configuration = configuration;
  }

  @Override
  protected void configure()
  {
    binder().bind(SampleConfiguration.class).annotatedWith(Names.named("Sample configuration")).toInstance(this.configuration);
    binder().bind(SampleSubConfiguration.class).annotatedWith(Names.named("Sub configuration")).toInstance(this.configuration.getSubConfiguration());
  }
}