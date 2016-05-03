/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.nlp.resources;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.wealdtech.GenericWObject;
import com.wealdtech.ServerError;
import com.wealdtech.utils.ResourceLoader;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.decode.NLPDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 *
 */
@Path("/tokenizer")
@Singleton
public class TokenizerResource
{
  private static final Logger LOG = LoggerFactory.getLogger(TokenizerResource.class);

  private final NLPDecoder decoder;

  @Inject
  public TokenizerResource() throws IOException
  {
    final URL resource = ResourceLoader.getResource("config-decode-en.xml");
    decoder = new NLPDecoder(resource.openStream());
  }

  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public GenericWObject tokenize(@QueryParam("q") final String input)
  {
    final NLPNode[] nodes = decoder.decode(input);
    final ImmutableList.Builder<GenericWObject> obs = ImmutableList.builder();
    for (int i = 1; i < nodes.length; i++)
    {
      final GenericWObject.Builder<?> builder = GenericWObject.builder();
      if (nodes[i].getNamedEntityTag().startsWith("O") || nodes[i].getNamedEntityTag().startsWith("U"))
      {
        // Single-word entity
        builder.data("phrase", nodes[i].getWordForm());
        builder.data("tag", nodes[i].getNamedEntityTag().replace("U-", ""));
      }
      else if (nodes[i].getNamedEntityTag().startsWith("B"))
      {
        builder.data("tag", nodes[i].getNamedEntityTag().replace("B-", ""));
        final List<String> words = Lists.newArrayList();
        words.add(nodes[i++].getWordForm());
        do
        {
          words.add(nodes[i++].getWordForm());
        }
        while (!nodes[i - 1].getNamedEntityTag().startsWith("L"));
        i--;
        final String tempPhrase = Joiner.on(" ").join(words);
        builder.data("phrase", tempPhrase.replaceAll("(?U) ([^\\w]+) ", "$1"));
      }
      else
      {
        throw new ServerError("Error parsing phrase at " + nodes[i].getWordForm() + " (" + nodes[i].getNamedEntityTag() + ")");
      }
      obs.add(builder.build());
    }
//    for (int i = 1; i < nodes.length; i++)
//    {
//      final GenericWObject.Builder<?> builder = GenericWObject.builder();
//      builder.data("word", nodes[i].getWordForm());
//      builder.data("tag", nodes[i].getNamedEntityTag());
//      obs.add(builder.build());
//    }
    return GenericWObject.builder().data("results", obs.build()).build();
  }
}
