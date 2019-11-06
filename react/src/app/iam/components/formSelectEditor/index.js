import React from 'react';
import FormSelectEditor from './FormSelectEditor';
import { StoreProvider } from './stores';

export default (props) => (
  <StoreProvider {...props}>
    <FormSelectEditor {...props} />
  </StoreProvider>
);
