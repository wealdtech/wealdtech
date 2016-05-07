/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.nlg.services;

import simplenlg.framework.DocumentElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.realiser.english.Realiser;

/**
 *
 */
public class NlgServiceSimpleNlgImpl implements NlgService
{
  private final NLGFactory factory;
  private final Realiser realiser;

  public NlgServiceSimpleNlgImpl()
  {
    final Lexicon lexicon = Lexicon.getDefaultLexicon();
    this.factory = new NLGFactory(lexicon);
    this.realiser = new Realiser(lexicon);
  }

  @Override
  public String generate()
  {
    final DocumentElement sentence = factory.createSentence("my dog is happy");
    return realiser.realiseSentence(sentence);
  }
}
