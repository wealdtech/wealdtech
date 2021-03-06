/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.mail.mandrill.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.wealdtech.mail.MailActor;

import java.io.IOException;

/**
 * A serializer for mail actors to fit Mandrill's recipient format
 */
public class MandrillRecipientSerializer extends StdSerializer<MailActor>
{
  public MandrillRecipientSerializer()
  {
    super(MailActor.class, true);
  }

  @Override
  public void serialize(final MailActor value, final JsonGenerator gen, final SerializerProvider provider) throws IOException
  {
    gen.writeStartObject();
    gen.writeStringField("name", value.getName());
    gen.writeStringField("email", value.getEmail());
    gen.writeStringField("type", "to");
    gen.writeEndObject();
  }
}
