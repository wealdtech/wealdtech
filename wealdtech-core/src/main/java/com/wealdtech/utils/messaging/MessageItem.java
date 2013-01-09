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

/**
 * A message item contains a {@link MessageObjects} along with details of
 * the message to send
 * @author jgm
 *
 */
public class MessageItem
{
  private transient final int msgtype;
  private transient final String destination;
  private transient final MessageObjects<?> objects;

  public MessageItem(final int msgtype, final String destination, final MessageObjects<?> objects)
  {
    this.msgtype = msgtype;
    this.destination = destination;
    this.objects = objects;
  }

  public int getMsgType()
  {
    return this.msgtype;
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
