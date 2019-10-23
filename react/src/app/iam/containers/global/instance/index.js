/**
 * Created by hulingfangzi on 2018/6/20.
 */
import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/boot';

const list = asyncRouter(() => import('./list'));

const Index = ({ match }) => (
  <Switch>
    <Route exact path={match.url} component={list} />
    <Route path="*" component={nomatch} />
  </Switch>
);

export default Index;
