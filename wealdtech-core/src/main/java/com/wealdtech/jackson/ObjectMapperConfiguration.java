package com.wealdtech.jackson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.base.Optional;
import com.wealdtech.jackson.modules.WealdJodaModule;

/**
 * The configuration for an object mapper.
 * This contains various settings that can be changed for an object mapper.
 */
public class ObjectMapperConfiguration
{
  private List<Module> modules;
  private Map<JsonParser.Feature, Boolean> parserFeatures;

  private InjectableValues.Std injectableValues;
  private Optional<JsonFactory> factory;
  private Optional<? extends PropertyNamingStrategy> propertyNamingStrategy;
  private Optional<JsonInclude.Include> serializationInclusion;
  
  /**
   * Create a new object mapper configuration,
   * with suitable defaults.
   */
  public ObjectMapperConfiguration()
  {
    this.modules = new ArrayList<>();
    this.parserFeatures = new HashMap<>();
    this.parserFeatures.put(JsonParser.Feature.ALLOW_COMMENTS, true);
    this.injectableValues = new InjectableValues.Std();
    this.modules.add(new GuavaModule());
    this.modules.add(new WealdJodaModule());
    this.factory = Optional.absent();
    this.propertyNamingStrategy = Optional.fromNullable(new LcStrategy());
    this.serializationInclusion = Optional.fromNullable(JsonInclude.Include.NON_NULL);
  }

  /**
   * Clear the additional modules.
   */
  public void clearModules()
  {
    this.modules.clear();
  }
  
  /**
   * Add an additional module.
   * @param module the additional module
   */
  public void addModule(final Module module)
  {
    this.modules.add(module);
  }

  /**
   * Get the additional modules.
   * @return the additional modules
   */
  public List<Module> getModules()
  {
    return this.modules;
  }

  /**
   * Clear the parser features.
   */
  public void clearParserFeatures()
  {
    this.parserFeatures.clear();
  }
  
  /**
   * Add a parser feature.
   * @param feature the parser feature
   * @param value the value of the feature
   */
  public void addParserFeature(final JsonParser.Feature feature, final Boolean value)
  {
    this.parserFeatures.put(feature, value);
  }

  /**
   * Get the parser features.
   * @return the parser features
   */
  public Map<JsonParser.Feature, Boolean> getParserFeatures()
  {
    return this.parserFeatures;
  }

  /**
   * Add an injectable value.
   * @param name the name of the injectable
   * @param value the value of the injectable
   */
  public void addInjectableValue(final String name, final Object value)
  {
    this.injectableValues.addValue(name, value);
  }

  /**
   * Get the injectable values.
   * @return the injectable values
   */
  public InjectableValues getInjectableValues()
  {
    return this.injectableValues;
  }

  /**
   * Set the parsing factory.
   * @param factory the parsing factory
   */
  public void setFactory(final JsonFactory factory)
  {
    this.factory = Optional.fromNullable(factory);
  }

  /**
   * Get the (optional) parsing factory.
   * @return the parsing factory
   */
  public Optional<JsonFactory> getFactory()
  {
    return this.factory;
  }

  /**
   * Set the property naming strategy.
   * @param factory the property naming strategy
   */
  public void setPropertyNamingStrategy(final PropertyNamingStrategy propertyNamingStrategy)
  {
    this.propertyNamingStrategy = Optional.fromNullable(propertyNamingStrategy);
  }

  /**
   * Get the (optional) property naming strategy.
   * @return the property naming strategy
   */
  public Optional<? extends PropertyNamingStrategy> getPropertyNamingStrategy()
  {
    return this.propertyNamingStrategy;
  }


  /**
   * Set the serialization inclusion.
   * @param factory the serialization inclusion
   */
  public void setSerializationInclusion(final JsonInclude.Include serializationInclusion)
  {
    this.serializationInclusion = Optional.fromNullable(serializationInclusion);
  }

  /**
   * Get the (optional) serialization inclusion.
   * @return the serialization inclusion
   */
  public Optional<JsonInclude.Include> getSerializationInclusion()
  {
    return this.serializationInclusion;
  }
}
