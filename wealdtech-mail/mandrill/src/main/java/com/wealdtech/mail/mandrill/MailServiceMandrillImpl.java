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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.wealdtech.ServerError;
import com.wealdtech.mail.MailActor;
import com.wealdtech.mail.MailRecipientResponse;
import com.wealdtech.mail.MailResponse;
import com.wealdtech.mail.config.MailConfiguration;
import com.wealdtech.mail.services.MailService;
import com.wealdtech.retrofit.JacksonRetrofitConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit.RestAdapter;
import retrofit.converter.Converter;

import java.util.List;

import static com.wealdtech.Preconditions.checkState;

/**
 * Mail service using Mandrill
 */
public class MailServiceMandrillImpl implements MailService
{
  private static final Logger LOG = LoggerFactory.getLogger(MailServiceMandrillImpl.class);

  private static final String ENDPOINT = "https://mandrillapp.com/api/1.0";

  private final MailConfiguration configuration;

  public final MandrillService service;

  @Inject
  public MailServiceMandrillImpl(final MailConfiguration configuration)
  {
    this.configuration = configuration;

    checkState(configuration.getSecret().isPresent(), "Mandrill configuration requires API key provided in \"secret\"");
    checkState(configuration.getSender().isPresent(), "Mandrill configuration requires sender");

    final Converter converter = new JacksonRetrofitConverter();
    final RestAdapter adapter =
        new RestAdapter.Builder().setEndpoint(ENDPOINT).setConverter(converter).build();
    this.service = adapter.create(MandrillService.class);
  }

  @Override
  public MailResponse sendEmail(final ImmutableList<MailActor> recipients, final String subject, final String textBody, final String htmlBody)
  {
    throw new ServerError("Not implemented");
  }

  @Override
  public MailResponse sendTemplate(final String template,
                                   final String subject,
                                   final ImmutableList<ImmutableMap<String, String>> merge,
                                   final ImmutableList<MailActor> recipients)
  {
    final MandrillSendRequest request = new MandrillSendRequest(configuration.getSecret().get(), template,
                                                                new MandrillMessage.Builder().recipients(recipients)
                                                                                             .sender(configuration.getSender().get())
                                                                                             .globalMergeVars(merge)
                                                                                             .build());
    final List<MandrillSendResponse> responses = service.sendTemplate(request);
    // Need to translate Mandrill response in to wealdtech response
    final ImmutableList.Builder<MailRecipientResponse> recipientResponsesB = ImmutableList.builder();
    for (final MandrillSendResponse response : responses)
    {
      recipientResponsesB.add(new MailRecipientResponse(response.getAddress(), response.getStatus()));
    }
    return new MailResponse("Success", recipientResponsesB.build());
  }
}

