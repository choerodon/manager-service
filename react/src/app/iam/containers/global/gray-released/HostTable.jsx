import React, { useContext } from 'react';
import { Content, Header, Breadcrumb, Action, TabPage } from '@choerodon/boot';
import { Table, Button, Modal, Pagination } from 'choerodon-ui/pro';
import Store from './Store';
import { CreateHost } from './SideBar';

const ModalKey = Modal.key();
const { Column } = Table;

const HostTable = () => {
  const { hostTableDataSet, sourceTypeMap } = useContext(Store);

  const createHost = () => {
    const currentRecord = hostTableDataSet.create({});
    Modal.open({
      title: '创建主机',
      key: ModalKey,
      drawer: true,
      style: {
        width: '51.39%',
      },
      children: (
        <CreateHost record={currentRecord} />
      ),
      onOk: () => hostTableDataSet.submit(),
      onCancel: () => hostTableDataSet.splice(currentRecord.index, 1),
    });
  };

  const deleteRecord = (record) => hostTableDataSet.delete(record).then((res) => hostTableDataSet.query());

  const ActionRenderer = ({ record }) => record.get('parentServiceName') && record.get('sourceType') !== 'pod' && (
    <Action
      className="action-icon"
      data={[{
        service: [],
        text: '删除',
        action: () => deleteRecord(record),
      }]}
    />
  );

  const SourceTypeRenderer = ({ value }) => sourceTypeMap[value];

  return (
    <TabPage>
      <Header>
        <Button
          icon="playlist_add"
          onClick={createHost}
        >
          创建主机
        </Button>
      </Header>
      <Breadcrumb />
      <Content>
        <Table dataSet={hostTableDataSet} mode="tree" filter={(item) => item.status !== 'add'}>
          <Column name="serviceHostName" width={150} />
          <Column renderer={ActionRenderer} width={48} />
          <Column name="ipAddr" width={150} />
          <Column name="port" width={150} />
          <Column name="sourceType" renderer={SourceTypeRenderer} width={150} />
          <Column name="createDate" width={150} />
        </Table>
      </Content>
    </TabPage>
  );
};

export default HostTable;
