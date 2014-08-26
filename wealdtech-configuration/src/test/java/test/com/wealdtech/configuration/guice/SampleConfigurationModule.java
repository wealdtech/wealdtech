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