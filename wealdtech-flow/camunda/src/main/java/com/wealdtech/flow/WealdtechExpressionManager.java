/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.flow;

import com.google.inject.Injector;
import org.camunda.bpm.engine.impl.el.ExpressionManager;
import org.camunda.bpm.engine.impl.javax.el.CompositeELResolver;
import org.camunda.bpm.engine.impl.javax.el.ELResolver;

/**
 * An expression manager that attaches the Guice resolver to the list, allowing for tasks to be instantiated by Guice and obtained
 * by name
 */
public class WealdtechExpressionManager extends ExpressionManager
{
  private final Injector injector;

  public WealdtechExpressionManager(final Injector injector)
  {
    this.injector = injector;
  }

  @Override
  protected ELResolver createElResolver()
  {
    final ELResolver superResolver = super.createElResolver();
    final CompositeELResolver resolver = new CompositeELResolver();
    resolver.add(superResolver);
    resolver.add(new GuiceElResolver(injector));
    return resolver;
  }
}
