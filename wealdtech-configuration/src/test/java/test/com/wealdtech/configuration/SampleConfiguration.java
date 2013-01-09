package test.com.wealdtech.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SampleConfiguration
{
  String teststring;
  int testint;
  String testoptional = "Default";
  SampleSubConfiguration subconfig;

  @JsonCreator
  public SampleConfiguration(@JsonProperty("test string") final String teststring,
                             @JsonProperty("test int") final Integer testint,
                             @JsonProperty("test optional") final String testoptional,
                             @JsonProperty("sub configuration") final SampleSubConfiguration samplesubconfiguration)
  {
    this.teststring = teststring;
    this.testint = testint;
    if (testoptional != null)
    {
      this.testoptional = testoptional;
    }
    this.subconfig = samplesubconfiguration;
  }

  public String getString()
  {
    return this.teststring;
  }

  public int getInt()
  {
    return this.testint;
  }

  public String getOptional()
  {
    return this.testoptional;
  }

  public SampleSubConfiguration getSubConfiguration()
  {
    return this.subconfig;
  }
}
