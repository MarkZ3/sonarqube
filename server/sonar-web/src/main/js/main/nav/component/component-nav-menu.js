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
import qs from 'querystring';
import _ from 'underscore';
import classNames from 'classnames';
import React from 'react';

import LinksMixin from '../links-mixin';
import { translate, getLocalizedDashboardName } from '../../../helpers/l10n';
import {
    getComponentDashboardUrl,
    getComponentFixedDashboardUrl,
    getComponentDashboardManagementUrl
} from '../../../helpers/urls';

const SETTINGS_URLS = [
  '/project/settings',
  '/project/profile',
  '/project/qualitygate',
  '/custom_measures',
  '/action_plans',
  '/project/links',
  '/project_roles',
  '/project/history',
  'background_tasks',
  '/project/key',
  '/project/deletion'
];


export default React.createClass({
  mixins: [LinksMixin],

  isDeveloper() {
    const qualifier = _.last(this.props.component.breadcrumbs).qualifier;
    return qualifier === 'DEV';
  },

  isView() {
    const qualifier = _.last(this.props.component.breadcrumbs).qualifier;
    return qualifier === 'VW' || qualifier === 'SVW';
  },

  periodParameter() {
    const params = qs.parse(window.location.search.substr(1));
    return params.period ? `&period=${params.period}` : '';
  },

  getPeriod() {
    const params = qs.parse(window.location.search.substr(1));
    return params.period;
  },

  isFixedDashboardActive() {
    const path = window.location.pathname;
    return path.indexOf('/overview') === 0;
  },

  isCustomDashboardActive(customDashboard) {
    const path = window.location.pathname;
    const params = qs.parse(window.location.search.substr(1));
    return path.indexOf('/dashboard') === 0 && params['did'] === `${customDashboard.key}`;
  },

  isCustomDashboardsActive () {
    const dashboards = this.props.component.dashboards;
    return _.any(dashboards, this.isCustomDashboardActive) ||
        this.isDashboardManagementActive() ||
        this.isDefaultDeveloperDashboardActive();
  },

  isDefaultDeveloperDashboardActive() {
    const path = window.location.pathname;
    return this.isDeveloper() && path.indexOf('/dashboard') === 0;
  },

  isDashboardManagementActive () {
    const path = window.location.pathname;
    return path.indexOf('/dashboards') === 0;
  },

  renderOverviewLink() {
    const url = getComponentFixedDashboardUrl(this.props.component.key, '');
    const name = <i className="icon-home"/>;
    const className = classNames({ active: this.isFixedDashboardActive() });
    return (
        <li key="overview" className={className}>
          <a href={url}>{name}</a>
        </li>
    );
  },

  renderCustomDashboard(customDashboard) {
    const key = 'custom-dashboard-' + customDashboard.key;
    const url = getComponentDashboardUrl(this.props.component.key, customDashboard.key, this.getPeriod());
    const name = getLocalizedDashboardName(customDashboard.name);
    const className = classNames({ active: this.isCustomDashboardActive(customDashboard) });
    return <li key={key} className={className}>
      <a href={url}>{name}</a>
    </li>;
  },

  renderCustomDashboards() {
    const dashboards = this.props.component.dashboards.map(this.renderCustomDashboard);
    const className = classNames('dropdown', { active: this.isCustomDashboardsActive() });
    const managementLink = this.renderDashboardsManagementLink();
    return <li className={className}>
      <a className="dropdown-toggle" data-toggle="dropdown" href="#">
        {translate('layout.dashboards')}&nbsp;
        <i className="icon-dropdown"/>
      </a>
      <ul className="dropdown-menu">
        {dashboards}
        {managementLink && <li className="divider"/>}
        {managementLink}
      </ul>
    </li>;
  },

  renderDashboardsManagementLink() {
    if (!window.SS.user) {
      return null;
    }
    const key = 'dashboard-management';
    const url = getComponentDashboardManagementUrl(this.props.component.key);
    const name = translate('dashboard.manage_dashboards');
    const className = classNames('pill-right', { active: this.isDashboardManagementActive() });
    return <li key={key} className={className}>
      <a className="note" href={url}>{name}</a>
    </li>;
  },

  renderCodeLink() {
    if (this.isView() || this.isDeveloper()) {
      return null;
    }

    const url = `/code/index?id=${encodeURIComponent(this.props.component.key)}`;
    return this.renderLink(url, translate('code.page'), '/code');
  },

  renderProjectsLink() {
    if (!this.isView()) {
      return null;
    }

    const url = `/view_projects/index?id=${encodeURIComponent(this.props.component.key)}`;
    return this.renderLink(url, translate('view_projects.page'), '/view_projects');
  },

  renderComponentIssuesLink() {
    const url = `/component_issues/index?id=${encodeURIComponent(this.props.component.key)}`;
    return this.renderLink(url, translate('issues.page'), '/component_issues');
  },

  renderComponentMeasuresLink() {
    const url = `/component_measures/?id=${encodeURIComponent(this.props.component.key)}`;
    return this.renderLink(url, translate('layout.measures'), '/component_measures');
  },

  renderAdministration() {
    const shouldShowAdministration =
        this.props.conf.showActionPlans ||
        this.props.conf.showBackgroundTasks ||
        this.props.conf.showDeletion ||
        this.props.conf.showHistory ||
        this.props.conf.showLinks ||
        this.props.conf.showManualMeasures ||
        this.props.conf.showPermissions ||
        this.props.conf.showQualityGates ||
        this.props.conf.showQualityProfiles ||
        this.props.conf.showSettings ||
        this.props.conf.showUpdateKey;
    if (!shouldShowAdministration) {
      return null;
    }
    const isSettingsActive = SETTINGS_URLS.some(url => {
      return window.location.href.indexOf(url) !== -1;
    });
    const className = 'dropdown' + (isSettingsActive ? ' active' : '');
    return (
        <li className={className}>
          <a className="dropdown-toggle navbar-admin-link" data-toggle="dropdown" href="#">
            {translate('layout.settings')}&nbsp;
            <i className="icon-dropdown"/>
          </a>
          <ul className="dropdown-menu">
            {this.renderSettingsLink()}
            {this.renderProfilesLink()}
            {this.renderQualityGatesLink()}
            {this.renderCustomMeasuresLink()}
            {this.renderActionPlansLink()}
            {this.renderLinksLink()}
            {this.renderPermissionsLink()}
            {this.renderHistoryLink()}
            {this.renderBackgroundTasksLink()}
            {this.renderUpdateKeyLink()}
            {this.renderDeletionLink()}
            {this.renderExtensions()}
          </ul>
        </li>
    );
  },

  renderSettingsLink() {
    if (!this.props.conf.showSettings) {
      return null;
    }
    const url = `/project/settings?id=${encodeURIComponent(this.props.component.key)}`;
    return this.renderLink(url, translate('project_settings.page'), '/project/settings');
  },

  renderProfilesLink() {
    if (!this.props.conf.showQualityProfiles) {
      return null;
    }
    const url = `/project/profile?id=${encodeURIComponent(this.props.component.key)}`;
    return this.renderLink(url, translate('project_quality_profiles.page'), '/project/profile');
  },

  renderQualityGatesLink() {
    if (!this.props.conf.showQualityGates) {
      return null;
    }
    const url = `/project/qualitygate?id=${encodeURIComponent(this.props.component.key)}`;
    return this.renderLink(url, translate('project_quality_gate.page'), '/project/qualitygate');
  },

  renderCustomMeasuresLink() {
    if (!this.props.conf.showManualMeasures) {
      return null;
    }
    const url = `/custom_measures?id=${encodeURIComponent(this.props.component.key)}`;
    return this.renderLink(url, translate('custom_measures.page'), '/custom_measures');
  },

  renderActionPlansLink() {
    if (!this.props.conf.showActionPlans) {
      return null;
    }
    const url = `/action_plans?id=${encodeURIComponent(this.props.component.key)}`;
    return this.renderLink(url, translate('action_plans.page'), '/action_plans');
  },

  renderLinksLink() {
    if (!this.props.conf.showLinks) {
      return null;
    }
    const url = `/project/links?id=${encodeURIComponent(this.props.component.key)}`;
    return this.renderLink(url, translate('project_links.page'), '/project/links');
  },

  renderPermissionsLink() {
    if (!this.props.conf.showPermissions) {
      return null;
    }
    const url = `/project_roles?id=${encodeURIComponent(this.props.component.key)}`;
    return this.renderLink(url, translate('permissions.page'), '/project_roles');
  },

  renderHistoryLink() {
    if (!this.props.conf.showHistory) {
      return null;
    }
    const url = `/project/history?id=${encodeURIComponent(this.props.component.key)}`;
    return this.renderLink(url, translate('project_history.page'), '/project/history');
  },

  renderBackgroundTasksLink() {
    if (!this.props.conf.showBackgroundTasks) {
      return null;
    }
    const url = `/project/background_tasks?id=${encodeURIComponent(this.props.component.key)}`;
    return this.renderLink(url, translate('background_tasks.page'), '/project/background_tasks');
  },

  renderUpdateKeyLink() {
    if (!this.props.conf.showUpdateKey) {
      return null;
    }
    const url = `/project/key?id=${encodeURIComponent(this.props.component.key)}`;
    return this.renderLink(url, translate('update_key.page'), '/project/key');
  },

  renderDeletionLink() {
    if (!this.props.conf.showDeletion) {
      return null;
    }
    const url = `/project/deletion?id=${encodeURIComponent(this.props.component.key)}`;
    return this.renderLink(url, translate('deletion.page'), '/project/deletion');
  },

  renderExtensions() {
    const extensions = this.props.conf.extensions || [];
    return extensions.map(e => {
      return this.renderLink(e.url, e.name, e.url);
    });
  },

  renderTools() {
    const component = this.props.component;
    if (!component.isComparable && !_.size(component.extensions)) {
      return null;
    }
    const tools = [];
    (component.extensions || []).forEach(e => {
      tools.push(this.renderLink(e.url, e.name));
    });
    return tools;
  },

  render() {
    if (this.isDeveloper()) {
      return (
          <ul className="nav navbar-nav nav-tabs">
            {this.renderCustomDashboards()}
            {this.renderComponentIssuesLink()}
            {this.renderComponentMeasuresLink()}
            {this.renderCodeLink()}
            {this.renderProjectsLink()}
            {this.renderTools()}
            {this.renderAdministration()}
          </ul>
      );
    } else {
      return (
          <ul className="nav navbar-nav nav-tabs">
            {this.renderOverviewLink()}
            {this.renderComponentIssuesLink()}
            {this.renderComponentMeasuresLink()}
            {this.renderCodeLink()}
            {this.renderProjectsLink()}
            {this.renderCustomDashboards()}
            {this.renderTools()}
            {this.renderAdministration()}
          </ul>
      );
    }
  }
});
