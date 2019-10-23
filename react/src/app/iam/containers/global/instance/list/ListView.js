import React, { useEffect, useState, useContext } from 'react';
import { Content, Page, Header, Breadcrumb } from '@choerodon/boot';
import { Icon, Tree } from 'choerodon-ui/pro';
import { runInAction } from 'mobx';
import { observer } from 'mobx-react-lite';
import { Input } from 'choerodon-ui';
import DetailView from '../detail';
import Store from './stores';

import './index.less';

const intlPrefix = 'global.baseTable.list';

export default observer((props) => {
  const { dataSet, intl } = useContext(Store);
  const [code, setCode] = useState(null);
  const [inputValue, setInputValue] = useState('');
  const [isTree, setIsTree] = useState(true);

  function showDetail(record) {
    if (record.get('service')) {
      setCode(record.get('instanceId'));
    }
  }

  function selectFirst() {
    showDetail(dataSet.data[0]);
  }
  useEffect(() => {
    dataSet.addEventListener('load', selectFirst);
    return () => {
      dataSet.removeEventListener('load', selectFirst);
    };
  });

  
  function getTitle(record) {
    const name = record.get('instanceId').toLowerCase();
    const searchValue = inputValue.toLowerCase();
    const index = name.indexOf(searchValue);
    const beforeStr = name.substr(0, index).toLowerCase();
    const afterStr = name.substr(index + searchValue.length).toLowerCase();
    const title = index > -1 ? (
      <span className="tree-title" onClick={() => showDetail(record)}>
        {!record.get('service') && <Icon type={record.get('expand') ? 'folder_open2' : 'folder_open'} />}
        {record.get('service') && <Icon type="instance_outline" />}

        {beforeStr}
        <span style={{ color: '#f50' }}>{inputValue.toLowerCase()}</span>
        {afterStr}
      </span>
    ) : (
      <span className="tree-title" onClick={() => showDetail(record)}>
        {!record.get('service') && <Icon type={record.get('expand') ? 'folder_open2' : 'folder_open'} />}
        {record.get('service') && <Icon type="instance_outline" />}
        {name}
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
  function handleExpand(e) {
    runInAction(() => {
      dataSet.forEach((record) => {
        if (!record.get('service')) {
          if (record.get('instanceId').toLowerCase().includes(inputValue.toLowerCase())) {
            record.set('expand', true);
          } else {
            record.set('expand', false);
          }
        }
      });
    });
  }

  function getExpand() {
    return (
      <div className="c7n-instance-tree">
        <Input 
          className="c7n-instance-search"
          style={{ marginBottom: '.1rem', width: '1.9rem' }}
          prefix={<Icon type="search" style={{ color: 'black' }} />}
          placeholder="请输入搜索条件"
          onChange={handleSearch}
          value={inputValue}
          onPressEnter={handleExpand}
        />
        <div
          role="none"
          className="hidden-button"
          onClick={() => setIsTree(false)}
        >
          <Icon type="navigate_before" />
        </div>
        <Tree
          renderer={nodeRenderer}
          dataSet={dataSet}
        />
      </div>
    );
  }
  function getUnExpand() {
    return (
      <div className="c7n-iam-apitest-bar">
        <div
          role="none"
          className="c7n-iam-apitest-bar-button"
          onClick={() => setIsTree(true)}
        >
          <Icon type="navigate_next" />
        </div>
        <p
          role="none"
          onClick={() => setIsTree(true)}
        >
            实例
        </p>
      </div>
    );
  }
  
  return (
    <Page>
      <Breadcrumb />
      <Content className="c7n-instance">
        {isTree ? getExpand() : getUnExpand()}
        <div className="c7n-instance-content">
          <DetailView id={code} intl={intl} />
        </div>
      </Content>
    </Page>
  );
});
