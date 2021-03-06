/*
 * SonarQube
 * Copyright (C) 2009-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.computation.step;

import java.util.Arrays;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.sonar.api.utils.log.LogTester;
import org.sonar.api.utils.log.LoggerLevel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ComputationStepExecutorTest {
  @Rule
  public LogTester logTester = new LogTester();
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void execute_call_execute_on_each_ComputationStep_in_order_returned_by_instances_method() {
    ComputationStep computationStep1 = mockComputationStep("step1");
    ComputationStep computationStep2 = mockComputationStep("step2");
    ComputationStep computationStep3 = mockComputationStep("step3");

    new ComputationStepExecutor(mockComputationSteps(computationStep1, computationStep2, computationStep3))
      .execute();

    InOrder inOrder = inOrder(computationStep1, computationStep2, computationStep3);
    inOrder.verify(computationStep1).execute();
    inOrder.verify(computationStep1).getDescription();
    inOrder.verify(computationStep2).execute();
    inOrder.verify(computationStep2).getDescription();
    inOrder.verify(computationStep3).execute();
    inOrder.verify(computationStep3).getDescription();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void execute_let_exception_thrown_by_ComputationStep_go_up_as_is() {
    String message = "Exception should go up";

    ComputationStep computationStep = mockComputationStep("step1");
    doThrow(new RuntimeException(message))
      .when(computationStep)
      .execute();

    ComputationStepExecutor computationStepExecutor = new ComputationStepExecutor(mockComputationSteps(computationStep));

    expectedException.expect(RuntimeException.class);
    expectedException.expectMessage(message);

    computationStepExecutor.execute();
  }

  @Test
  public void execute_logs_end_timing_for_each_ComputationStep_called() {
    ComputationStep computationStep1 = mockComputationStep("step1");
    ComputationStep computationStep2 = mockComputationStep("step2");

    new ComputationStepExecutor(mockComputationSteps(computationStep1, computationStep2))
        .execute();

    List<String> infoLogs = logTester.logs(LoggerLevel.INFO);
    assertThat(infoLogs).hasSize(2);
    assertThat(infoLogs.get(0)).contains("step1 | time=");
    assertThat(infoLogs.get(1)).contains("step2 | time=");
  }

  private static ComputationSteps mockComputationSteps(ComputationStep... computationSteps) {
    ComputationSteps steps = mock(ComputationSteps.class);
    when(steps.instances()).thenReturn(Arrays.asList(computationSteps));
    return steps;
  }

  private static ComputationStep mockComputationStep(String desc) {
    ComputationStep mock = mock(ComputationStep.class);
    when(mock.getDescription()).thenReturn(desc);
    return mock;
  }
}
