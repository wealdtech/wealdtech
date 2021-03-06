/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */
package com.wealdtech.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.wealdtech.jackson.modules.TriValModule;
import com.wealdtech.jackson.modules.WealdJodaModule;
import com.wealdtech.jackson.modules.WealdMiscModule;

import java.util.Map;

/**
 * Create object mappers, with an option to create non-standard mappers if
 * required. The default object mapper contains defaults suitable for most
 * real-world usage, and additional serializers and deserializers for common
 * types not supported natively by Jackson. Additional features can be
 * configured using ObjectMapperConfiguration.
 */
public class ObjectMapperFactory
{
  // A default mapper, without any configuration tweaks
  private static final transient ObjectMapper DEFAULTMAPPER;

  static
  {
    DEFAULTMAPPER = new ObjectMapper();
    // Clients need the ability to send empty arrays for updates
    DEFAULTMAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    // Ensure that JSON keys are completely lower-case
    DEFAULTMAPPER.setPropertyNamingStrategy(new LcStrategy());
    // Register our custom serializers and deserializers
    DEFAULTMAPPER.registerModule(new WealdJodaModule());
    // Use Guava custom serializers and deserializers
    DEFAULTMAPPER.registerModule(new GuavaModule());
    // Handle the Weald TriVal
    DEFAULTMAPPER.registerModule(new TriValModule());
    // Handle various other types
    DEFAULTMAPPER.registerModule(new WealdMiscModule());
    // Allow comments in JSON
    DEFAULTMAPPER.configure(Feature.ALLOW_COMMENTS, true);
  }

  public ObjectMapperFactory()
  {
  }

  /**
   * Build an objectmapper from a given configuration.
   *
   * @param configuration
   *          the objectmapper configuration
   * @return an objectmapper
   */
  public ObjectMapper build(final ObjectMapperConfiguration configuration)
  {
    final ObjectMapper mapper = new ObjectMapper(configuration.getFactory().orNull());
    for (Module module : configuration.getModules())
    {
      mapper.registerModule(module);
    }
    for (Map.Entry<JsonParser.Feature, Boolean> entry : configuration.getParserFeatures().entrySet())
    {
      mapper.getFactory().configure(entry.getKey(), entry.getValue());
    }
    mapper.setInjectableValues(configuration.getInjectableValues());
    if (configuration.getPropertyNamingStrategy().isPresent())
    {
      mapper.setPropertyNamingStrategy(configuration.getPropertyNamingStrategy().get());
    }
    if (configuration.getSerializationInclusion().isPresent())
    {
      mapper.setSerializationInclusion(configuration.getSerializationInclusion().get());
    }

    return mapper;
  }

  public static ObjectMapper getDefaultMapper()
  {
    return DEFAULTMAPPER;
  }
}
