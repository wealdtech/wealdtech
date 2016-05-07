/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.nlg;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.net.MediaType;
import com.wealdtech.WID;
import com.wealdtech.WObject;

import javax.annotation.Nonnull;
import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * A template contains a template that can be used
 */
public class Template extends WObject<Template> implements Comparable<Template>
{
  private static final String NAME = "name";
  private static final String MEDIA_TYPE = "mediatype";
  private static final String TEXT = "text";

  @JsonCreator
  public Template(final Map<String, Object> data){super(data);}

  @Override
  protected void validate()
  {
    super.validate();
    checkState(exists(ID), "Template failed validation: missing ID");
    checkState(exists(NAME), "Template failed validation: missing name");
    checkState(exists(MEDIA_TYPE), "Template failed validation: missing media type");
    checkState(exists(TEXT), "Template failed validation: missing text");
  }

  // We override getId() to make it non-null as we confirm ID's existence in validate()
  @Override
  @Nonnull
  @JsonIgnore
  public WID<Template> getId(){ return super.getId(); }

  @JsonIgnore
  public String getName(){ return get(NAME, String.class).get(); }

  @JsonIgnore
  public String getText(){ return get(TEXT, String.class).get(); }


  public static class Builder<P extends Builder<P>> extends WObject.Builder<Template, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final Template prior)
    {
      super(prior);
    }

    public P name(final String name)
    {
      data(NAME, name);
      return self();
    }

    public P mediaType(final MediaType mediaType)
    {
      data(MEDIA_TYPE, mediaType);
      return self();
    }

    public P text(final String text)
    {
      data(TEXT, text);
      return self();
    }

    public Template build()
    {
      return new Template(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final Template prior)
  {
    return new Builder(prior);
  }
}
