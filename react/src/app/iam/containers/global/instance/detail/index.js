import React, { useCallback, useContext, useMemo, useState } from 'react';
import { observer } from 'mobx-react-lite';
import { StoreProvider } from './stores';
import DetailView from './DetailView';

export default observer((props) => (
  <StoreProvider {...props}>
    <DetailView />
  </StoreProvider>
));
