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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.wealdtech.DataError;
import com.wealdtech.utils.GuavaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * JDoc is a JSON document which serializes without altering its structure.
 * At current JDoc does not store any type information so this needs to be contained externally
 */
@JsonSerialize(using = JDoc.JDocSerializer.class)
@JsonDeserialize(using = JDoc.JDocDeserializer.class)
public class JDoc
{
  private static final Logger LOG = LoggerFactory.getLogger(JDoc.class);

  @JsonProperty
  private final ImmutableMap<String, Object> data;

  @JsonCreator
  public JDoc(final ImmutableMap<String, Object> data)
  {
    this.data = data;
  }

  @SuppressWarnings("unchecked")
  @JsonIgnore
  public <T> Optional<T> get(final String key, final TypeReference<T> typeRef)
  {
    LOG.trace("Attempting to fetch {} as {}",  key, typeRef.getType());
    final Object val = data.get(key);
    if (val == null)
    {
      LOG.trace("No such key");
      return Optional.absent();
    }
    else
    {
      try
      {
        LOG.trace("Attempting to parse {} as {}",  val, typeRef.getType());
        if (val instanceof JDoc)
        {
          return Optional.of((T)val);
        }
        else
        {
          return Optional.fromNullable((T)WealdMapper.getServerMapper().readValue("\"" + val + "\"", typeRef));
        }
      }
      catch (final IOException ioe)
      {
        throw new DataError.Bad("Failed to parse data: ", ioe);
      }
    }
  }

  @SuppressWarnings("unchecked")
  @JsonIgnore
  public <T> Optional<T> get(final String key, final Class<T> klazz)
  {
    LOG.trace("Attempting to fetch {} as {}",  key, klazz.toString());
    final Object val = data.get(key);
    if (val == null)
    {
      LOG.trace("No such key");
      return Optional.absent();
    }
    else
    {
      try
      {
        LOG.trace("Attempting to parse {} as {}",  val, klazz.toString());
        if (val instanceof JDoc)
        {
          return Optional.of((T)val);
        }
        else
        {
          return Optional.fromNullable((T)WealdMapper.getServerMapper().readValue("\"" + val + "\"", klazz));
        }
      }
      catch (final IOException ioe)
      {
        throw new DataError.Bad("Failed to parse data: ", ioe);
      }
    }
  }

  @Override
  public String toString()
  {
    return MoreObjects.toStringHelper(this).add("data", GuavaUtils.emptyToNull(data)).omitNullValues().toString();
  }

  public static class JDocSerializer extends StdSerializer<JDoc>
  {
    protected JDocSerializer()
    {
      super(JDoc.class);
    }

    @Override
    public void serialize(final JDoc value,
                          final JsonGenerator jgen,
                          final SerializerProvider provider) throws IOException
    {
      jgen.writeStartObject();
      for (final Map.Entry<String, Object> entry : value.data.entrySet())
      {
        jgen.writeFieldName(entry.getKey());
        final Object val = entry.getValue();
        if (val instanceof JDoc)
        {
          serialize((JDoc)val, jgen, provider);
        }
        else
        {
          // Wrap it in quotes
          jgen.writeRawValue("\"" + entry.getValue() + "\"");
        }
      }
      jgen.writeEndObject();
    }
  }

  public static class JDocDeserializer extends StdDeserializer<JDoc>
  {
    private static final long serialVersionUID = 6030347476748849469L;

    protected JDocDeserializer()
    {
      super(JDoc.class);
    }

    @Override
    public JDoc deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException
    {

      if (jp.getCurrentToken() != JsonToken.START_OBJECT)
      {
        throw new IOException("invalid start marker " + jp.getText() + " " + jp.getValueAsString());
      }

      final ImmutableMap.Builder<String, Object> dataB = ImmutableMap.builder();

      // Everything is a string, unless it's an object in which case it's another jdoc
      while (jp.nextToken() != JsonToken.END_OBJECT)
      {
        final String key = jp.getCurrentName();
        jp.nextToken();

        final Object value;
        if (jp.getCurrentToken() == JsonToken.START_OBJECT)
        {
          // Child jdoc; store it as a raw string
          value = deserialize(jp, ctxt);
        }
        else
        {
          // Straight value
          value = jp.getText();
        }
        dataB.put(key, value);
      }
      return new JDoc(dataB.build());
    }
  }
}
