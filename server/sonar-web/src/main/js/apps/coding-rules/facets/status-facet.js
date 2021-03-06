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
import _ from 'underscore';
import BaseFacet from './base-facet';
import { translate } from '../../../helpers/l10n';

export default BaseFacet.extend({
  statuses: ['READY', 'DEPRECATED', 'BETA'],

  getValues () {
    const values = this.model.getValues();
    const x = values.map(function (value) {
      return _.extend(value, { label: translate('rules.status', value.val.toLowerCase()) });
    });
    return x;
  },

  sortValues (values) {
    const order = this.statuses;
    return _.sortBy(values, function (v) {
      return order.indexOf(v.val);
    });
  },

  serializeData () {
    return _.extend(BaseFacet.prototype.serializeData.apply(this, arguments), {
      values: this.sortValues(this.getValues())
    });
  }
});
