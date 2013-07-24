package com.wealdtech.jackson.modules;

import java.util.List;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.wealdtech.TriVal;

public class TriValBeanSerializerModifier extends BeanSerializerModifier
{
  @Override
  public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties)
  {
    for (int i = 0; i < beanProperties.size(); ++i)
    {
      final BeanPropertyWriter writer = beanProperties.get(i);
      if (TriVal.class.isAssignableFrom(writer.getPropertyType()))
      {
        beanProperties.set(i, new TriValBeanPropertyWriter(writer));
      }
    }

    return beanProperties;
  }
}
