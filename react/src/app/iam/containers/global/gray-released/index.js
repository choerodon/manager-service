import React from 'react';
import { PageWrap, PageTab } from '@choerodon/boot';
import { StoreProvider } from './Store/index';
import HostTable from './HostTable';
import RouterTable from './RouterTable';
import './index.less';

const cssPrefix = 'c7n-manager-grayReleased';

export default (props) => (
  <StoreProvider {...props}>
    <PageWrap noHeader={[]} cache className={cssPrefix}>
      <PageTab title="主机" tabKey="1" component={HostTable} alwaysShow />
      <PageTab title="路由" tabKey="2" component={RouterTable} alwaysShow />
    </PageWrap>
  </StoreProvider>
);
