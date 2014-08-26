/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */
package com.wealdtech.jackson;

import java.util.Locale;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;

/**
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
