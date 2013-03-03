package com.wealdtech.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wealdtech.jackson.ObjectMapperFactory;

public class ConfigurationGenerator
{
  public static String generate(final Configuration configuration)
  {
    String result = null;
    ObjectMapper mapper = ObjectMapperFactory.getDefaultMapper();
    try
    {
      result = mapper.writeValueAsString(configuration);
    }
    catch (JsonProcessingException e)
    {
      System.err.println("Failed to generate configuration file: " + e.getLocalizedMessage());
    }
    return result;
  }
}