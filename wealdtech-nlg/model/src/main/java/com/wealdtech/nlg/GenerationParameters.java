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
import com.wealdtech.WObject;

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * Parameters used to help define generation of language
 */
public class GenerationParameters extends WObject<GenerationParameters> implements Comparable<GenerationParameters>
{
  private static final String LANGUAGE = "langauge";
  private static final String INFORMALITY = "informality";
  private static final String FAMILIARITY = "familiarity";
  private static final String FORMAT = "format";
  private static final String MEDIA_TYPE = "mediatype";

  @JsonCreator
  public GenerationParameters(final Map<String, Object> data){super(data);}

  @Override
  protected void validate()
  {
    super.validate();
    checkState(exists(LANGUAGE), "Generation parameters failed validation: missing language");
    checkState(exists(INFORMALITY), "Generation parameters failed validation: missing informality");
    checkState(exists(FAMILIARITY), "Generation parameters failed validation: missing familiarity");
    checkState(exists(FORMAT), "Generation parameters failed validation: missing  format");
    checkState(exists(MEDIA_TYPE), "Generation parameters failed validation: missing media type");
  }

  @JsonIgnore
  public String getLanguage(){ return get(LANGUAGE, String.class).get(); }

  @JsonIgnore
  public FormatType getFormat(){ return get(FORMAT, FormatType.class).get(); }

  @JsonIgnore
  public MediaType getLength(){ return get(MEDIA_TYPE, MediaType.class).get(); }

  @JsonIgnore
  public int getInformality(){ return get(INFORMALITY, Integer.class).get(); }

  @JsonIgnore
  public int getFamiliarity(){ return get(FAMILIARITY, Integer.class).get(); }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<GenerationParameters, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final GenerationParameters prior)
    {
      super(prior);
    }

    public P language(final String language)
    {
      data(LANGUAGE, language);
      return self();
    }

    public P informality(final Integer informality)
    {
      data(INFORMALITY, informality);
      return self();
    }

    public P familiarity(final Integer familiarity)
    {
      data(FAMILIARITY, familiarity);
      return self();
    }

    public P format(final FormatType format)
    {
      data(FORMAT, format);
      return self();
    }
    public P mediaType(final MediaType mediaType)
    {
      data(MEDIA_TYPE, mediaType);
      return self();
    }
    public GenerationParameters build()
    {
      return new GenerationParameters(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final GenerationParameters prior)
  {
    return new Builder(prior);
  }
}
