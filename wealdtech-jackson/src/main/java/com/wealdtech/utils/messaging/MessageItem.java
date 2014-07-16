/*
 *    Copyright 2013 Weald Technology Trading Limited
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.wealdtech.utils.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wealdtech.DataError;
import com.wealdtech.utils.StringUtils;

import java.util.Locale;

import static com.wealdtech.Preconditions.checkNotNull;

/**
 * A message item contains a {@link MessageObjects} along with details of
 * the message to send.
 */
public class MessageItem
{
  private final Type msgType;
  private final String destination;
  private final MessageObjects<?> objects;

  public enum Type
  {
    /**
     * Publish a message for general consumption
     */
    PUBLISH,
    /**
     * Place a message on a queue
     */
    QUEUE;

    @Override
    @JsonValue
    public String toString()
    {
        return StringUtils.capitalize(super.toString().toLowerCase(Locale.ENGLISH));
    }

    @JsonCreator
    public static Type fromString(final String type)
    {
      checkNotNull(type, "Message item type is required");
      try
      {
        return valueOf(type.toUpperCase(Locale.ENGLISH));
      }
      catch (IllegalArgumentException iae)
      {
        // N.B. we don't pass the iae as the cause of this exception because
        // this happens during invocation, and in that case the enum handler
        // will report the root cause exception rather than the one we throw.
        throw new DataError.Bad("A message item type supplied is invalid"); // NOPMD
      }
    }
  }

  public MessageItem(final Type msgtype, final String destination, final MessageObjects<?> objects)
  {
    this.msgType = msgtype;
    this.destination = destination;
    this.objects = objects;
  }

  public Type getMsgType()
  {
    return this.msgType;
  }

  public String getDestination()
  {
    return this.destination;
  }

  public MessageObjects<?> getObjects()
  {
    return this.objects;
  }
}
