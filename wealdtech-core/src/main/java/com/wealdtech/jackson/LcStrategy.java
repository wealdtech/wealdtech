package com.wealdtech.jackson;

import java.util.Locale;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;

/*
 * Simple naming strategy to force all JSON keys to lower-case.
 * There are a variety of naming conventions out there, but attempting
 * to remember which one is in use where is a pain.  In addition,
 * separation on word boundaries can case confusion (is it 'usergroup' or
 * 'user group', for example?).  Keeping everything lower-case avoids
 * confusion.
 */
public class LcStrategy extends PropertyNamingStrategy
{
  private static final long serialVersionUID = -3010650892383630260L;

  @Override
  public String nameForField(final MapperConfig<?> config, final AnnotatedField field, final String defaultName)
  {
    return translate(defaultName);
  }
  
  @Override
  public String nameForGetterMethod(final MapperConfig<?> config, final AnnotatedMethod method, final String defaultName)
  {
    return translate(defaultName);
  }

  @Override
  public String nameForSetterMethod(final MapperConfig<?> config, final AnnotatedMethod method, final String defaultName)
  {
    return translate(defaultName);
  }
  
  @Override
  public String nameForConstructorParameter(final MapperConfig<?> config, final AnnotatedParameter parameter, final String defaultName)
  {
    return translate(defaultName);
  }
  
  public String translate(final String propertyName)
  {
    return propertyName.toLowerCase(Locale.ENGLISH);
  }
}
