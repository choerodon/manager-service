import React, { createContext, useMemo } from 'react';
import { inject } from 'mobx-react';
import { DataSet } from 'choerodon-ui/pro';
import { injectIntl } from 'react-intl';
import InstanceDataSet from './InstanceDataSet';
import MetadataDataSet from './MetadataDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = injectIntl(inject('AppState')(
  (props) => {
    const { intl, children } = props;
    const intlPrefix = 'global.instance';
    const metadataDataSet = useMemo(() => new DataSet(MetadataDataSet()), []);
    const instanceDataSet = useMemo(() => new DataSet(InstanceDataSet({ intl, intlPrefix, metadataDataSet })), []);
    const value = {
      ...props,
      intlPrefix,
      intl,
      metadataDataSet,
      instanceDataSet,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
