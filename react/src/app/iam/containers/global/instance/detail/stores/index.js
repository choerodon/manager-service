import React, { createContext, useMemo } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';

const Store = createContext();

export default Store;

export const StoreProvider = injectIntl(inject('AppState')(
  (props) => {
    const { intl, children } = props;
    const intlPrefix = 'global.baseTable.list';
    const value = {
      ...props,
      intlPrefix,
      intl,
    };
    debugger;
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
