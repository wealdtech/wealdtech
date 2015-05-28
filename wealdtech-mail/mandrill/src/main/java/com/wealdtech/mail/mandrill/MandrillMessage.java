/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.mail.mandrill;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.wealdtech.mail.MailActor;
import com.wealdtech.mail.mandrill.jackson.MandrillRecipientSerializer;

/**
 * Details of a message to be sent
 */
class MandrillMessage
{
  @JsonProperty("from_name")
  private final String senderName;
  @JsonProperty("from_email")
  private final String senderAddress;

  @JsonProperty("to")
  @JsonSerialize(contentUsing=MandrillRecipientSerializer.class)
  private final ImmutableList<MailActor> recipients;

  @JsonProperty("global_merge_vars")
  private final ImmutableList<ImmutableMap<String, String>> globalMergeVars;

  @JsonProperty("important")
  private final boolean important = false;
  @JsonProperty("track_opens")
  private final boolean trackOpens = true;
  @JsonProperty("track_clicks")
  private final boolean trackClicks = false;
  @JsonProperty("auto_text")
  private final boolean autoText = false;
  @JsonProperty("inline_css")
  private final boolean inlineCss = false;
  @JsonProperty("url_strip_qs")
  private final boolean urlStripQs = false;
  @JsonProperty("preserve_recipients")
  private final boolean preserveRecipients = false;
  @JsonProperty("view_content_link")
  private final boolean viewContentLink = false;
  @JsonProperty("tracking_domain")
  private final String trackingDomain = null;
  @JsonProperty("signing_domain")
  private final String signingDomain = null;
  @JsonProperty("merge")
  private final boolean merge = true;
  @JsonProperty("merge_language")
  private final String mergeLanguage = "mailchimp";

  public MandrillMessage(final MailActor sender,
                         final ImmutableList<MailActor> recipients,
                         ImmutableList<ImmutableMap<String, String>> globalMergeVars)
  {
    this.senderName = sender.getName();
    this.senderAddress = sender.getEmail();
    this.recipients = recipients;
    this.globalMergeVars = globalMergeVars;
  }

  public static class Builder
  {
    private MailActor sender;
    private ImmutableList<MailActor> recipients;
    private ImmutableList<ImmutableMap<String, String>> globalMergeVars;

    public Builder()
    {
    }

    public Builder sender(final MailActor sender)
    {
      this.sender = sender;
      return this;
    }

    public Builder recipients(final ImmutableList<MailActor> recipients)
    {
      this.recipients = recipients;
      return this;
    }

    public Builder globalMergeVars(final ImmutableList<ImmutableMap<String, String>> globalMergeVars)
    {
      this.globalMergeVars = globalMergeVars;
      return this;
    }

    public MandrillMessage build()
    {
      return new MandrillMessage(sender, recipients, globalMergeVars);
    }
  }
}
