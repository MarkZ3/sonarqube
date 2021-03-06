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
import React, { Component } from 'react';
import { connect } from 'react-redux';
import classNames from 'classnames';

import { search, selectCurrent, selectNext, selectPrev } from '../actions';
import { translateWithParameters } from '../../../helpers/l10n';


class Search extends Component {
  componentDidMount () {
    this.refs.input.focus();
  }

  handleKeyDown (e) {
    const { dispatch } = this.props;
    switch (e.keyCode) {
      case 13:
        e.preventDefault();
        dispatch(selectCurrent());
        break;
      case 38:
        e.preventDefault();
        dispatch(selectPrev());
        break;
      case 40:
        e.preventDefault();
        dispatch(selectNext());
        break;
      default:
        // do nothing
    }
  }

  handleSearch (e) {
    e.preventDefault();
    const { dispatch, component } = this.props;
    const query = this.refs.input.value;
    dispatch(search(query, component));
  }

  render () {
    const { query } = this.props;
    const inputClassName = classNames('search-box-input', {
      'touched': query.length > 0 && query.length < 3
    });

    return (
        <form
            onSubmit={this.handleSearch.bind(this)}
            className="search-box code-search-box">
          <button className="search-box-submit button-clean">
            <i className="icon-search"></i>
          </button>
          <input
              ref="input"
              onKeyDown={this.handleKeyDown.bind(this)}
              onChange={this.handleSearch.bind(this)}
              value={query}
              className={inputClassName}
              type="search"
              name="q"
              placeholder="Search"
              maxLength="100"
              autoComplete="off"/>
          <div className="note">
            {translateWithParameters('select2.tooShort', 3)}
          </div>
        </form>
    );
  }
}


export default connect(state => {
  return { query: state.current.searchQuery };
})(Search);
