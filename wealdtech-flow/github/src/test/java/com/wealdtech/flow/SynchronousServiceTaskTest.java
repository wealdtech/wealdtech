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

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;

/**
 *
 */
public class SynchronousServiceTaskTest
{
  @Rule
  public ProcessEngineRule processEngineRule = new ProcessEngineRule();

  @Test
  @Deployment(resources = {"synchronousServiceInvocation.bpmn"})
  public void testServiceInvocationSuccessful()
  {

    final RuntimeService runtimeService = processEngineRule.getRuntimeService();
    final TaskService taskService = processEngineRule.getTaskService();

    // this invocation should NOT fail
    Map<String, Object> variables = Collections.<String, Object>singletonMap(SynchronousServiceTask.SHOULD_FAIL_VAR_NAME, false);

    // start the process instance
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("synchronousServiceInvocation", variables);

    // the process instance is now waiting in the first wait state (user task):
    Task waitStateBefore = taskService.createTaskQuery()
                                      .taskDefinitionKey("waitStateBefore")
                                      .processInstanceId(processInstance.getId())
                                      .singleResult();
    assertNotNull(waitStateBefore);

    // Complete this task. This triggers the synchronous invocation of the
    // service task. This method invocation returns after the service task
    // has been executed and the process instance has advanced to the second waitstate.
    taskService.complete(waitStateBefore.getId());

    // the process instance is now waiting in the second wait state (user task):
    Task waitStateAfter =
        taskService.createTaskQuery().taskDefinitionKey("waitStateAfter").processInstanceId(processInstance.getId()).singleResult();
    assertNotNull(waitStateAfter);

    // check for variable set by the service task:
    variables = runtimeService.getVariables(processInstance.getId());
    assertEquals(variables.get(SynchronousServiceTask.PRICE_VAR_NAME), SynchronousServiceTask.PRICE);
  }

  @Test
  @Deployment(resources = {"synchronousServiceInvocation.bpmn"})
  public void testServiceInvocationFailure()
  {

    final RuntimeService runtimeService = processEngineRule.getRuntimeService();
    final TaskService taskService = processEngineRule.getTaskService();

    // this invocation should fail
    Map<String, Object> variables = Collections.<String, Object>singletonMap(SynchronousServiceTask.SHOULD_FAIL_VAR_NAME, true);

    // start the process instance
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("synchronousServiceInvocation", variables);

    // the process instance is now waiting in the first wait state (user task):
    Task waitStateBefore = taskService.createTaskQuery()
                                      .taskDefinitionKey("waitStateBefore")
                                      .processInstanceId(processInstance.getId())
                                      .singleResult();
    assertNotNull(waitStateBefore);

    // Complete this task. This triggers the synchronous invocation of the service task.
    // This time the service task will fail and the process instance will roll
    // back to it's previous state:
    try
    {
      taskService.complete(waitStateBefore.getId());
      fail("Exception expected.");
    }
    catch (Exception e)
    {
      assertTrue(e.getMessage().contains("Service invocation failure!"));
    }

    // the process instance is still waiting in the first wait state (user
    // task):
    waitStateBefore = taskService.createTaskQuery()
                                 .taskDefinitionKey("waitStateBefore")
                                 .processInstanceId(processInstance.getId())
                                 .singleResult();
    assertNotNull(waitStateBefore);

    // the variable is not present:
    variables = runtimeService.getVariables(processInstance.getId());
    assertNull(variables.get(SynchronousServiceTask.PRICE_VAR_NAME));

  }
}
