import React, { useContext } from 'react';
import { Breadcrumb, Content, Header, Action } from '@choerodon/boot';
import { message } from 'choerodon-ui';
import { Button, Table, Modal } from 'choerodon-ui/pro';
import Store from './Store';
import RouterChildTable from './RouterChildTable';
import { CreateRouter } from './SideBar';
import './RouterTable.less';

const cssPrefix = 'c7n-manager-grayRelease-router';
const { Column } = Table;
const ModalKey = Modal.key();

const RouterTable = () => {
  const { routerTableDataSet } = useContext(Store);

  const createRouter = () => {
    const currentRecord = routerTableDataSet.create({});
    Modal.open({
      title: '创建路由',
      key: ModalKey,
      drawer: true,
      style: {
        width: '51.39%',
      },
      okText: '创建',
      children: (
        <CreateRouter currentRecord={currentRecord} />
      ),
      onOk: () => routerTableDataSet.submit(),
      onCancel: () => routerTableDataSet.splice(currentRecord.index, 1),
    });
  };

  const editRouter = (record) => {
    record.status = 'update';
    Modal.open({
      title: '编辑路由',
      key: ModalKey,
      drawer: true,
      style: {
        width: '51.39%',
      },
      okText: '保存',
      children: (
        <CreateRouter currentRecord={record} />
      ),
      onOk: () => routerTableDataSet.submit().then((res) => routerTableDataSet.query()),
      onCancel: () => record.reset(),
    });
  };

  const deleteRecord = (record) => routerTableDataSet.delete(record);

  const ActionRenderer = ({ record }) => (
    <Action
      className="action-icon"
      data={[{
        service: [],
        text: '删除',
        action: () => deleteRecord(record),
      }]}
    />
  );

  return (
    <React.Fragment>
      <Header>
        <Button icon="playlist_add" onClick={createRouter}>创建路由</Button>
      </Header>
      <Breadcrumb />
      <Content className={cssPrefix}>
        <Table
          dataSet={routerTableDataSet}
          className={`${cssPrefix}-table`}
          expandedRowRenderer={
            ({ dataSet, record }) => (
              <div style={{ marginLeft: '-0.12rem' }}><RouterChildTable record={record} /></div>
            )
          }
          filter={(item) => item.status !== 'add'}
        >
          <Column
            name="code"
            width={150}
            onCell={({ record }) => ({
              onClick: () => editRouter(record),
            })}
          />
          <Column renderer={ActionRenderer} width={48} />
          <Column name="description" />
          <Column name="hostNumber" />
          <Column name="userNumber" />
          <Column name="creationDate" />
        </Table>
      </Content>
    </React.Fragment>
  );
};

export default RouterTable;
