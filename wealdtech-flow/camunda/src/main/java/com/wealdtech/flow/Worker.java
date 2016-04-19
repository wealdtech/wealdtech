/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.flow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableList;
import com.rabbitmq.client.*;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.spin.plugin.impl.SpinProcessEnginePlugin;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 *
 */
public class Worker
{
  // The name of the queue where we listen for events
  public static final String QUEUE_NAME = "test";

  private final ProcessEngine engine;

  public Worker()
  {
    final ProcessEngineConfigurationImpl processEngineConfiguration;
    processEngineConfiguration =
        (ProcessEngineConfigurationImpl)ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration()
                                                                  .setJdbcDriver("org.postgresql.Driver")
                                                                  .setJdbcUrl("jdbc:postgresql://localhost/camunda")
                                                                  .setJdbcUsername("camunda")
                                                                  .setJdbcPassword("camunda")
                                                                  .setDatabaseSchemaUpdate(
                                                                      ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
                                                                  .setHistory(ProcessEngineConfiguration.HISTORY_FULL)
                                                                  .setJobExecutorActivate(true);
    processEngineConfiguration.setDefaultSerializationFormat("application/json");
    processEngineConfiguration.setProcessEnginePlugins(ImmutableList.<ProcessEnginePlugin>of(new SpinProcessEnginePlugin()));

    engine = processEngineConfiguration.buildProcessEngine();
    final DeploymentBuilder builder = engine.getRepositoryService().createDeployment();
    builder.addClasspathResource("MessageProcess.bpmn").enableDuplicateFiltering(true);
    final Deployment deployment = builder.deploy();
  }

  public void onMessage(final String destination, final String message) throws JsonProcessingException
  {
    System.out.println("Received " + destination + ": '" + message + "'");

    if (message.startsWith("Send"))
    {
      // Start the task
      final VariableMap varMap = Variables.createVariables();
      varMap.put("recipient", message.replace("Send ", ""));

      final RuntimeService runtimeService = engine.getRuntimeService();
      final ProcessInstance instance = runtimeService.startProcessInstanceByKey("MessageProcess", varMap);
    }
    else if (message.startsWith("Response"))
    {
      // Handle response to existing task
      final VariableMap varMap = Variables.createVariables();
      final RuntimeService runtimeService = engine.getRuntimeService();
      // How to obtain the tasks waiting on the response?
      Execution execution = runtimeService.createExecutionQuery()
                                          .messageEventSubscriptionName("ReceivedResponse")
                                          .processInstanceId(message.replace("Response ", ""))
                                          .singleResult();
      runtimeService.signal(execution.getId());
//      final ProcessInstance instance = runtimeService.startProcessInstanceByMessage("ReceivedResponse");
//      runtimeService.signal(message.replace("Response ", "")); // gives cannot signal execution 6004: it has no current activity
//      runtimeService.messageEventReceived("ReceivedResponse", message.replace("Response ", "")); // gives Execution with id '6104' does not have a subscription to a message event with name 'ReceivedResponse': eventSubscriptions is empty
    }
  }

  public static void main(final String[] args) throws java.io.IOException, java.lang.InterruptedException, TimeoutException
  {
    final Worker worker = new Worker();

    final ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    final Connection connection = factory.newConnection();
    final Channel channel = connection.createChannel();
    channel.queueDeclare(QUEUE_NAME, false, false, false, null);

    // Set up a consumer for messages on the queue - just pass it to onMessage() in the worker
    final Consumer consumer = new DefaultConsumer(channel)
    {
      @Override
      public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws
                                                                                                                      IOException
      {
        String message = new String(body, "UTF-8");
        worker.onMessage(envelope.getRoutingKey(), message);
      }
    };
    channel.basicConsume(QUEUE_NAME, true, consumer);
  }
}
