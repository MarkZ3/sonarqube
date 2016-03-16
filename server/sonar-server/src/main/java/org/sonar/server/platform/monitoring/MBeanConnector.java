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
package org.sonar.server.platform.monitoring;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.sonar.process.ProcessId;
import org.sonar.server.app.ProcessCommandWrapper;

public class MBeanConnector {

  private final ProcessCommandWrapper processCommands;

  public MBeanConnector(ProcessCommandWrapper processCommands) {
    this.processCommands = processCommands;
  }

  public MBeanConnection connect(ProcessId processId) {
    try {
      String url = processCommands.getJmxUrl(processId.getIpcIndex());
      JMXConnector jmxConnector = JMXConnectorFactory.newJMXConnector(new JMXServiceURL(url), null);
      jmxConnector.connect();
      return new MBeanConnection(jmxConnector);
    } catch (Exception e) {
      throw new IllegalStateException("TODO", e);
    }
  }
}
