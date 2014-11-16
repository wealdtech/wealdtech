/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.mail;

/**
 * An actor in a mail process - either sender or recipient
 */
public class MailActor
{
  private String name;
  private String address;

  public MailActor(final String name, final String address)
  {
    this.name = name;
    this.address = address;
  }

  public String getName()
  {
    return name;
  }

  public String getAddress()
  {
    return address;
  }

  public static class Builder
  {
    private String name;
    private String address;

    public Builder(){}

    public Builder name(final String name)
    {
      this.name = name;
      return this;
    }

    public Builder address(final String address)
    {
      this.address = address;
      return this;
    }

    public MailActor build()
    {
      return new MailActor(name, address);
    }
  }
}
