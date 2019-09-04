import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/master';

const index = asyncRouter(() => (import('./APITest')), () => import('../../../stores/global/api-test'));
const detail = asyncRouter(() => import('./APIDetail'));
const overview = asyncRouter(() => import('../api-overview'));

const Index = ({ match }) => (
  <Switch>
    <Route exact path={match.url} component={index} />
    <Route path={`${match.url}/overview`} component={overview} />
    <Route path={`${match.url}/detail/:controller/:service/:operationId/:version`} component={detail} />
    <Route path={'*'} component={nomatch} />
  </Switch>
);

export default Index;
