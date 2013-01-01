package com.wealdtech.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.wealdtech.jackson.modules.WealdJodaModule;

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
  private static transient final ObjectMapper DEFAULTMAPPER;

  static
  {
    DEFAULTMAPPER = new ObjectMapper();
    // Clients need the ability to send empty arrays for updates
    DEFAULTMAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    // Ensure that JSON keys are completely lower-case
    DEFAULTMAPPER.setPropertyNamingStrategy(new LcStrategy());
    // If people send us "" treat as NULL
    // DEFAULTMAPPER.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    // Register our custom serializers and deserializers
    DEFAULTMAPPER.registerModule(new WealdJodaModule());
    // Use Guava custom serializers and deserializers
    DEFAULTMAPPER.registerModule(new GuavaModule());
    // Add flag stating that this is a client mapper
    final InjectableValues clientinject = new InjectableValues.Std().addValue("AllowPartials", Boolean.FALSE);
    DEFAULTMAPPER.setInjectableValues(clientinject);
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

  // private transient final Map<String, ObjectMapper> mappers;
  // private static transient final ObjectMapper CLIENTMAPPER;
  // private static transient final ObjectMapper SERVERMAPPER;
  // static
  // {
  // CLIENTMAPPER = new ObjectMapper();
  // // Clients need the ability to send empty arrays for updates
  // CLIENTMAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
  // // Ensure that JSON keys are completely lower-case
  // CLIENTMAPPER.setPropertyNamingStrategy(new LcStrategy());
  // // If people send us "" treat as NULL
  // //
  // CLIENTMAPPER.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
  // // Register our custom serializers and deserializers
  // CLIENTMAPPER.registerModule(new WealdJodaModule());
  // // Use Guava custom serializers and deserializers
  // CLIENTMAPPER.registerModule(new GuavaModule());
  // // Add flag stating that this is a client mapper
  // final InjectableValues clientinject = new
  // InjectableValues.Std().addValue("AllowPartials", Boolean.FALSE);
  // CLIENTMAPPER.setInjectableValues(clientinject);
  // CLIENTMAPPER.configure(Feature.ALLOW_COMMENTS, true);
  //
  // SERVERMAPPER = new ObjectMapper();
  // SERVERMAPPER.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
  // // SERVERMAPPER.enable(SerializationFeature.INDENT_OUTPUT);
  // // Ensure that JSON keys are completely lower-case
  // SERVERMAPPER.setPropertyNamingStrategy(new LcStrategy());
  // // If people send us "" treat as NULL
  // //
  // SERVERMAPPER.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
  // // TODO see if this makes a difference
  // SERVERMAPPER.enable(MapperFeature.USE_STATIC_TYPING);
  // // Register our custom serializers and deserializers
  // SERVERMAPPER.registerModule(new WealdJodaModule());
  // // Use Guava custom serializers and deserializers
  // SERVERMAPPER.registerModule(new GuavaModule());
  // // Add flag stating that this is a server mapper
  // final InjectableValues serverinject = new
  // InjectableValues.Std().addValue("AllowPartials", Boolean.TRUE);
  // SERVERMAPPER.setInjectableValues(serverinject);
  // SERVERMAPPER.configure(Feature.ALLOW_COMMENTS, true);
  // }
  //
  // /**
  // * Obtain a client ObjectMapper with Weald settings.
  // * Note that this returns a shared instance so the
  // * configuration should not be altered.
  // * @return An ObjectMapper.
  // */
  // public static ObjectMapper getMapper()
  // {
  // return CLIENTMAPPER;
  // }
  //
  // /**
  // * Obtain a server ObjectMapper with Weald settings.
  // * Note that this returns a shared instance so the
  // * configuration should not be altered.
  // * @return An ObjectMapper.
  // */
  // public static ObjectMapper getServerMapper()
  // {
  // return SERVERMAPPER;
  // }
}
