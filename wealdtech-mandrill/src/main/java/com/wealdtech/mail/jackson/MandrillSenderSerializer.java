/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.mail.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.wealdtech.mail.MailActor;

import java.io.IOException;

/**
 * A serializer for mail actors to fit Mandrill's sender format
 */
public class MandrillSenderSerializer extends StdSerializer<MailActor>
{
  public MandrillSenderSerializer()
  {
    super(MailActor.class, true);
  }

  @Override
  public void serialize(final MailActor value, final JsonGenerator gen, final SerializerProvider provider) throws IOException
  {
    gen.writeStringField("from_name", value.getName());
    gen.writeStringField("from_email", value.getAddress());
  }
}
