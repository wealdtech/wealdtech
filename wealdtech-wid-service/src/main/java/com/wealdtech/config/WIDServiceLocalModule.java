/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.config;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.wealdtech.services.WIDService;
import com.wealdtech.services.WIDServiceLocalImpl;

/**
 */
public class WIDServiceLocalModule extends AbstractModule
{
  @Override
  protected void configure()
  {
    bind(WIDService.class).to(WIDServiceLocalImpl.class).in(Singleton.class);
  }
}
