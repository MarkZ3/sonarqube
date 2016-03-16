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

import com.google.common.collect.Maps;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.util.SortedMap;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;

public class MBeanConnection implements AutoCloseable {

  private final JMXConnector jmxConnector;

  public MBeanConnection(JMXConnector jmxConnector) {
    this.jmxConnector = jmxConnector;
  }

  public <M> M getMBean(String mBeanName, Class<M> mBeanInterfaceClass) {
    try {
      MBeanServerConnection connection = jmxConnector.getMBeanServerConnection();
      return JMX.newMBeanProxy(connection, new ObjectName(mBeanName), mBeanInterfaceClass);
    } catch (Exception e) {
      throw new IllegalStateException("Fail to connect to MBean " + mBeanName, e);
    }
  }

  public SortedMap<String, Object> getSystemState() {
    SortedMap<String, Object> props = Maps.newTreeMap();
    MemoryMXBean memory = getMBean(ManagementFactory.MEMORY_MXBEAN_NAME, MemoryMXBean.class);
    props.put("Heap Committed (bytes)", memory.getHeapMemoryUsage().getCommitted());
    props.put("Heap Init (bytes)", memory.getHeapMemoryUsage().getInit());
    props.put("Heap Max (bytes)", memory.getHeapMemoryUsage().getMax());
    props.put("Heap Used (bytes)", memory.getHeapMemoryUsage().getUsed());
    props.put("Non Heap Committed (bytes)", memory.getNonHeapMemoryUsage().getCommitted());
    props.put("Non Heap Init (bytes)", memory.getNonHeapMemoryUsage().getInit());
    props.put("Non Heap Max (bytes)", memory.getNonHeapMemoryUsage().getMax());
    props.put("Non Heap Used (bytes)", memory.getNonHeapMemoryUsage().getUsed());
    ThreadMXBean thread = getMBean(ManagementFactory.THREAD_MXBEAN_NAME, ThreadMXBean.class);
    props.put("Thread Count", thread.getThreadCount());
    return props;
  }

  @Override
  public void close() {
    try {
      jmxConnector.close();
    } catch (IOException e) {
      throw new IllegalStateException("TODO");
    }
  }
}
