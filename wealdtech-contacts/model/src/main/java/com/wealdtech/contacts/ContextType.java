/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.contacts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.wealdtech.DataError;
import com.wealdtech.utils.StringUtils;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import static com.wealdtech.Preconditions.checkNotNull;

/**
 * The type of a context: professional, social, familial
 */
public enum ContextType
{
  /**
   * Professional context
   */
  PROFESSIONAL(1)
  /**
   * Social context
   */
 ,SOCIAL(2)
  /**
   * Familial context
   */
  ,FAMILIAL(3)
  ;

  public final int val;

  private ContextType(final int val)
  {
    this.val = val;
  }

  private static final ImmutableSortedMap<Integer, ContextType> _VALMAP;
  static
  {
    final Map<Integer, ContextType> levelMap = Maps.newHashMap();
    for (final ContextType contextType : ContextType.values())
    {
      levelMap.put(contextType.val, contextType);
    }
    _VALMAP = ImmutableSortedMap.copyOf(levelMap);
  }

  @Override
  @JsonValue
  public String toString()
  {
      return StringUtils.capitalize(super.toString().toLowerCase(Locale.ENGLISH)).replaceAll("_", " ");
  }

  @JsonCreator
  public static ContextType fromString(final String val)
  {
    try
    {
      return valueOf(val.toUpperCase(Locale.ENGLISH).replaceAll(" ", "_"));
    }
    catch (final IllegalArgumentException iae)
    {
      // N.B. we don't pass the iae as the cause of this exception because
      // this happens during invocation, and in that case the enum handler
      // will report the root cause exception rather than the one we throw.
      throw new DataError.Bad("A context type \"" + val + "\" supplied is invalid");
    }
  }

  public static ContextType fromInt(final Integer val)
  {
    checkNotNull(val, "Context type not supplied");
    final ContextType state = _VALMAP.get(val);
    checkNotNull(state, "Context type is invalid");
    return state;
  }

  /**
   * A meta class to allow us to view the internal values of the enum
   */
  public static class Meta
  {
    public static final Meta PROFESSIONAL = new Meta(ContextType.PROFESSIONAL);
    public static final Meta SOCIAL = new Meta(ContextType.SOCIAL);
    public static final Meta FAMILIAL = new Meta(ContextType.FAMILIAL);

    public static final ImmutableCollection<Meta> CONTEXT_TYPES =
        ImmutableSet.of(PROFESSIONAL, SOCIAL, FAMILIAL);

    @JsonSerialize(using = MetaSerializer.class)
    private ContextType type;

    private Meta(final ContextType type)
    {
      this.type = type;
    }
  }

  public static class MetaSerializer extends JsonSerializer<ContextType>
  {
    @Override
    public void serialize(final ContextType value,
                          final JsonGenerator jgen,
                          final SerializerProvider provider) throws IOException
    {
      jgen.writeStartObject();
      jgen.writeObjectField("name", value.toString());
      jgen.writeObjectField("val", value.val);
      jgen.writeEndObject();
    }
  }
}
