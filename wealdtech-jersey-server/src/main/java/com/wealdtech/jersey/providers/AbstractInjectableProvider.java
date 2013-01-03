package com.wealdtech.jersey.providers;

import java.lang.reflect.Type;

import javax.ws.rs.core.Context;

import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

public abstract class AbstractInjectableProvider<E> extends AbstractHttpContextInjectable<E> implements InjectableProvider<Context, Type>
{
  private transient final Type t;

  public AbstractInjectableProvider(final Type t)
  {
    super();
    this.t = t;
  }

  @Override
  public Injectable<E> getInjectable(final ComponentContext ic, final Context a, final Type c)
  {
    Injectable<E> result = null;
    if (c.equals(t))
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
}