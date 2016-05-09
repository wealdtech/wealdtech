/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.repositories;

import com.google.inject.Inject;
import com.wealdtech.datastore.config.Neo4jConfiguration;

/**
 */
//public class Neo4jRepository implements Repository<Graph>
public class Neo4jRepository implements Repository<Void>
{
  private final Neo4jConfiguration configuration;
  private final String url;

  @Inject
  public Neo4jRepository(final Neo4jConfiguration configuration)
  {
    this.configuration = configuration;
    final StringBuilder sb = new StringBuilder(250);
    sb.append("jdbc:postgresql://");
    sb.append(this.configuration.getHost());
    sb.append(':');
    sb.append(this.configuration.getPort());
    sb.append('/');
    sb.append(this.configuration.getName());
    if (this.configuration.getAdditionalParams().isPresent())
    {
      sb.append('?');
      sb.append(this.configuration.getAdditionalParams());
    }
    this.url = sb.toString();
  }

  @Override
  public Void getConnection()
  {
//    Neo4jGraph graph = Neo4jGraph.open("/tmp/neo4jtest");
//
//    final Vertex alice = graph.addVertex(T.id, 1, T.label, "alice");
//    final Vertex bob = graph.addVertex(T.id, 1, T.label, "bob");
//    final Vertex eve = graph.addVertex(T.id, 1, T.label, "eve");
//
//    alice.addEdge("knows", bob);
//    bob.addEdge("knows", alice);
//    eve.addEdge("eavesdrops on", bob);
//
//    return graph;
    return null;
  }
}
