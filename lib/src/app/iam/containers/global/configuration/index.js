'use strict';

Object.defineProperty(exports, "__esModule", {
  value: true
});

var _react = require('react');

var _react2 = _interopRequireDefault(_react);

var _reactRouterDom = require('react-router-dom');

var _boot = require('@choerodon/boot');

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

var index = (0, _boot.asyncRouter)(function () {
  return import('./Configuration');
}, function () {
  return import('../../../stores/global/configuration');
});
var create = (0, _boot.asyncRouter)(function () {
  return import('./ConfigurationCreate');
});
var edit = (0, _boot.asyncRouter)(function () {
  return import('./ConfigurationEdit');
});

var Index = function Index(_ref) {
  var match = _ref.match;
  return _react2['default'].createElement(
    _reactRouterDom.Switch,
    null,
    _react2['default'].createElement(_reactRouterDom.Route, { exact: true, path: match.url, component: index }),
    _react2['default'].createElement(_reactRouterDom.Route, { path: match.url + '/create', component: create }),
    _react2['default'].createElement(_reactRouterDom.Route, { path: match.url + '/edit/:name/:id', component: edit }),
    _react2['default'].createElement(_reactRouterDom.Route, { path: '*', component: _boot.nomatch })
  );
};

exports['default'] = Index;