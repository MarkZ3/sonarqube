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

import { Domain,
         DomainHeader,
         DomainPanel,
         DomainNutshell,
         DomainLeak,
         MeasuresList,
         Measure,
         DomainMixin } from './components';
import { DrilldownLink } from '../../../components/shared/drilldown-link';
import { TooltipsMixin } from '../../../components/mixins/tooltips-mixin';
import { DonutChart } from '../../../components/charts/donut-chart';
import { getMetricName } from '../helpers/metrics';
import { formatMeasure, formatMeasureVariation } from '../../../helpers/measures';
import { translate } from '../../../helpers/l10n';


export const GeneralDuplications = React.createClass({
  propTypes: {
    leakPeriodLabel: React.PropTypes.string,
    leakPeriodDate: React.PropTypes.object
  },

  mixins: [TooltipsMixin, DomainMixin],

  renderLeak () {
    if (!this.hasLeakPeriod()) {
      return null;
    }
    const measure = this.props.leak['duplicated_lines_density'];
    const formatted = measure != null ? formatMeasureVariation(measure, 'PERCENT') : '—';
    return <DomainLeak>
      <MeasuresList>
        <Measure label={getMetricName('duplications')}>
          {formatted}
        </Measure>
      </MeasuresList>
      {this.renderTimeline('after')}
    </DomainLeak>;
  },

  renderDuplicatedBlocks () {
    if (this.props.measures['duplicated_blocks'] == null) {
      return null;
    }
    return <Measure label={getMetricName('duplicated_blocks')}>
      <DrilldownLink component={this.props.component.key} metric="duplicated_blocks">
        {formatMeasure(this.props.measures['duplicated_blocks'], 'SHORT_INT')}
      </DrilldownLink>
    </Measure>;
  },

  render () {
    const donutData = [
      { value: this.props.measures['duplicated_lines_density'], fill: '#f3ca8e' },
      { value: Math.max(0, 20 - this.props.measures['duplicated_lines_density']), fill: '#e6e6e6' }
    ];

    return <Domain>
      <DomainHeader component={this.props.component}
                    title={translate('overview.domain.duplications')}/>

      <DomainPanel>
        <DomainNutshell>
          <MeasuresList>

            <Measure composite={true}>
              <div className="display-inline-block text-middle big-spacer-right">
                <DonutChart width="40"
                            height="40"
                            thickness="4"
                            data={donutData}/>
              </div>
              <div className="display-inline-block text-middle">
                <div className="overview-domain-measure-value">
                  <DrilldownLink component={this.props.component.key} metric="duplicated_lines_density">
                    {formatMeasure(this.props.measures['duplicated_lines_density'], 'PERCENT')}
                  </DrilldownLink>
                </div>
                <div className="overview-domain-measure-label">{getMetricName('duplications')}</div>
              </div>
            </Measure>

            {this.renderDuplicatedBlocks()}
          </MeasuresList>
          {this.renderTimeline('before')}
        </DomainNutshell>
        {this.renderLeak()}
      </DomainPanel>
    </Domain>;
  }
});
