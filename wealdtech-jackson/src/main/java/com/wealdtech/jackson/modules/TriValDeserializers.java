package com.wealdtech.jackson.modules;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.wealdtech.TriVal;

public class TriValDeserializers extends Deserializers.Base
{
  @Override
  public JsonDeserializer<?> findBeanDeserializer(final JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException
  {
    Class<?> raw = type.getRawClass();
    if(TriVal.class.isAssignableFrom(raw))
    {
      return new TriValDeserializer(type);
    }
    return super.findBeanDeserializer(type, config, beanDesc);
  }
}
