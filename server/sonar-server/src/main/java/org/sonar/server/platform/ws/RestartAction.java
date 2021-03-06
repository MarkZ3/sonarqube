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

import org.sonar.api.config.Settings;
import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.Response;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.api.web.UserRole;
import org.sonar.server.app.ProcessCommandWrapper;
import org.sonar.server.platform.Platform;
import org.sonar.server.user.UserSession;

/**
 * Implementation of the {@code restart} action for the System WebService.
 */
public class RestartAction implements SystemWsAction {

  private static final Logger LOGGER = Loggers.get(RestartAction.class);

  private final UserSession userSession;
  private final Settings settings;
  private final Platform platform;
  private final ProcessCommandWrapper processCommandWrapper;

  public RestartAction(UserSession userSession, Settings settings, Platform platform, ProcessCommandWrapper processCommandWrapper) {
    this.userSession = userSession;
    this.settings = settings;
    this.platform = platform;
    this.processCommandWrapper = processCommandWrapper;
  }

  @Override
  public void define(WebService.NewController controller) {
    controller.createAction("restart")
      .setDescription("Restart server. " +
          "In development mode (sonar.web.dev=true), performs a partial and quick restart of only the web server where " +
          "Ruby on Rails extensions are not reloaded. " +
          "In Production mode, require system administration permission and fully restart web server and Elastic Search processes.")
      .setSince("4.3")
      .setPost(true)
      .setHandler(this);
  }

  @Override
  public void handle(Request request, Response response) {
    if (settings.getBoolean("sonar.web.dev")) {
      LOGGER.info("Fast restarting WebServer...");
      platform.restart();
      LOGGER.info("WebServer restarted");
    } else {
      userSession.checkPermission(UserRole.ADMIN);
      LOGGER.info("SonarQube restart requested by {}", userSession.getLogin());
      processCommandWrapper.requestSQRestart();
    }
    response.noContent();
  }

}
