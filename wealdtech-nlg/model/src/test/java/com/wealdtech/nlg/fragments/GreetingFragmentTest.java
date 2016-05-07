/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.nlg.fragments;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Range;
import com.google.common.net.MediaType;
import com.wealdtech.WID;
import com.wealdtech.collect.IntervalMultimap;
import com.wealdtech.nlg.FormatType;
import com.wealdtech.nlg.GenerationModel;
import com.wealdtech.nlg.GenerationParameters;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 *
 */
public class GreetingFragmentTest
{
  @Test
  public void testBasic()
  {
    final GenerationParameters params = GenerationParameters.builder()
                                                            .language("en")
                                                            .familiarity(0)
                                                            .informality(50)
                                                            .format(FormatType.TEXT)
                                                            .mediaType(MediaType.PLAIN_TEXT_UTF_8)
                                                            .build();

    final ImmutableMultimap<String, String> args = ImmutableMultimap.of("first name", "John");

    final IntervalMultimap<Integer, String> selections = new IntervalMultimap<>();
    selections.put(Range.closedOpen(0, 50), "Dear {first name}");
    selections.put(Range.closedOpen(40, 70), "Hi {first name}");
    selections.put(Range.closedOpen(60, 90), "Hi");
    selections.put(Range.closedOpen(90, 100), "Hey");

    final GenerationModel model =
        GenerationModel.builder().id(WID.<GenerationModel>generate()).name("Test model").selections(selections).build();
    final GreetingFragment fragment = new GreetingFragment(model);
    final String generated = fragment.generate(params, args);
    assertEquals(generated, "Hi John");
  }
}
