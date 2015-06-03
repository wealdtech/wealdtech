/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.mail;

import com.google.common.collect.ImmutableList;

/**
 * A response to an attempt to send an email
 */
public class MailResponse
{
  private final String status;
  private final ImmutableList<MailRecipientResponse> recipientResponses;

  public MailResponse(final String status, final ImmutableList<MailRecipientResponse> recipientResponses)
  {
    this.status = status;
    this.recipientResponses = recipientResponses;
  }

  public String getStatus(){return status;}

  public ImmutableList<MailRecipientResponse> getRecipientResponses(){ return recipientResponses; }
}
