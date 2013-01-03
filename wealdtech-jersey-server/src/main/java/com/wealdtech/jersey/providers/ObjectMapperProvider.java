package com.wealdtech.jersey.providers;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.wealdtech.jackson.ObjectMapperFactory;

/**
 * Provide an objectmapper.
 * <p/>The default objectmapper will be provided, unless an object mapper
 * configuration is passed in to the constructor for this provider in which
 * case those configuration options will override the default.
 */
@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper>
{
  private transient final ObjectMapper mapper;

  @Inject
  public ObjectMapperProvider()
  {
    // TODO work out which configuration to put here
    this.mapper = ObjectMapperFactory.getDefaultMapper();
  }

  @Override
  public ObjectMapper getContext(final Class<?> type)
  {
    return this.mapper;
  }
}