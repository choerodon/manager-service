import React, { createContext } from 'react';
import { observable } from 'mobx';

const Store = createContext();

export default Store;

export const StoreProvider = (props) => {
  const dsStore = [];

  const { children } = props;
  const value = {
    ...props,
    dsStore: observable(props.dsStore || dsStore),
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
};
