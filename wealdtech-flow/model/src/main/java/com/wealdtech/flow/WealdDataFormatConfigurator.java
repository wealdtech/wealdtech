/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.flow;

import com.wealdtech.jackson.WealdMapper;
import org.camunda.spin.impl.json.jackson.format.JacksonJsonDataFormat;
import org.camunda.spin.spi.DataFormatConfigurator;

/**
 * Configurator for Camunda to utilise Weald's object mapper settings
 */
public class WealdDataFormatConfigurator implements DataFormatConfigurator<JacksonJsonDataFormat>
{
  public void configure(JacksonJsonDataFormat dataFormat)
  {
    System.err.println("Configure() called");
    dataFormat.setObjectMapper(WealdMapper.getServerMapper());
  }

  public Class<JacksonJsonDataFormat> getDataFormatClass()
  {
    return JacksonJsonDataFormat.class;
  }
}