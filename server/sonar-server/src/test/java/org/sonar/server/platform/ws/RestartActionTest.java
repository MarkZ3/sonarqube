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
package org.sonar.server.platform.ws;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.config.Settings;
import org.sonar.api.utils.log.LogTester;
import org.sonar.api.utils.log.LoggerLevel;
import org.sonar.api.web.UserRole;
import org.sonar.server.app.ProcessCommandWrapper;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.platform.Platform;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;
import org.sonar.server.ws.WsTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class RestartActionTest {
  @Rule
  public UserSessionRule userSessionRule = UserSessionRule.standalone();
  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  @Rule
  public LogTester logTester = new LogTester();

  private Settings settings = new Settings();
  private Platform platform = mock(Platform.class);
  private ProcessCommandWrapper processCommandWrapper = mock(ProcessCommandWrapper.class);
  private RestartAction sut = new RestartAction(userSessionRule, settings, platform, processCommandWrapper);
  private WsActionTester actionTester = new WsActionTester(sut);

  @Test
  public void restart_if_dev_mode() throws Exception {
    settings.setProperty("sonar.web.dev", true);

    SystemWs ws = new SystemWs(sut);

    WsTester tester = new WsTester(ws);
    tester.newPostRequest("api/system", "restart").execute();
    verify(platform).restart();
  }

  @Test
  public void requires_admin_permission_if_production_mode() {
    expectedException.expect(ForbiddenException.class);

    actionTester.newRequest().execute();
  }

  @Test
  public void calls_ProcessCommandWrapper_requestForSQRestart_in_production_mode() throws Exception {
    userSessionRule.login().setGlobalPermissions(UserRole.ADMIN);

    actionTester.newRequest().execute();

    verify(processCommandWrapper).requestSQRestart();
  }

  @Test
  public void logs_login_of_authenticated_user_requesting_the_restart_in_production_mode() throws Exception {
    String login = "BigBother";

    userSessionRule.login(login).setGlobalPermissions(UserRole.ADMIN);

    actionTester.newRequest().execute();

    assertThat(logTester.logs(LoggerLevel.INFO))
      .contains("SonarQube restart requested by " + login);
  }

}
