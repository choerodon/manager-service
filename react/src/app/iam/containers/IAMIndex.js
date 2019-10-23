import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { inject } from 'mobx-react';
import { asyncLocaleProvider, asyncRouter, nomatch } from '@choerodon/boot';

const siteStatistics = asyncRouter(() => import('./global/site-statistics'));
const microService = asyncRouter(() => import('./global/microservice'));
const instance = asyncRouter(() => import('./global/instance'));
const configuration = asyncRouter(() => import('./global/configuration'));
const route = asyncRouter(() => import('./global/route'));
const apiTest = asyncRouter(() => import('./global/api-test'));
const apiStatistics = asyncRouter(() => import('./global/api-overview'));
const apiOverview = asyncRouter(() => import('./global/api-overview'));

@inject('AppState')
class IAMIndex extends React.Component {
  render() {
    const { match, AppState } = this.props;
    const langauge = AppState.currentLanguage;
    const IntlProviderAsync = asyncLocaleProvider(langauge, () => import(`../locale/${langauge}`));
    return (
      <IntlProviderAsync>
        <Switch>
          <Route path={`${match.url}/site-statistics`} component={siteStatistics} />
          <Route path={`${match.url}/microservice`} component={microService} />
          <Route path={`${match.url}/instance`} component={instance} />
          <Route path={`${match.url}/configuration`} component={configuration} />
          <Route path={`${match.url}/route`} component={route} />
          <Route path={`${match.url}/api-test`} component={apiTest} />
          <Route path={`${match.url}/api-statistics`} component={apiStatistics} />
          <Route path={`${match.url}/api-overview`} component={apiOverview} />
          <Route path="*" component={nomatch} />
        </Switch>
      </IntlProviderAsync>
    );
  }
}

export default IAMIndex;
