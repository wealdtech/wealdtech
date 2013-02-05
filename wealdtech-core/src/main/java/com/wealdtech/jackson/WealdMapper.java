package com.wealdtech.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wealdtech.jackson.modules.MessageObjectsModule;

/**
 * Provide Jackson object mappers with Weald settings. This provides two object
 * mappers, one for clients (which will be sending REST requests to servers) and
 * one for servers (which will be receiving REST requests from clients). Please
 * ensure that you use the appropriate mapper, otherwise bad things will happen.
 * <p/>
 * If you are unsure which one you should be using, use the client mapper.
 */
public enum WealdMapper
{
  INSTANCE;
  private static final transient ObjectMapper CLIENTMAPPER;
  private static final transient ObjectMapper SERVERMAPPER;

  static
  {
    ObjectMapperConfiguration clientconfiguration = new ObjectMapperConfiguration();
    clientconfiguration.addInjectableValue("AllowPartials", Boolean.FALSE);
    clientconfiguration.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    // TODO necessary?
    clientconfiguration.addModule(new MessageObjectsModule());
    CLIENTMAPPER = new ObjectMapperFactory().build(clientconfiguration);

    ObjectMapperConfiguration serverconfiguration = new ObjectMapperConfiguration();
    serverconfiguration.addInjectableValue("AllowPartials", Boolean.TRUE);
    serverconfiguration.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    serverconfiguration.addModule(new MessageObjectsModule());
    SERVERMAPPER = new ObjectMapperFactory().build(serverconfiguration);
  }

  /**
   * Obtain a client ObjectMapper with Weald settings. Note that this returns a
   * shared instance so the configuration should not be altered.
   *
   * @return An ObjectMapper.
   */
  public static ObjectMapper getMapper()
  {
    return CLIENTMAPPER;
  }

  /**
   * Obtain a server ObjectMapper with Weald settings. Note that this returns a
   * shared instance so the configuration should not be altered.
   *
   * @return An ObjectMapper.
   */
  public static ObjectMapper getServerMapper()
  {
    return SERVERMAPPER;
  }
}
