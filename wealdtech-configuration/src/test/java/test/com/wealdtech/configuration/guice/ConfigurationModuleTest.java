/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package test.com.wealdtech.configuration.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.wealdtech.configuration.ConfigurationSource;
import org.testng.annotations.Test;
import test.com.wealdtech.configuration.SampleConfiguration;
import test.com.wealdtech.configuration.SampleSubConfiguration;

import static org.testng.Assert.assertEquals;

public class ConfigurationModuleTest
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
