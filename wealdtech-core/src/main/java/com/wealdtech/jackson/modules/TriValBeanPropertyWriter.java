package com.wealdtech.jackson.modules;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.wealdtech.TriVal;

public class TriValBeanPropertyWriter extends BeanPropertyWriter
{
  protected TriValBeanPropertyWriter(BeanPropertyWriter base)
  {
    super(base);
  }

  @Override
  public void serializeAsField(Object bean, JsonGenerator jgen, SerializerProvider prov) throws Exception
  {
    if ((get(bean) == null || TriVal.absent().equals(get(bean))) && _nullSerializer == null)
    {
      return;
    }
    super.serializeAsField(bean, jgen, prov);
  }
}
