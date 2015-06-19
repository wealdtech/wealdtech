/*
 * Copyright 2012 - 2015 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.tools.gcm;

import com.google.common.collect.ImmutableSet;
import com.wealdtech.GenericWObject;
import com.wealdtech.services.config.GcmConfiguration;
import com.wealdtech.services.gcm.GcmClient;

/**
 */
public class Push
{
  public static void main(final String[] args)
  {
    final String apiKey = args[0];
    final String recipient = args[1];
    final GenericWObject msg = GenericWObject.deserialize(args[2], GenericWObject.class);

    if (msg == null)
    {
      System.err.println("Invalid message");
      System.exit(-1);
    }

    final GcmClient client = new GcmClient(new GcmConfiguration(apiKey));
    client.sendMessage(ImmutableSet.of(recipient), msg);
  }
}
