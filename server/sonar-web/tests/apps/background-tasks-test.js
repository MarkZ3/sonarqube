/* eslint no-unused-expressions: 0 */
import React from 'react';
import ReactDOM from 'react-dom';
import TestUtils from 'react-addons-test-utils';

import Header from '../../src/main/js/apps/background-tasks/components/Header';
import Stats from '../../src/main/js/apps/background-tasks/components/Stats';
import Search from '../../src/main/js/apps/background-tasks/components/Search';
import Tasks from '../../src/main/js/apps/background-tasks/components/Tasks';
import { STATUSES, CURRENTS, DEBOUNCE_DELAY } from '../../src/main/js/apps/background-tasks/constants';
import { formatDuration } from '../../src/main/js/apps/background-tasks/helpers';

let chai = require('chai');
let expect = chai.expect;
let sinon = require('sinon');
chai.use(require('sinon-chai'));

describe('Background Tasks', function () {
  describe('Constants', () => {
    it('should have STATUSES', () => {
      expect(STATUSES).to.be.a('object');
      expect(Object.keys(STATUSES).length).to.equal(7);
    });

    it('should have CURRENTS', () => {
      expect(CURRENTS).to.be.a('object');
      expect(Object.keys(CURRENTS).length).to.equal(2);
    });
  });

  describe('Search', () => {
    it('should render search form', () => {
      let component = TestUtils.renderIntoDocument(
          <Search
              types={[]}
              date={{}}/>
          ),
          searchBox = TestUtils.scryRenderedDOMComponentsWithClass(component, 'js-search');
      expect(searchBox).to.have.length(1);
    });

    it('should not render search form', () => {
      let component = TestUtils.renderIntoDocument(
          <Search
              component={{ id: 'ABCD' }}
              types={[]}
              date={{}}/>
          ),
          searchBox = TestUtils.scryRenderedDOMComponentsWithClass(component, 'js-search');
      expect(searchBox).to.be.empty;
    });

    it('should search', (done) => {
      let searchSpy = sinon.spy();
      let component = TestUtils.renderIntoDocument(
          <Search
              types={[]}
              date={{}}
              onSearch={searchSpy}/>);
      let searchInput = ReactDOM.findDOMNode(
          TestUtils.findRenderedDOMComponentWithClass(component, 'js-search'));
      searchInput.value = 'some search query';
      TestUtils.Simulate.change(searchInput);
      setTimeout(() => {
        expect(searchSpy).to.have.been.calledWith('some search query');
        done();
      }, DEBOUNCE_DELAY);
    });

    it('should reload', () => {
      let reloadSpy = sinon.spy();
      let component = TestUtils.renderIntoDocument(
          <Search
              types={[]}
              date={{}}
              onRefresh={reloadSpy}/>
      );
      let reloadButton = component.refs.reloadButton;
      expect(reloadSpy).to.not.have.been.called;
      TestUtils.Simulate.click(reloadButton);
      expect(reloadSpy).to.have.been.called;
    });
  });

  describe('Stats', () => {
    describe('Pending', () => {
      it('should show zero pending', () => {
        let result = TestUtils.renderIntoDocument(<Stats pendingCount="0"/>),
            pendingCounter = result.refs.pendingCount;
        expect(pendingCounter.textContent).to.contain('0');
      });

      it('should show 5 pending', () => {
        let result = TestUtils.renderIntoDocument(<Stats pendingCount="5"/>),
            pendingCounter = result.refs.pendingCount;
        expect(pendingCounter.textContent).to.contain('5');
      });

      it('should not show cancel pending button', () => {
        let result = TestUtils.renderIntoDocument(<Stats pendingCount="0"/>),
            cancelPending = result.refs.cancelPending;
        expect(cancelPending).to.not.be.ok;
      });

      it('should show cancel pending button', () => {
        let result = TestUtils.renderIntoDocument(<Stats pendingCount="5"/>),
            cancelPending = result.refs.cancelPending;
        expect(cancelPending).to.be.ok;
      });

      it('should trigger cancelling pending', () => {
        let spy = sinon.spy();
        let result = TestUtils.renderIntoDocument(<Stats pendingCount="5" onCancelAllPending={spy}/>),
            cancelPending = result.refs.cancelPending;
        expect(spy).to.not.have.been.called;
        TestUtils.Simulate.click(cancelPending);
        expect(spy).to.have.been.called;
      });
    });

    describe('Failures', () => {
      it('should show zero failures', () => {
        let result = TestUtils.renderIntoDocument(<Stats failingCount="0"/>),
            failureCounter = result.refs.failureCount;
        expect(failureCounter.textContent).to.contain('0');
      });

      it('should show 5 failures', () => {
        let result = TestUtils.renderIntoDocument(<Stats failingCount="5"/>),
            failureCounter = result.refs.failureCount;
        expect(failureCounter.textContent).to.contain('5');
      });

      it('should not show link to failures', () => {
        let result = TestUtils.renderIntoDocument(<Stats failingCount="0"/>),
            failureCounter = result.refs.failureCount;
        expect(failureCounter.tagName.toLowerCase()).to.not.equal('a');
      });

      it('should show link to failures', () => {
        let result = TestUtils.renderIntoDocument(<Stats failingCount="5"/>),
            failureCounter = result.refs.failureCount;
        expect(failureCounter.tagName.toLowerCase()).to.equal('a');
      });

      it('should trigger filtering failures', () => {
        let spy = sinon.spy();
        let result = TestUtils.renderIntoDocument(<Stats failingCount="5" onShowFailing={spy}/>),
            failureCounter = result.refs.failureCount;
        expect(spy).to.not.have.been.called;
        TestUtils.Simulate.click(failureCounter);
        expect(spy).to.have.been.called;
      });
    });

    describe('In Progress Duration', () => {
      it('should show duration', () => {
        let result = TestUtils.renderIntoDocument(<Stats inProgressDuration="173"/>),
            inProgressDuration = result.refs.inProgressDuration;
        expect(inProgressDuration.textContent).to.include('173ms');
      });

      it('should format duration', () => {
        let result = TestUtils.renderIntoDocument(<Stats inProgressDuration="1073"/>),
            inProgressDuration = result.refs.inProgressDuration;
        expect(inProgressDuration.textContent).to.include('1s');
      });

      it('should not show duration', () => {
        let result = TestUtils.renderIntoDocument(<Stats/>),
            inProgressDuration = result.refs.inProgressDuration;
        expect(inProgressDuration).to.not.be.ok;
      });
    });
  });

  describe('Helpers', () => {
    describe('#formatDuration()', () => {
      it('should format 173ms', () => {
        expect(formatDuration(173)).to.equal('173ms');
      });

      it('should format 999ms', () => {
        expect(formatDuration(999)).to.equal('999ms');
      });

      it('should format 1s', () => {
        expect(formatDuration(1000)).to.equal('1s');
      });

      it('should format 1s', () => {
        expect(formatDuration(1001)).to.equal('1s');
      });

      it('should format 2s', () => {
        expect(formatDuration(1501)).to.equal('2s');
      });

      it('should format 59s', () => {
        expect(formatDuration(59000)).to.equal('59s');
      });

      it('should format 1min', () => {
        expect(formatDuration(60000)).to.equal('1min');
      });

      it('should format 1min', () => {
        expect(formatDuration(62757)).to.equal('1min');
      });

      it('should format 4min', () => {
        expect(formatDuration(224567)).to.equal('4min');
      });

      it('should format 80min', () => {
        expect(formatDuration(80 * 60 * 1000)).to.equal('80min');
      });
    });
  });
});
