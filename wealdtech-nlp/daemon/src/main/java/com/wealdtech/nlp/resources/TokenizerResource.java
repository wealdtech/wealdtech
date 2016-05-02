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

import com.wealdtech.ServerError;
import com.wealdtech.jersey.exceptions.BadRequestException;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordNamedEntityRecognizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordParser;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordSegmenter;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.util.JCasUtil.select;

/**
 *
 */
@Path("/tokenizer")
public class TokenizerResource
{
  private static final Logger LOG = LoggerFactory.getLogger(TokenizerResource.class);

  private final AnalysisEngine pipeline;
  private final JCas pipelineCas;

  @Inject
  public TokenizerResource()
  {
    try
    {
      pipeline = createEngine(createEngineDescription(createEngineDescription(StanfordSegmenter.class),
                                                      createEngineDescription(StanfordPosTagger.class),
                                                      createEngineDescription(StanfordParser.class),
//                                                      createEngineDescription(StanfordNamedEntityRecognizer.class, StanfordNamedEntityRecognizer.PARAM_VARIANT, "muc.7class.caseless.distsim.crf")));
//                                                                createEngineDescription(StanfordNamedEntityRecognizer.class, StanfordNamedEntityRecognizer.PARAM_VARIANT, "muc.7class.distsim.crf")));
                                                      createEngineDescription(StanfordNamedEntityRecognizer.class, StanfordNamedEntityRecognizer.PARAM_VARIANT, "muc.7class.distsim.crf")));

      pipelineCas = pipeline.newJCas();
//      AggregateBuilder builder = new AggregateBuilder();
//
//      builder.add(AnalysisEngineFactory.createEngineDescription(StanfordSegmenter.class));
//      builder.add(AnalysisEngineFactory.createEngineDescription(StanfordPosTagger.class));
//      builder.add(AnalysisEngineFactory.createEngineDescription(StanfordParser.class));
//      builder.add(AnalysisEngineFactory.createEngineDescription(StanfordNamedEntityRecognizer.class));
//
//      pipeline = builder.createAggregate();
    }
    catch (Exception e)
    {
      throw new ServerError("Failed to initialize pipeline: ", e);
    }
//    AnalysisEngineFactory.createEngine();
//    try
//    {
//      this.pipeline = AnalysisEngineFactory.createEngine();
//    }
//    catch (final Exception e)
//    {
//      LOG.error("Failed to set up tokenizer: ", e);
//      throw new ServerError("Failed to set up tokenizer", e);
//    }
  }

  @GET
  public String tokenize(@QueryParam("q") final String input)
  {
    try
    {
      pipelineCas.reset();
//      final JCas pipelineCas = pipeline.newJCas();
      pipelineCas.setDocumentText(input);
      pipelineCas.setDocumentLanguage("en");
      pipeline.process(pipelineCas);
      final StringBuilder sb = new StringBuilder();
//            for (final CAS cas : pipelineCas)
//            {
                      for (final NamedEntity ne : select(pipelineCas, NamedEntity.class))
                      {
                        sb.append('[');
                        sb.append(ne.getCoveredText());
                        sb.append(' ');
                        sb.append(ne.getValue());
                        sb.append(']');
                      }
                      sb.append('\n');
//              for (final Token token : select(pipelineCas, Token.class))
//              {
//                sb.append('[');
//                sb.append(token.getCoveredText());
//                sb.append(' ');
//                sb.append(token.getPos().getPosValue());
//                sb.append(']');
//              }
//              sb.append('\n');
//            }
      return sb.toString();
    }
    catch (final Exception e)
    {
            LOG.error("Failed to parse input: ", e);
            throw new BadRequestException(e.getMessage(), "Input could not be parsed");
    }
//    try
//    {
//      final JCasIterable en = iteratePipeline(
//          createReaderDescription(StringReader.class, StringReader.PARAM_DOCUMENT_TEXT, input, StringReader.PARAM_LANGUAGE, "en")
//          ,createEngineDescription(StanfordSegmenter.class)
//          ,createEngineDescription(StanfordPosTagger.class)
//          ,createEngineDescription(StanfordParser.class)
//          ,createEngineDescription(StanfordNamedEntityRecognizer.class)
//      );
//      final StringBuilder sb = new StringBuilder();
//      for (final JCas jCas : en)
//      {
//        for (final Token token : select(jCas, Token.class))
//        {
//          sb.append('[');
//          sb.append(token.getCoveredText());
//          sb.append(' ');
//          sb.append(token.getPos().getPosValue());
//          sb.append(']');
//        }
//        sb.append('\n');
//
//                for (final NamedEntity ne : select(jCas, NamedEntity.class))
//                {
//                  sb.append('[');
//                  sb.append(ne.getCoveredText());
//                  sb.append(' ');
//                  sb.append(ne.getValue());
//                  sb.append(']');
//                }
//                sb.append('\n');
//        final AnnotationIndex<Annotation> annotationIndex = jCas.getAnnotationIndex();
//        for (final Annotation annotation : annotationIndex)
//        {
//          sb.append('[');
//          sb.append(annotation.getCoveredText());
//          sb.append(' ');
//          sb.append(annotation.getType());
//          sb.append(']');
//          sb.append('\n');
//        }
//      }
//      return sb.toString();
////      final JCas next = en.iterator().next();
//      //      iteratePipeline();
////      JCas jcas = JCasFactory.createJCas();
////      jcas.setDocumentText(input);
////      pipeline.process(jcas);
////      for(Token token : iterate(jcas, Token.class)){
////          System.out.println(token.getTag());
////      }
//
////      return "foo";
//    }
//    catch (final Exception e)
//    {
//      LOG.error("Failed to parse input: ", e);
//      throw new BadRequestException(e.getMessage(), "Input could not be parsed");
//    }
  }
//  @Consumes({MediaType.APPLICATION_JSON})
//  @Produces({MediaType.APPLICATION_JSON})
//  public ResultSet makeRequest(@Context final User user,
//                               @QueryParam("q") final String input,
//                               @Nullable final ResultSet resultSet)
//  {
//    final CreateEventRequestDefinition def = injector.getInstance(CreateEventRequestDefinition.class);
//    final AdditionalInfo.Builder<?> additionalInfoB = AdditionalInfo.builder();
//    additionalInfoB.data("userid", user.getId());
//    additionalInfoB.data("context", com.wealdtech.contacts.Context.PROFESSIONAL);
//    final AdditionalInfo additionalInfo = additionalInfoB.build();
//
//    return ResultSet.fromDefinition(def, ImmutableMultimap.of("summary", "Test meeting", "timeframe", "[2018-06-01T09:00:00Z,2018-06-01T11:00:00Z)", "participants", "Fred", "participants", "Joe"), additionalInfo);
//  }
}
