/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.mail;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.sparkpost.Client;
import com.sparkpost.exception.SparkPostException;
import com.sparkpost.model.AddressAttributes;
import com.sparkpost.model.RecipientAttributes;
import com.sparkpost.model.TemplateContentAttributes;
import com.sparkpost.model.TransmissionWithRecipientArray;
import com.sparkpost.model.responses.Response;
import com.sparkpost.resources.ResourceTransmissions;
import com.sparkpost.transport.RestConnection;
import com.wealdtech.mail.config.MailConfiguration;
import com.wealdtech.mail.services.MailService;
import com.wealdtech.utils.ResourceLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 */
public class MailServiceSparkPostImpl implements MailService
{
  private String apiKey;
  private MailActor sender;

  @Inject
  public MailServiceSparkPostImpl(final MailConfiguration configuration)
  {
    this.apiKey = configuration.getSecret().orNull();
    this.sender = configuration.getSender().orNull();
  }

  @Override
  public MailResponse sendEmail(final ImmutableList<MailActor> recipients, final String subject, final String textBody, final String htmlBody)
  {
    final Client client = new Client(this.apiKey);

    TransmissionWithRecipientArray transmission = new TransmissionWithRecipientArray();

    // Populate Recipients
    List<RecipientAttributes> recipientArray = new ArrayList<>();
    for (MailActor recipient : recipients)
    {
      RecipientAttributes recipientAttribs = new RecipientAttributes();
      recipientAttribs.setAddress(new AddressAttributes(recipient.getEmail(), recipient.getName(), null));
      recipientArray.add(recipientAttribs);
    }
    transmission.setRecipientArray(recipientArray);

    TemplateContentAttributes contentAttributes = new TemplateContentAttributes();

    contentAttributes.setFrom(new AddressAttributes(sender.getEmail(), sender.getName(), null));

    contentAttributes.setSubject(subject);
    contentAttributes.setHtml(htmlBody);
    contentAttributes.setText(textBody);
    transmission.setContentAttributes(contentAttributes);

    MailResponse response;
    try
    {
      RestConnection connection = new RestConnection(client);
      Response sparkResponse = ResourceTransmissions.create(connection, 0, transmission);
      response = new MailResponse(sparkResponse.getResponseBody(), null);
    }
    catch (SparkPostException spe)
    {
      response = new MailResponse(spe.getMessage(), null);
    }

    return response;
  }

  @Override
  public MailResponse sendTemplate(final String template,
                                   final String subject,
                                   final ImmutableList<ImmutableMap<String, String>> merge,
                                   final ImmutableList<MailActor> recipients)
  {
    // Obtain the template
    final String txtTemplate = ResourceLoader.readResource("emailtemplates" + java.io.File.separator + template + ".tpl");
    final String htmlTemplate = ResourceLoader.readResource("emailtemplates" + java.io.File.separator + template + ".html.tpl");

    // Send it to each recipient in turn with the template changes
    int numRecipients = recipients.size();
    for (int i = 0; i < numRecipients; i++)
    {
      String txtBody = txtTemplate;
      String htmlBody = htmlTemplate;
      for (final Map.Entry<String, String> mergeItem : merge.get(i).entrySet())
      {
        if (txtBody != null)
        {
          txtBody = txtBody.replace("%%" + mergeItem.getKey() + "%%", mergeItem.getValue());
        }
        if (htmlBody != null)
        {
          htmlBody = htmlBody.replace("%%" + mergeItem.getKey() + "%%", mergeItem.getValue());
        }
      }

      sendEmail(ImmutableList.of(recipients.get(i)), subject, txtBody, htmlBody);
    }
    return null;
  }
}
