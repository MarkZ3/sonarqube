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
package org.sonarqube.ws.client;

import org.sonarqube.ws.client.ce.CeService;
import org.sonarqube.ws.client.component.ComponentsService;
import org.sonarqube.ws.client.issue.IssuesService;
import org.sonarqube.ws.client.measure.MeasuresService;
import org.sonarqube.ws.client.permission.PermissionsService;
import org.sonarqube.ws.client.qualitygate.QualityGatesService;
import org.sonarqube.ws.client.qualityprofile.QualityProfilesService;
import org.sonarqube.ws.client.rule.RulesService;
import org.sonarqube.ws.client.system.SystemService;
import org.sonarqube.ws.client.usertoken.UserTokensService;

/**
 * Entry point of the Java Client for SonarQube Web Services
 *
 * @since 5.3
 */
public class HttpWsClient implements WsClient {

  private final WsConnector wsConnector;
  private final PermissionsService permissionsService;
  private final ComponentsService componentsService;
  private final QualityProfilesService qualityProfilesService;
  private final IssuesService issuesService;
  private final UserTokensService userTokensService;
  private final QualityGatesService qualityGatesService;
  private final MeasuresService measuresService;
  private final SystemService systemService;
  private final CeService ceService;
  private final RulesService rulesService;

  public HttpWsClient(WsConnector wsConnector) {
    this.wsConnector = wsConnector;
    this.permissionsService = new PermissionsService(wsConnector);
    this.componentsService = new ComponentsService(wsConnector);
    this.qualityProfilesService = new QualityProfilesService(wsConnector);
    this.issuesService = new IssuesService(wsConnector);
    this.userTokensService = new UserTokensService(wsConnector);
    this.qualityGatesService = new QualityGatesService(wsConnector);
    this.measuresService = new MeasuresService(wsConnector);
    this.systemService = new SystemService(wsConnector);
    this.ceService = new CeService(wsConnector);
    this.rulesService = new RulesService(wsConnector);
  }

  @Override
  public WsConnector wsConnector() {
    return wsConnector;
  }

  @Override
  public PermissionsService permissions() {
    return this.permissionsService;
  }

  @Override
  public ComponentsService components() {
    return componentsService;
  }

  @Override
  public QualityProfilesService qualityProfiles() {
    return qualityProfilesService;
  }

  @Override
  public IssuesService issues() {
    return issuesService;
  }

  @Override
  public UserTokensService userTokens() {
    return userTokensService;
  }

  @Override
  public QualityGatesService qualityGates() {
    return qualityGatesService;
  }

  @Override
  public MeasuresService measures() {
    return measuresService;
  }

  @Override
  public SystemService system() {
    return systemService;
  }

  @Override
  public CeService ce() {
    return ceService;
  }

  @Override
  public RulesService rules() {
    return rulesService;
  }
}
