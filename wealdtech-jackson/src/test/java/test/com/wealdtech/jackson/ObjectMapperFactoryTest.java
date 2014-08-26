/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package test.com.wealdtech.jackson;

import org.testng.annotations.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.wealdtech.jackson.LcStrategy;
import com.wealdtech.jackson.ObjectMapperConfiguration;
import com.wealdtech.jackson.ObjectMapperFactory;
import com.wealdtech.jackson.WealdMapper;
import com.wealdtech.jackson.modules.WealdJodaModule;

import static org.testng.Assert.*;

/**
 *
 */
public class ObjectMapperFactoryTest
{
  @Test
  public void testDefaultMapper()
  {
    assertNotNull(ObjectMapperFactory.getDefaultMapper());
  }

  @Test
  public void testObjectMapperConfiguration()
  {
    ObjectMapperConfiguration configuration = new ObjectMapperConfiguration();
    configuration.setFactory(null);
    configuration.clearModules();
    configuration.addModule(null);
    configuration.addModule(new WealdJodaModule());
    configuration.clearParserFeatures();
    configuration.addParserFeature(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
    configuration.setPropertyNamingStrategy(null);
    configuration.addInjectableValue("testname", "testval");
    configuration.setSerializationInclusion(JsonInclude.Include.ALWAYS);
    assertNotNull(new ObjectMapperFactory().build(configuration));

    configuration.setPropertyNamingStrategy(new LcStrategy());
    configuration.setSerializationInclusion(null);
    assertNotNull(new ObjectMapperFactory().build(configuration));
  }

  @Test
  public void testWealdMapper()
  {
    WealdMapper.getServerMapper();
  }
}
