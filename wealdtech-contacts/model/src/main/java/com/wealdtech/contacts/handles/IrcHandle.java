/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.contacts.handles;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.wealdtech.DataError;
import com.wealdtech.contacts.Context;
import com.wealdtech.contacts.uses.Use;

import java.util.Map;

/**
 * A handle related to an IRC channel
 */
@JsonTypeName("irc")
public class IrcHandle extends Handle<IrcHandle> implements Comparable<IrcHandle>
{
  private static final String _TYPE = "irc";

  private static final String SERVER = "server";
  private static final String CHANNEL = "channel";
  private static final String NICK = "nick";

  @JsonCreator
  public IrcHandle(final Map<String, Object> data){ super(data); }

  @JsonIgnore
  public String getServer() { return get(SERVER, String.class).get(); }

  @JsonIgnore
  public String getChannel() { return get(CHANNEL, String.class).get(); }

  @JsonIgnore
  public String getNick() { return get(NICK, String.class).get(); }

  @Override
  public boolean hasUse()
  {
    return false;
  }

  @Override
  public Use toUse(final Context context, final int familiarity, final int formality)
  {
    return null;
  }

  @Override
  protected Map<String, Object> preCreate(Map<String, Object> data)
  {
    // Set our defining types
    data.put(TYPE, _TYPE);
    data.put(KEY, data.get(NICK) + "@" + data.get(CHANNEL) + "@" + data.get(SERVER));

    return super.preCreate(data);
  }

  @Override
  protected void validate()
  {
    super.validate();

    if (!exists(SERVER))
    {
      throw new DataError.Missing("IRC handle failed validation: missing server");
    }

    if (!exists(CHANNEL))
    {
      throw new DataError.Missing("IRC handle failed validation: missing channel");
    }

    if (!exists(NICK))
    {
      throw new DataError.Missing("IRC handle failed validation: missing nick");
    }
  }

  public static class Builder<P extends Builder<P>> extends Handle.Builder<IrcHandle, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final IrcHandle prior)
    {
      super(prior);
    }

    public P server(final String server)
    {
      data(SERVER, server);
      return self();
    }

    public P channel(final String channel)
    {
      data(CHANNEL, channel);
      return self();
    }

    public P nick(final String nick)
    {
      data(NICK, nick);
      return self();
    }

    public IrcHandle build()
    {
      return new IrcHandle(data);
    }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final IrcHandle prior)
  {
    return new Builder(prior);
  }

}
