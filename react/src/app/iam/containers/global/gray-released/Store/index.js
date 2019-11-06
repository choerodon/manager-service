import React, { createContext, useMemo } from 'react';
import { withRouter } from 'react-router-dom';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import HostTableDataSet from './hostTableDataSet';
import RouterTableDataSet from './routerTableDataSet';
import RouterChildTableDataSet from './routerChildTableDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = withRouter(injectIntl(inject('AppState')(
  (props) => {
    const { AppState: { currentMenuType: { type, id } }, intl, children } = props;
    const hostTableDataSet = useMemo(() => new DataSet(HostTableDataSet()), []);
    const routerChildTableDataSet = useMemo(() => new DataSet(RouterChildTableDataSet()), []);
    const routerTableDataSet = useMemo(() => new DataSet(RouterTableDataSet(routerChildTableDataSet)), []);
    const value = {
      hostTableDataSet,
      routerTableDataSet,
      routerChildTableDataSet,
      sourceTypeMap: {
        custom: '自定义',
        pod: '系统预置',
      },
      ...props,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
)));
