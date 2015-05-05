/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.chat.listeners;

import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.wealdtech.DeviceRegistration;
import com.wealdtech.GenericWObject;
import com.wealdtech.User;
import com.wealdtech.chat.Topic;
import com.wealdtech.chat.events.MessageEvent;
import com.wealdtech.chat.services.TopicService;
import com.wealdtech.notifications.service.NotificationService;
import com.wealdtech.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.wealdtech.Preconditions.checkState;

/**
 * Handle events for messages
 */
public class MessageListener
{
  private static final Logger LOG = LoggerFactory.getLogger(MessageListener.class);

  private NotificationService notificationService;
  private TopicService topicService;
  private UserService userService;

  @Inject
  public MessageListener(final NotificationService notificationService,
                         final TopicService topicService,
                         final UserService userService)
  {
    this.notificationService = notificationService;
    this.topicService = topicService;
    this.userService = userService;
  }

  @Subscribe
  @AllowConcurrentEvents
  public void messageEvent(final MessageEvent event)
  {
    switch (event.getType())
    {
      case CREATED:
        LOG.debug("Message created: {}/{}/{}", event.getAppId(), event.getTopicId(), event.getBody());
        // Let the relevant people know that the message exists
        final Topic topic = topicService.obtain(event.getAppId(), event.getTopicId());
        checkState(topic != null, "Failed to obtain topic information");
        final ImmutableSet<User> topicUsers = userService.obtain(topic.getParticipantIds());
        final ImmutableSet.Builder<String> deviceIdsB = ImmutableSet.builder();
        for (final User topicUser : topicUsers)
        {
          for (final DeviceRegistration registration : topicUser.getDeviceRegistrations())
          {
            deviceIdsB.add(registration.getDeviceId());
          }
        }
        notificationService.notify(ImmutableSet.of("APA91bGcxjphD0JKlayw5Yxe4vdUzgjVZW6SmVqSJ5ifiWPixeysbFEDPRb_EtO40Tf2Y4JLtxpOY0_QvDz4n2GW0WLQiuNpOX5LnbgvPEUaNIRX46HG8dNO1RWCQuJsGfrHFL6t08XZafxLVNbLsjTcE3AdFrbbMA"),
                                   GenericWObject.builder()
                                                 .data("topicid", event.getTopicId())
                                                 .data("messageid", event.getBody().getId())
                                                 .data("text", event.getBody().getText())
                                                 .build());
        //        notificationService.notify(deviceIdsB.build(), GenericWObject.builder()
//                                                                     .data("topicid", event.getTopicId())
//                                                                     .data("messageid", event.getBody().getId())
//                                                                     .build());
        break;
      default:
        LOG.error("Unhandled type {} for {}/{}/{}", event.getType(), event.getAppId(), event.getTopicId(), event.getBody());
        break;
    }
  }
}
