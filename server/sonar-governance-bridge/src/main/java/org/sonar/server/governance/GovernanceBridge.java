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
package org.sonar.server.governance;

import org.sonar.core.platform.ComponentContainer;

/**
 * Interface implemented by the Extension point exposed by the Governance plugin that serves as the unique access point from
 * the whole SQ instance into the Governance plugin.
 */
public interface GovernanceBridge {

  /**
   * Bootstraps the Governance plugin.
   *
   * @param parent the parent ComponentContainer which provides Platform components for Governance to use.
   *
   * @throws IllegalStateException if called more than once
   */
  void startGovernance(ComponentContainer parent);

  /**
   * This method is called when Platform is shutting down.
   */
  void stopGovernance();

}
