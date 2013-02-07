package test.com.wealdtech.configuration.guice;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import test.com.wealdtech.configuration.SampleConfiguration;
import test.com.wealdtech.configuration.SampleSubConfiguration;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.wealdtech.configuration.ConfigurationSource;

public class TestConfigurationModule
{
  @Test
  public void testInjection() throws Exception
  {
    final SampleConfiguration sc = new ConfigurationSource<SampleConfiguration>().getConfiguration("config-test.json", SampleConfiguration.class);
    final Injector injector = Guice.createInjector(new SampleConfigurationModule(sc));
    final SampleSubConfiguration ssc = injector.getInstance(Key.get(SampleSubConfiguration.class, Names.named("Sub configuration")));
    assertEquals(ssc.getString(), "sub string");
  }
}
