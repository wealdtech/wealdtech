package com.wealdtech.jackson.modules;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.wealdtech.TriVal;

public class TriValSerializers extends Serializers.Base
{
   @Override
   public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc)
   {
     Class<?> raw = type.getRawClass();
     if (TriVal.class.isAssignableFrom(raw))
     {
       return new TriValSerializer(type);
     }
     return super.findSerializer(config, type, beanDesc);
   }
}
