import React, { createContext, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import TreeDataSet from './TreeDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = injectIntl(inject('AppState')(
  (props) => {
    const { AppState: { currentMenuType: { type, id } }, intl, children } = props;
    const intlPrefix = 'global.baseTable.list';
    const dataSet = useMemo(() => new DataSet(TreeDataSet(intl, intlPrefix)));
    const value = {
      ...props,
      intlPrefix,
      dataSet,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
