package test.com.wealdtech.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.google.common.base.Optional;

public class SampleConfiguration
{
  private transient String testString = "default";
  private transient int testInt = -1;
  private transient Optional<String> testOptional = Optional.absent();
  private transient test.com.wealdtech.configuration.SampleSubConfiguration subconfig;

  @JsonCreator
  public SampleConfiguration(@JsonProperty("test string") final String testString,
                             @JsonProperty("test int") final Integer testInt,
                             @JsonProperty("test optional") final String testOptional,
                             @JsonProperty("sub configuration") final test.com.wealdtech.configuration.SampleSubConfiguration sampleSubConfiguration)
  {
    this.testString = Objects.firstNonNull(testString, this.testString);
    this.testInt = Objects.firstNonNull(testInt, this.testInt);
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
