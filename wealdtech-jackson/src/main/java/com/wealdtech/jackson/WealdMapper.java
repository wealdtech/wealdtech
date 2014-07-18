package com.wealdtech.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Provide Jackson object mappers with Weald settings. This provides two object mappers, one for clients (which will be sending REST
 * requests to servers) and one for servers (which will be receiving REST requests from clients). Please ensure that you use the
 * appropriate mapper, otherwise bad things will happen.
 * <p/>
 * If you are unsure which one you should be using, use the client mapper.
 */
public enum WealdMapper
{
  INSTANCE;
  private static final transient ObjectMapper CLIENT_MAPPER;
  private static final transient ObjectMapper SERVER_MAPPER;

  static
  {
    ObjectMapperConfiguration clientConfiguration = new ObjectMapperConfiguration();
    clientConfiguration.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    CLIENT_MAPPER = new ObjectMapperFactory().build(clientConfiguration)
                                             .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                                             .enable(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN);

    ObjectMapperConfiguration serverConfiguration = new ObjectMapperConfiguration();
    serverConfiguration.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    SERVER_MAPPER = new ObjectMapperFactory().build(serverConfiguration)
                                             .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                                             .enable(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN);
  }

  /**
   * Obtain a client ObjectMapper with Weald settings. Note that this returns a shared instance so the configuration should not be
   * altered.
   *
   * @return An ObjectMapper.
   */
  public static ObjectMapper getMapper()
  {
    return CLIENT_MAPPER;
  }

  /**
   * Obtain a server ObjectMapper with Weald settings. Note that this returns a shared instance so the configuration should not be
   * altered.
   *
   * @return An ObjectMapper.
   */
  public static ObjectMapper getServerMapper()
  {
    return SERVER_MAPPER;
  }
}
