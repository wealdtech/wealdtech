/*
 *    Copyright 2013 Weald Technology Trading Limited
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.wealdtech.jersey.providers;

import java.lang.reflect.Type;

import javax.ws.rs.core.Context;
import javax.ws.rs.ext.ContextResolver;

import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

public abstract class AbstractInjectableProvider<E> extends AbstractHttpContextInjectable<E> implements InjectableProvider<Context, Type>, ContextResolver<E>
{
  private final transient Type t;

  public AbstractInjectableProvider(final Type t)
  {
    super();
    this.t = t;
  }

  @Override
  public Injectable<E> getInjectable(final ComponentContext ic, final Context a, final Type c)
  {
    Injectable<E> result = null;
    if (c.equals(this.t))
    {
      result = getInjectable(ic, a);
    }

    return result;
  }

  public Injectable<E> getInjectable(final ComponentContext ic, final Context a)
  {
    return this;
  }

  @Override
  public ComponentScope getScope()
  {
    return ComponentScope.PerRequest;
  }

  @Override
  public E getValue()
  {
    return getValue(null);
  }

  @Override
  public E getContext(Class<?> type)
  {
    return getValue();
  }
}
