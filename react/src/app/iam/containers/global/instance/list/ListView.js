import React, { PureComponent, useState, useContext } from 'react';
import { FormattedMessage, injectIntl } from 'react-intl';
import { Content, Page, Header, Breadcrumb } from '@choerodon/master';
import { Icon, Tree } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { Input, Button } from 'choerodon-ui';
import DetailView from '../detail';
import Store from './stores';

import './index.less';

const intlPrefix = 'global.baseTable.list';

export default observer((props) => {
  const { dataSet, intl } = useContext(Store);
  const [code, setCode] = useState(null);
  const [inputValue, setInputValue] = useState('');

  function refresh() {
    setCode(null);
    dataSet.query();
  }
  
  function showDetail(record) {
    if (record.get('service')) {
      setCode(record.get('instanceId'));
    }
  }
  function getTitle(record) {
    const name = record.get('instanceId').toLowerCase();
    const searchValue = inputValue.toLowerCase();
    const index = name.indexOf(searchValue);
    const beforeStr = name.substr(0, index);
    const afterStr = name.substr(index + searchValue.length);
    const title = (
      <span className="tree-title" onClick={() => showDetail(record)}>
        {!record.get('service') && <Icon type={record.get('expand') ? 'folder_open2' : 'folder_open'} />}
        {record.get('service') && <Icon type="instance_outline" />}

        {beforeStr}
        <span style={{ color: '#f50' }}>{inputValue}</span>
        {afterStr}
      </span>
    );
    return title;
  }
  function nodeRenderer({ record }) {
    return getTitle(record);
  }
  function handleSearch(e) {
    setInputValue(e.target.value);
  }
  
  return (
    <Page>
      <Breadcrumb />
      <Content className="c7n-instance">
        <div className="c7n-instance-tree">
          <Input 
            className="c7n-instance-search"
            style={{ marginBottom: '.1rem' }}
            prefix={<Icon type="search" style={{ color: 'black' }} />}
            placeholder="请输入搜索条件"
            onChange={handleSearch}
            value={inputValue}
          />
          <Tree
            
            renderer={nodeRenderer}
            dataSet={dataSet}
          />
        </div>
        <div className="c7n-instance-content">
          <DetailView id={code} intl={intl} />
        </div>
      </Content>
    </Page>
  );
});
