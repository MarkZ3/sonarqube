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

import MeasureDetailsSeeAlso from './MeasureDetailsSeeAlso';
import LanguageDistribution from './LanguageDistribution';
import { ComplexityDistribution } from '../../overview/components/complexity-distribution';
import { formatLeak } from '../utils';
import { formatMeasure } from '../../../helpers/measures';
import { translateWithParameters } from '../../../helpers/l10n';
import { TooltipsContainer } from '../../../components/mixins/tooltips-mixin';

export default function MeasureDetailsHeader (
    {
        measure,
        metric,
        secondaryMeasure,
        leakPeriodLabel,
        metricSelectOpen,
        onMetricClick
    }
) {
  const leakPeriodTooltip = translateWithParameters('overview.leak_period_x', leakPeriodLabel);

  const displayLeak = measure.leak != null && metric.type !== 'RATING' && metric.type !== 'LEVEL';

  return (
      <header className="measure-details-header">
        <h2 className="measure-details-metric">
          <a
              href="#"
              onClick={e => {
                e.stopPropagation();
                e.preventDefault();
                onMetricClick();
              }}>
            {metric.name}
            &nbsp;
            <i className="icon-dropdown"/>
          </a>
        </h2>

        <TooltipsContainer options={{ html: false }}>
          <div className="measure-details-value">
            {measure.value != null && (
                <div className="measure-details-value-absolute">
                  {formatMeasure(measure.value, metric.type)}
                </div>
            )}

            {displayLeak && (
                <div
                    className="measure-details-value-leak"
                    title={leakPeriodTooltip}
                    data-toggle="tooltip">
                  {formatLeak(measure.leak, metric)}
                </div>
            )}

            {secondaryMeasure && secondaryMeasure.metric === 'ncloc_language_distribution' && (
                <div className="measure-details-secondary">
                  <LanguageDistribution distribution={secondaryMeasure.value}/>
                </div>
            )}

            {secondaryMeasure && secondaryMeasure.metric === 'function_complexity_distribution' && (
                <div className="measure-details-secondary">
                  <ComplexityDistribution distribution={secondaryMeasure.value} of="function"/>
                </div>
            )}

            {secondaryMeasure && secondaryMeasure.metric === 'file_complexity_distribution' && (
                <div className="measure-details-secondary">
                  <ComplexityDistribution distribution={secondaryMeasure.value} of="file"/>
                </div>
            )}
          </div>
        </TooltipsContainer>

        {metricSelectOpen && (
            <MeasureDetailsSeeAlso metric={metric} onClose={onMetricClick}/>
        )}
      </header>
  );
}
