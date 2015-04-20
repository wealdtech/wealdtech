/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.roberto.dataprovider.github;

import com.google.common.base.Optional;
import com.wealdtech.GitHubStatus;
import com.wealdtech.roberto.DataProviderConfiguration;
import com.wealdtech.roberto.DataProviderConfigurationState;
import com.wealdtech.roberto.dataprovider.AbstractDataProvider;

/**
 * A test dataprovider to obtain status of the GitHub API
 */
public class GitHubStatusDataProvider extends AbstractDataProvider<GitHubStatus>
{
  private Optional<GitHubStatus> status = Optional.absent();

  public GitHubStatusDataProvider()
  {
    super("GitHub status");
    // No configuration so start providing straight away
    configure(DataProviderConfiguration.builder().updateInterval(1000L).build());
    startProviding();
  }

  @Override
  protected void configure(final DataProviderConfiguration configuration)
  {
    super.configure(configuration);
    setConfigurationState(DataProviderConfigurationState.CONFIGURED);
  }

  @Override
  protected GitHubStatus obtainData()
  {
    return GitHubClient.getInstance().status();
  }
}
