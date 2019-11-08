import React from 'react';
import { Form, TextField, Select, Tooltip, Icon, TextArea } from 'choerodon-ui/pro';
import { Collapse } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import _ from 'lodash';
import AllUserDataSet from '../Store/AllUserDataSet';
import AllHostDataSet from '../Store/AllHostDataSet';
import FormSelectEditor from '../../../../components/formSelectEditor';
import './CreateRouter.less';

const prefixCls = 'c7n-manager-router-createRouter';
const { Panel } = Collapse;

const CreateRouter = observer(({ currentRecord }) => {
  const getUserOption = ({ record, text, value }) => (
    <Tooltip placement="left" title={`${record.get('email')}`}>
      <div className={`${prefixCls}-option`}>
        <div className={`${prefixCls}-option-avatar`}>
          {record.get('imageUrl') ? <img src={record.get('imageUrl')} alt="userAvatar" style={{ width: '100%' }} />
            : <span className={`${prefixCls}-option-avatar-noavatar`}>{record.get('realName') && record.get('realName').split('')[0]}</span>}
        </div>
        <span>{record.get('realName')}</span>
      </div>
    </Tooltip>
  );

  const getHostOption = ({ record, text, value }) => (
    <span>{text}</span>
  );

  const queryFunc = _.debounce((str = '', optionDataSet, queryField) => {
    optionDataSet.setQueryParameter(queryField, str);
    if (str !== '') { optionDataSet.query(); }
  }, 500);
  function handleFilterChange(e, optionDataSet, queryField) {
    e.persist();
    queryFunc(e.target.value, optionDataSet, queryField);
  }

  return (
    <div className={prefixCls}>
      <Form record={currentRecord} style={{ width: '5.12rem' }}>
        <TextField name="code" />
        <TextArea name="description" />
      </Form>
      <hr className={`${prefixCls}-hr`} />
      <Collapse bordered={false} defaultActiveKey={['1', '2']} className={`${prefixCls}-collapse`}>
        <Panel
          header={(
            <div className={`${prefixCls}-help`}>添加主机
              <Tooltip title="您可通过输入服务名称或主机名称来选择所需要添加的主机">
                <Icon type="help" />
              </Tooltip>
            </div>
          )}
          key="1"
        >
          <FormSelectEditor
            record={currentRecord}
            optionDataSetConfig={AllHostDataSet}
            name="instanceIds"
            addButton="添加主机"
            maxDisable={false}
          >
            {((itemProps) => (
              <Select
                {...itemProps}
                labelLayout="float"
                searchable
                searchMatcher={() => true}
                onInput={(e) => handleFilterChange(e, itemProps.options, 'param')}
                optionRenderer={getHostOption}
              />
            ))}
          </FormSelectEditor>
        </Panel>
      </Collapse>
      <hr className={`${prefixCls}-hr`} />
      <Collapse bordered={false} defaultActiveKey={['1', '2']} className={`${prefixCls}-collapse`}>
        <Panel header="添加用户" key="1">
          <FormSelectEditor
            record={currentRecord}
            optionDataSetConfig={AllUserDataSet}
            name="userIds"
            filterObject
            addButton="添加用户"
            maxDisable={false}
          >
            {((itemProps) => (
              <Select
                {...itemProps}
                labelLayout="float"
                searchable
                searchMatcher={() => true}
                onInput={(e) => handleFilterChange(e, itemProps.options, 'user_name')}
                optionRenderer={getUserOption}
              />
            ))}
          </FormSelectEditor>
        </Panel>
      </Collapse>
      <hr className={`${prefixCls}-hr`} />
    </div>
  );
});

export default CreateRouter;
