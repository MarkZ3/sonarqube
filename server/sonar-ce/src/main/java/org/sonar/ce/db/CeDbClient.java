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
package org.sonar.ce.db;

import java.util.Map;
import org.sonar.db.Dao;
import org.sonar.db.Database;
import org.sonar.db.DbClient;
import org.sonar.db.MyBatis;
import org.sonar.db.property.PropertiesDao;

public class CeDbClient extends DbClient {
  private ReadOnlyPropertiesDao readOnlyPropertiesDao;

  public CeDbClient(Database database, MyBatis myBatis, Dao... daos) {
    super(database, myBatis, daos);
  }

  @Override
  protected void doOnLoad(Map<Class, Dao> daoByClass) {
    this.readOnlyPropertiesDao = getDao(daoByClass, ReadOnlyPropertiesDao.class);
  }

  @Override
  public PropertiesDao propertiesDao() {
    return this.readOnlyPropertiesDao;
  }
}
