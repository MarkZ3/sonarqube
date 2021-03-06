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
import moment from 'moment';
import React from 'react';

import { Timeline } from './timeline';
import { translateWithParameters } from '../../../helpers/l10n';


export const Domain = React.createClass({
  render () {
    return <div className="overview-card">{this.props.children}</div>;
  }
});


export const DomainTitle = React.createClass({
  render () {
    return <div className="overview-title">{this.props.children}</div>;
  }
});


export const DomainLeakTitle = React.createClass({
  renderInline (tooltip, fromNow) {
    return <span className="overview-domain-leak-title" title={tooltip} data-toggle="tooltip">
      <span>{translateWithParameters('overview.leak_period_x', this.props.label)}</span>
      <span className="note spacer-left">{translateWithParameters('overview.started_x', fromNow)}</span>
    </span>;
  },

  render() {
    if (!this.props.label || !this.props.date) {
      return null;
    }
    const momentDate = moment(this.props.date);
    const fromNow = momentDate.fromNow();
    const tooltip = 'Started on ' + momentDate.format('LL');
    if (this.props.inline) {
      return this.renderInline(tooltip, fromNow);
    }
    return <span className="overview-domain-leak-title" title={tooltip} data-toggle="tooltip">
      <span>{translateWithParameters('overview.leak_period_x', this.props.label)}</span>
      <br/>
      <span className="note">{translateWithParameters('overview.started_x', fromNow)}</span>
    </span>;
  }
});


export const DomainHeader = React.createClass({
  render () {
    return <div className="overview-card-header">
      <DomainTitle {...this.props}>{this.props.title}</DomainTitle>
    </div>;
  }
});


export const DomainPanel = React.createClass({
  propTypes: {
    domain: React.PropTypes.string
  },

  render () {
    return <div className="overview-domain-panel">
      {this.props.children}
    </div>;
  }
});


export const DomainNutshell = React.createClass({
  render () {
    return <div className="overview-domain-nutshell">{this.props.children}</div>;
  }
});

export const DomainLeak = React.createClass({
  render () {
    return <div className="overview-domain-leak">{this.props.children}</div>;
  }
});


export const MeasuresList = React.createClass({
  render () {
    return <div className="overview-domain-measures">{this.props.children}</div>;
  }
});


export const Measure = React.createClass({
  propTypes: {
    label: React.PropTypes.string,
    composite: React.PropTypes.bool
  },

  getDefaultProps() {
    return { composite: false };
  },

  renderValue () {
    if (this.props.composite) {
      return this.props.children;
    } else {
      return <div className="overview-domain-measure-value">
        {this.props.children}
      </div>;
    }
  },

  renderLabel() {
    return this.props.label ?
        <div className="overview-domain-measure-label">{this.props.label}</div> : null;
  },

  render () {
    return <div className="overview-domain-measure">
      {this.renderValue()}
      {this.renderLabel()}
    </div>;
  }
});


export const DomainMixin = {
  renderTimelineStartDate() {
    const momentDate = moment(this.props.historyStartDate);
    const fromNow = momentDate.fromNow();
    return (
        <span className="overview-domain-timeline-date">
        {translateWithParameters('overview.started_x', fromNow)}
      </span>
    );
  },

  renderTimeline(range, displayDate) {
    if (!this.props.history) {
      return null;
    }
    const props = { history: this.props.history };
    props[range] = this.props.leakPeriodDate;
    return <div className="overview-domain-timeline">
      <Timeline {...props}/>
      {displayDate ? this.renderTimelineStartDate(range) : null}
    </div>;
  },

  hasLeakPeriod () {
    return this.props.leakPeriodDate != null;
  }
};
