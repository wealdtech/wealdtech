/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package test.com.wealdtech.mail.config;

import com.google.common.base.MoreObjects;
import com.google.inject.AbstractModule;
import com.wealdtech.configuration.ConfigurationSource;
import com.wealdtech.mail.config.MandrillConfiguration;

/**
 * Application module for
 */
public class ApplicationModule extends AbstractModule
{
  private String configFile = "config-test.json";

  public ApplicationModule(final String configFile)
  {
    super();
    this.configFile = MoreObjects.firstNonNull(configFile, this.configFile);
  }

  @Override
  protected void configure()
  {
    // Bind configuration information
    final MandrillConfiguration configuration = new ConfigurationSource<MandrillConfiguration>().getConfiguration(this.configFile,
                                                                                                                  MandrillConfiguration.class);
    bind(MandrillConfiguration.class).toInstance(configuration);
  }
}
