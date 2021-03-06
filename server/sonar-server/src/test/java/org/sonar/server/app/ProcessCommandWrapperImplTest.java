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
package org.sonar.server.app;

import java.io.File;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.sonar.api.config.Settings;
import org.sonar.process.DefaultProcessCommands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sonar.process.ProcessEntryPoint.PROPERTY_PROCESS_INDEX;
import static org.sonar.process.ProcessEntryPoint.PROPERTY_SHARED_PATH;

public class ProcessCommandWrapperImplTest {
  private static final int PROCESS_NUMBER = 2;

  @Rule
  public TemporaryFolder temp = new TemporaryFolder();
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private Settings settings = new Settings();

  @Test
  public void requestSQRestart_throws_IAE_if_process_sharedDir_property_not_set() throws Exception {
    ProcessCommandWrapperImpl processCommandWrapper = new ProcessCommandWrapperImpl(settings);

    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("Property process.sharedDir is not set");

    processCommandWrapper.requestSQRestart();
  }

  @Test
  public void requestSQRestart_throws_IAE_if_process_index_property_not_set() throws Exception {
    settings.setProperty(PROPERTY_SHARED_PATH, temp.newFolder().getAbsolutePath());
    ProcessCommandWrapperImpl processCommandWrapper = new ProcessCommandWrapperImpl(settings);

    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("Property process.index is not set");

    processCommandWrapper.requestSQRestart();
  }

  @Test
  public void requestSQRestart_updates_shareMemory_file() throws IOException {
    File tmpDir = temp.newFolder().getAbsoluteFile();
    settings.setProperty(PROPERTY_SHARED_PATH, tmpDir.getAbsolutePath());
    settings.setProperty(PROPERTY_PROCESS_INDEX, PROCESS_NUMBER);

    ProcessCommandWrapperImpl underTest = new ProcessCommandWrapperImpl(settings);
    underTest.requestSQRestart();

    try (DefaultProcessCommands processCommands = DefaultProcessCommands.secondary(tmpDir, PROCESS_NUMBER)) {
      assertThat(processCommands.askedForRestart()).isTrue();
    }
  }

  @Test
  public void notifyOperational_throws_IAE_if_process_sharedDir_property_not_set() throws Exception {
    ProcessCommandWrapperImpl processCommandWrapper = new ProcessCommandWrapperImpl(settings);

    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("Property process.sharedDir is not set");

    processCommandWrapper.notifyOperational();
  }

  @Test
  public void notifyOperational_throws_IAE_if_process_index_property_not_set() throws Exception {
    settings.setProperty(PROPERTY_SHARED_PATH, temp.newFolder().getAbsolutePath());
    ProcessCommandWrapperImpl processCommandWrapper = new ProcessCommandWrapperImpl(settings);

    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("Property process.index is not set");

    processCommandWrapper.notifyOperational();
  }

  @Test
  public void notifyOperational_updates_shareMemory_file() throws IOException {
    File tmpDir = temp.newFolder().getAbsoluteFile();
    settings.setProperty(PROPERTY_SHARED_PATH, tmpDir.getAbsolutePath());
    settings.setProperty(PROPERTY_PROCESS_INDEX, PROCESS_NUMBER);

    ProcessCommandWrapperImpl underTest = new ProcessCommandWrapperImpl(settings);
    underTest.notifyOperational();

    try (DefaultProcessCommands processCommands = DefaultProcessCommands.secondary(tmpDir, PROCESS_NUMBER)) {
      assertThat(processCommands.isOperational()).isTrue();
    }
  }

}
