/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.guice;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 */
public class EventBusModule extends AbstractModule
{
  private EventBus eventBus = new EventBus("Default EventBus");

  @Override
  protected void configure()
  {
    // Bind the event bus
    bind(EventBus.class).toInstance(eventBus);

    // Feed every injected item to the event bus to register subscribers
    bindListener(Matchers.any(), new TypeListener()
    {
      public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter)
      {
        typeEncounter.register(new InjectionListener<I>()
        {
          public void afterInjection(I i)
          {
            eventBus.register(i);
          }
        });
      }
    });
  }
}
