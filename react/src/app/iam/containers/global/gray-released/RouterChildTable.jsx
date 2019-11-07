import React, { useContext } from 'react';
import { Breadcrumb, Content, Header } from '@choerodon/boot';
import { Button, Table, DataSet } from 'choerodon-ui/pro';
import RouterChildTableDataSet from './Store/routerChildTableDataSet';
import Store from './Store';
import './RouterChildTable.less';

const cssPrefix = 'c7n-manager-grayRelease-router';
const { Column } = Table;

const RouterTable = ({ record }) => {
  const { sourceTypeMap } = useContext(Store);
  const childDataSet = new DataSet(RouterChildTableDataSet(record.toData().hostDTOS || []));

  const SourceTypeRenderer = ({ value }) => sourceTypeMap[value];
  return (
    <Table className={`${cssPrefix}-childTable`} dataSet={childDataSet} queryBar="none">
      <Column name="metadataHostName" width={200} />
      <Column name="ipAddr" />
      <Column name="port" />
      <Column name="appName" />
      <Column name="sourceType" renderer={SourceTypeRenderer} />
    </Table>
  );
};

export default RouterTable;
