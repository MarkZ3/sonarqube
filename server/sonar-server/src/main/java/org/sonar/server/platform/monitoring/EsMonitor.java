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

import java.util.LinkedHashMap;
import java.util.Map;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.action.admin.cluster.node.stats.NodeStats;
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsResponse;
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsResponse;
import org.elasticsearch.action.admin.indices.stats.IndexStats;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.common.breaker.CircuitBreaker;
import org.sonar.process.ProcessId;
import org.sonar.process.jmx.EsSettingsMBean;
import org.sonar.server.es.EsClient;

import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;

public class EsMonitor extends BaseMonitorMBean implements EsMonitorMBean {

  private final MBeanConnector mbeanConnector;
  private final EsClient esClient;

  public EsMonitor(MBeanConnector mbeanConnector, EsClient esClient) {
    this.mbeanConnector = mbeanConnector;
    this.esClient = esClient;
  }

  @Override
  public String name() {
    return "ElasticSearch";
  }

  /**
   * MXBean does not allow to return enum {@link ClusterHealthStatus}, so
   * returning String.
   */
  @Override
  public String getState() {
    return getStateAsEnum().name();
  }

  private ClusterHealthStatus getStateAsEnum() {
    return clusterStats().getStatus();
  }

  @Override
  public int getNumberOfNodes() {
    return clusterStats().getNodesStats().getCounts().getTotal();
  }

  @Override
  public LinkedHashMap<String, Object> attributes() {
    LinkedHashMap<String, Object> attributes = new LinkedHashMap<>();

    try (MBeanConnection connection = mbeanConnector.connect(ProcessId.ELASTICSEARCH)) {
      EsSettingsMBean mbean = connection.getMBean(EsSettingsMBean.OBJECT_NAME, EsSettingsMBean.class);
      attributes.put("Cluster Name", mbean.getClusterName());
      attributes.put("Node Name", mbean.getNodeName());
      attributes.put("HTTP Port", mbean.getHttpPort());
    }
    attributes.put("State", getStateAsEnum());
    attributes.put("Indices", indexAttributes());
    attributes.put("Number of Nodes", getNumberOfNodes());
    attributes.put("Nodes", nodeAttributes());
    return attributes;
  }

  private LinkedHashMap<String, LinkedHashMap<String, Object>> indexAttributes() {
    LinkedHashMap<String, LinkedHashMap<String, Object>> indices = new LinkedHashMap<>();
    IndicesStatsResponse indicesStats = esClient.prepareStats().all().get();

    for (Map.Entry<String, IndexStats> indexStats : indicesStats.getIndices().entrySet()) {
      LinkedHashMap<String, Object> attributes = new LinkedHashMap<>();
      indices.put(indexStats.getKey(), attributes);
      attributes.put("Docs", indexStats.getValue().getPrimaries().getDocs().getCount());
      attributes.put("Shards", indexStats.getValue().getShards().length);
      attributes.put("Store Size", byteCountToDisplaySize(indexStats.getValue().getPrimaries().getStore().getSizeInBytes()));
    }
    return indices;
  }

  /**
   * map of {node name -> node attributes}
   */
  private LinkedHashMap<String, LinkedHashMap<String, Object>> nodeAttributes() {
    LinkedHashMap<String, LinkedHashMap<String, Object>> nodes = new LinkedHashMap<>();
    NodesStatsResponse nodesStats = esClient.prepareNodesStats().all().get();
    for (Map.Entry<String, NodeStats> entry : nodesStats.getNodesMap().entrySet()) {

      LinkedHashMap<String, Object> nodeAttributes = new LinkedHashMap<>();
      NodeStats stats = entry.getValue();
      nodes.put(stats.getNode().getName(), nodeAttributes);
      nodeAttributes.put("Address", stats.getNode().getAddress().toString());
      nodeAttributes.put("Type", stats.getNode().isMasterNode() ? "Master" : "Slave");
      nodeAttributes.put("Disk Available", byteCountToDisplaySize(stats.getFs().getTotal().getAvailable().bytes()));
      nodeAttributes.put("Store Size", byteCountToDisplaySize(stats.getIndices().getStore().getSizeInBytes()));
      nodeAttributes.put("Open Files", stats.getProcess().getOpenFileDescriptors());
      nodeAttributes.put("JVM Heap Usage", formatPercent(stats.getJvm().getMem().getHeapUsedPrecent()));
      nodeAttributes.put("JVM Heap Used", byteCountToDisplaySize(stats.getJvm().getMem().getHeapUsed().bytes()));
      nodeAttributes.put("JVM Heap Max", byteCountToDisplaySize(stats.getJvm().getMem().getHeapMax().bytes()));
      nodeAttributes.put("JVM Non Heap Used", byteCountToDisplaySize(stats.getJvm().getMem().getNonHeapUsed().bytes()));
      nodeAttributes.put("JVM Threads", stats.getJvm().getThreads().count());
      nodeAttributes.put("Field Data Memory", byteCountToDisplaySize(stats.getIndices().getFieldData().getMemorySizeInBytes()));
      nodeAttributes.put("Field Data Circuit Breaker Limit", byteCountToDisplaySize(stats.getBreaker().getStats(CircuitBreaker.Name.FIELDDATA).getLimit()));
      nodeAttributes.put("Field Data Circuit Breaker Estimation", byteCountToDisplaySize(stats.getBreaker().getStats(CircuitBreaker.Name.FIELDDATA).getEstimated()));
      nodeAttributes.put("Request Circuit Breaker Limit", byteCountToDisplaySize(stats.getBreaker().getStats(CircuitBreaker.Name.REQUEST).getLimit()));
      nodeAttributes.put("Request Circuit Breaker Estimation", byteCountToDisplaySize(stats.getBreaker().getStats(CircuitBreaker.Name.REQUEST).getEstimated()));
      nodeAttributes.put("Filter Cache Memory", byteCountToDisplaySize(stats.getIndices().getFilterCache().getMemorySizeInBytes()));
      nodeAttributes.put("Query Cache Memory", byteCountToDisplaySize(stats.getIndices().getQueryCache().getMemorySizeInBytes()));
      nodeAttributes.put("ID Cache Memory", byteCountToDisplaySize(stats.getIndices().getIdCache().getMemorySizeInBytes()));
      nodeAttributes.put("Query Cache Memory", byteCountToDisplaySize(stats.getIndices().getQueryCache().getMemorySizeInBytes()));
    }
    return nodes;
  }

  private ClusterStatsResponse clusterStats() {
    return esClient.prepareClusterStats().get();
  }

  private static String formatPercent(long amount) {
    return String.format("%.1f%%", 100 * amount * 1.0D / 100L);
  }
}
