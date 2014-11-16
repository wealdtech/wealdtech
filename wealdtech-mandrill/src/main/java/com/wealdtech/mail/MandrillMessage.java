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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;
import com.wealdtech.mail.jackson.MandrillRecipientsSerializer;
import com.wealdtech.mail.jackson.MandrillSenderSerializer;

/**
 * Details of a message to be sent
 */
public class MandrillMessage
{
  @JsonProperty("subject")
  private final String subject;
  @JsonUnwrapped
  @JsonSerialize(using=MandrillSenderSerializer.class)
  private final MailActor sender;
  @JsonSerialize(contentUsing=MandrillRecipientsSerializer.class)
  private final ImmutableList<MailActor> recipients;

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

  public MandrillMessage(final String subject,
                         final MailActor sender,
                         final ImmutableList<MailActor> recipients
                         )
  {
    this.subject = subject;
    this.sender = sender;
    this.recipients = recipients;
  }

  public static class Builder
  {
    private String subject;
    private MailActor sender;
    private ImmutableList<MailActor> recipients;

    public Builder()
    {
    }

    public Builder subject(final String subject)
    {
      this.subject = subject;
      return this;
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

    public MandrillMessage build()
    {
      return new MandrillMessage(subject, sender, recipients);
    }
  }
}
