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
import com.google.inject.Key;
import com.google.inject.name.Names;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.javax.el.ELContext;
import org.camunda.bpm.engine.impl.javax.el.ELResolver;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

/**
 * A resolver that uses Guice to provide tasks.
 * Tasks must implement JavaDelegate and be listed by name in the module's configuration
 */
public class GuiceElResolver extends ELResolver
{
  private final Injector injector;

  public GuiceElResolver(final Injector injector)
  {
    this.injector = injector;
  }

  @Override
  public Class<?> getCommonPropertyType(final ELContext context, final Object base)
  {
    return Object.class;
  }

  @Override
  public Iterator<FeatureDescriptor> getFeatureDescriptors(final ELContext context, final Object base)
  {
    return null;
  }

  @Override
  public Class<?> getType(final ELContext context, final Object base, final Object property)
  {
    return Object.class;
  }

  @Override
  public Object getValue(final ELContext context, final Object base, final Object property)
  {
    JavaDelegate result = null;
    if (base == null)
    {
      if (!(property instanceof String))
      {
        throw new ProcessEngineException("Property passed to getValue() is not a string");
      }
      result = injector.getInstance(Key.get(JavaDelegate.class, Names.named((String)property)));
    }
    if (result != null)
    {
      context.setPropertyResolved(true);
    }
    return result;
  }

  @Override
  public boolean isReadOnly(final ELContext context, final Object base, final Object property)
  {
    return true;
  }

  @Override
  public void setValue(final ELContext context, final Object base, final Object property, final Object value)
  {
    JavaDelegate result = null;
    if (base == null)
    {
      if (!(property instanceof String))
      {
        throw new ProcessEngineException("Property passed to setValue() is not a string");
      }
      result = injector.getInstance(Key.get(JavaDelegate.class, Names.named((String)property)));
    }
    if (result != null)
    {
      throw new ProcessEngineException("Cannot set value of '" + property + "', it resolves to a Guice instance.");
    }
  }
}
