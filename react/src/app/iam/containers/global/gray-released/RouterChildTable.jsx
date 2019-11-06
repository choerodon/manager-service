import React, { useContext } from 'react';
import { Breadcrumb, Content, Header } from '@choerodon/boot';
import { Button, Table } from 'choerodon-ui/pro';
import Store from './Store';
import './RouterChildTable.less';

const cssPrefix = 'c7n-manager-grayRelease-router';
const { Column } = Table;

const RouterTable = () => {
  const { routerChildTableDataSet, sourceTypeMap } = useContext(Store);
  const SourceTypeRenderer = ({ value }) => sourceTypeMap[value];
  return (
    <Table className={`${cssPrefix}-childTable`} dataSet={routerChildTableDataSet} queryBar="none">
      <Column name="hostName" width={200} />
      <Column name="ipAddr" width={150} />
      <Column name="port" width={150} />
      <Column name="appName" width={150} />
      <Column name="sourceType" width={150} renderer={SourceTypeRenderer} />
    </Table>
  );
};

export default RouterTable;
