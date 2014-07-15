package test.com.wealdtech.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SampleSubConfiguration
{
  String testString;

  @JsonCreator
  public SampleSubConfiguration(@JsonProperty("test string") final String testString)
  {
    this.testString = testString;
  }

  public String getString()
  {
    return this.testString;
  }

}
