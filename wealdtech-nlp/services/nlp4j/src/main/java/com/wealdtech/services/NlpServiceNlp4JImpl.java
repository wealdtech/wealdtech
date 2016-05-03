/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.services;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.wealdtech.GenericWObject;
import com.wealdtech.ServerError;
import com.wealdtech.nlp.ParseResults;
import com.wealdtech.nlp.Token;
import com.wealdtech.utils.ResourceLoader;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.decode.NLPDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 *
 */
public class NlpServiceNlp4JImpl implements NlpService
{
  private static final Logger LOG = LoggerFactory.getLogger(NlpServiceNlp4JImpl.class);

  private final NLPDecoder decoder;

  @Inject
  public NlpServiceNlp4JImpl()
  {
    try
    {
      final URL resource = ResourceLoader.getResource("config-decode-en.xml");
      decoder = new NLPDecoder(resource.openStream());
    }
    catch (final IOException ioe)
    {
      LOG.error("Failed to create decoder: ", ioe);
      throw new ServerError("Failed to create decoder", ioe);
    }
  }

  @Override
  public ParseResults parse(final String input)
  {
    final ParseResults.Builder<?> builder = ParseResults.builder();
    builder.input(input);
    final ImmutableList.Builder<Token> tokensB = ImmutableList.builder();
    final ImmutableList.Builder<Token> phrasesB = ImmutableList.builder();

    final NLPNode[] nodes = decoder.decode(input);

    final ImmutableList.Builder<GenericWObject> obs = ImmutableList.builder();
    // Items for multi-token phrases
    Token.Builder<?> phraseB = null;
    List<String> words = null;
    long position = -1;
    for (int i = 1; i < nodes.length; i++)
    {
      // Add every token individually to the tokens list
      final Token.Builder<?> tokenB = Token.builder();
      tokenB.input(nodes[i].getWordForm());
      tokenB.position(i - 1L);
      tokenB.length(1L);
      tokenB.entity(nodes[i].getNamedEntityTag());
      final Token token = tokenB.build();
      tokensB.add(token);

      // Also add as part of a multi-word phrase if appropriate
      if (nodes[i].getNamedEntityTag().startsWith("O") || nodes[i].getNamedEntityTag().startsWith("U"))
      {
        // This is a single word phrase so take most data from the token
        phrasesB.add(Token.builder(token).entity(nodes[i].getNamedEntityTag().replace("U-", "")).build());
      }
      else if (nodes[i].getNamedEntityTag().startsWith("B"))
      {
        // This is the start of a multi-word phrase
        phraseB = Token.builder();
        position = i - 1L;
        phraseB.position(position);
        words = Lists.newArrayList();
        words.add(nodes[i].getWordForm());
      }
      else if (nodes[i].getNamedEntityTag().startsWith("I"))
      {
        // This is the middle of a multi-word phrase.
        words.add(nodes[i].getWordForm());
      }
      else if (nodes[i].getNamedEntityTag().startsWith("L"))
      {
        // This is the last of a multi-word phrase
        words.add(nodes[i].getWordForm());
        final String tempPhrase = Joiner.on(" ").join(words);
        phraseB.input(tempPhrase.replaceAll("(?U) ([^\\w\\d]+) ", "$1"));
        phraseB.length(i - position);
        phraseB.entity(nodes[i].getNamedEntityTag().replace("L-", ""));
        phrasesB.add(phraseB.build());
        phraseB = null;
        words = null;
      }
    }

    builder.tokens(tokensB.build());
    builder.phrases(phrasesB.build());
    return builder.build();
  }
}
