package test.com.wealdtech.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SampleSubConfiguration
{
  String teststring;

  @JsonCreator
  public SampleSubConfiguration(@JsonProperty("test string") final String teststring)
  {
    this.teststring = teststring;
  }

  public String getString()
  {
    return this.teststring;
  }

}
