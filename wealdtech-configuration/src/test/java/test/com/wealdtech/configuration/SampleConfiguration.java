/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package test.com.wealdtech.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;

public class SampleConfiguration
{
  private String testString = "default";
  private int testInt = -1;
  private Optional<String> testOptional = Optional.absent();
  private test.com.wealdtech.configuration.SampleSubConfiguration subconfig;

  @JsonCreator
  public SampleConfiguration(@JsonProperty("test string") final String testString,
                             @JsonProperty("test int") final Integer testInt,
                             @JsonProperty("test optional") final String testOptional,
                             @JsonProperty("sub configuration") final test.com.wealdtech.configuration.SampleSubConfiguration sampleSubConfiguration)
  {
    this.testString = MoreObjects.firstNonNull(testString, this.testString);
    this.testInt = MoreObjects.firstNonNull(testInt, this.testInt);
    this.testOptional = Optional.fromNullable(testOptional);
    this.subconfig = sampleSubConfiguration;
  }

  public String getString()
  {
    return this.testString;
  }

  public int getInt()
  {
    return this.testInt;
  }

  public Optional<String> getOptional()
  {
    return this.testOptional;
  }

  public test.com.wealdtech.configuration.SampleSubConfiguration getSubConfiguration()
  {
    return this.subconfig;
  }
}
