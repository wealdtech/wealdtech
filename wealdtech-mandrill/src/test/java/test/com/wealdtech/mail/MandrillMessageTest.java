/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package test.com.wealdtech.mail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableList;
import com.wealdtech.jackson.WealdMapper;
import com.wealdtech.mail.MailActor;
import com.wealdtech.mail.MandrillMessage;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class MandrillMessageTest
{
  @Test(groups = {"base"})
  public void testSimpleMessage() throws JsonProcessingException

  {
    final MandrillMessage message = new MandrillMessage.Builder().sender(new MailActor("Test user", "test@test.com"))
                                                                 .recipients(ImmutableList.of(new MailActor("Test recipient 1",
                                                                                                            "recipient1@test.com"),
                                                                                              new MailActor("Test recipient 2",
                                                                                                            "recipient2@test.com")))
                                                                 .build();

    final String serialized = WealdMapper.getMapper().writeValueAsString(message);
    assertEquals("{\"subject\":\"Test subject\",\"from_name\":\"Test user\",\"from_email\":\"test@test.com\",\"recipients\":[{\"name\":\"Test recipient 1\",\"email\":\"recipient1@test.com\",\"type\":\"to\"},{\"name\":\"Test recipient 2\",\"email\":\"recipient2@test.com\",\"type\":\"to\"}],\"important\":false,\"track_opens\":true,\"track_clicks\":false,\"auto_text\":false,\"inline_css\":false,\"url_strip_qs\":false,\"preserve_recipients\":false,\"view_content_link\":false,\"merge\":true,\"merge_language\":\"mailchimp\"}", serialized);

  }


}
