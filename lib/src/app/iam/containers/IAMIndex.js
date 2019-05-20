'use strict';

Object.defineProperty(exports, "__esModule", {
  value: true
});

var _classCallCheck2 = require('babel-runtime/helpers/classCallCheck');

var _classCallCheck3 = _interopRequireDefault(_classCallCheck2);

var _createClass2 = require('babel-runtime/helpers/createClass');

var _createClass3 = _interopRequireDefault(_createClass2);

var _possibleConstructorReturn2 = require('babel-runtime/helpers/possibleConstructorReturn');

var _possibleConstructorReturn3 = _interopRequireDefault(_possibleConstructorReturn2);

var _inherits2 = require('babel-runtime/helpers/inherits');

var _inherits3 = _interopRequireDefault(_inherits2);

var _dec, _class;

var _react = require('react');

var _react2 = _interopRequireDefault(_react);

var _reactRouterDom = require('react-router-dom');

var _mobxReact = require('mobx-react');

var _boot = require('@choerodon/boot');

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

var siteStatistics = (0, _boot.asyncRouter)(function () {
  return import('./global/site-statistics');
});
var microService = (0, _boot.asyncRouter)(function () {
  return import('./global/microservice');
});
var instance = (0, _boot.asyncRouter)(function () {
  return import('./global/instance');
});
var configuration = (0, _boot.asyncRouter)(function () {
  return import('./global/configuration');
});
var route = (0, _boot.asyncRouter)(function () {
  return import('./global/route');
});
var apiTest = (0, _boot.asyncRouter)(function () {
  return import('./global/api-test');
});
var apiOverview = (0, _boot.asyncRouter)(function () {
  return import('./global/api-overview');
});

var IAMIndex = (_dec = (0, _mobxReact.inject)('AppState'), _dec(_class = function (_React$Component) {
  (0, _inherits3['default'])(IAMIndex, _React$Component);

  function IAMIndex() {
    (0, _classCallCheck3['default'])(this, IAMIndex);
    return (0, _possibleConstructorReturn3['default'])(this, (IAMIndex.__proto__ || Object.getPrototypeOf(IAMIndex)).apply(this, arguments));
  }

  (0, _createClass3['default'])(IAMIndex, [{
    key: 'render',
    value: function render() {
      var _props = this.props,
          match = _props.match,
          AppState = _props.AppState;

      var langauge = AppState.currentLanguage;
      var IntlProviderAsync = (0, _boot.asyncLocaleProvider)(langauge, function () {
        return import('../locale/' + langauge);
      });
      return _react2['default'].createElement(
        IntlProviderAsync,
        null,
        _react2['default'].createElement(
          _reactRouterDom.Switch,
          null,
          _react2['default'].createElement(_reactRouterDom.Route, { path: match.url + '/site-statistics', component: siteStatistics }),
          _react2['default'].createElement(_reactRouterDom.Route, { path: match.url + '/microservice', component: microService }),
          _react2['default'].createElement(_reactRouterDom.Route, { path: match.url + '/instance', component: instance }),
          _react2['default'].createElement(_reactRouterDom.Route, { path: match.url + '/configuration', component: configuration }),
          _react2['default'].createElement(_reactRouterDom.Route, { path: match.url + '/route', component: route }),
          _react2['default'].createElement(_reactRouterDom.Route, { path: match.url + '/api-test', component: apiTest }),
          _react2['default'].createElement(_reactRouterDom.Route, { path: match.url + '/api-overview', component: apiOverview }),
          _react2['default'].createElement(_reactRouterDom.Route, { path: '*', component: _boot.nomatch })
        )
      );
    }
  }]);
  return IAMIndex;
}(_react2['default'].Component)) || _class);
exports['default'] = IAMIndex;