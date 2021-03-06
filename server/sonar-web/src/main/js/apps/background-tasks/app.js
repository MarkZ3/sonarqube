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
import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';

import BackgroundTasksAppContainer from './containers/BackgroundTasksAppContainer';
import rootReducer from './store/reducers';
import configureStore from '../../components/store/configureStore';

import './styles/background-tasks.css';

window.sonarqube.appStarted.then(options => {
  const el = document.querySelector(options.el);

  const store = configureStore(rootReducer);

  ReactDOM.render((
      <Provider store={store}>
        <BackgroundTasksAppContainer options={options}/>
      </Provider>
  ), el);
});
