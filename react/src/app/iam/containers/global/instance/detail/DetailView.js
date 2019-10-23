import React, { Component, useState, useEffect, useContext } from 'react';
import { Content, Header, Page, axios } from '@choerodon/boot';
import { Col, Row, Tabs, Spin } from 'choerodon-ui';
import { Icon, Table, Form, Output } from 'choerodon-ui/pro';
import AceEditor from '../../../../components/yamlAce';
import emptyApi from '../list/img/noright.svg';
import Store from './stores';
import './index.less';

const { TabPane } = Tabs;
const { Column } = Table;

export default function InstanceDetail() {
  const { id: instanceId, intl, metadataDataSet, instanceDataSet, intlPrefix } = useContext(Store);
  const configInfo = (instanceDataSet.current && instanceDataSet.current.get('configInfoYml')) || {};
  const envInfo = (instanceDataSet.current && instanceDataSet.current.get('envInfoYml')) || {};
  const [loading, setLoading] = useState(true);
  async function loadData() {
    setLoading(true);
    instanceDataSet.setQueryParameter('instanceId', instanceId);
    await instanceDataSet.query();
    setLoading(false);
  }
  useEffect(() => {
    if (instanceId) {
      loadData();
    }
  }, [instanceId]);

  function getInstanceInfo() {
    return (
      <div className="instanceInfoContainer">
        <div className="instanceInfo">
          <Form labelLayout="horizontal" labelAlign="left" dataSet={instanceDataSet}>
            <Output name="instanceId" />
            <Output name="hostName" />
            <Output name="ipAddr" />
            <Output name="app" />
            <Output name="port" />
            <Output name="version" />
            <Output name="registrationTime" />
            <Output name="metadata" renderer={() => ''} />
          </Form>
        </div>
        <Table
          dataSet={metadataDataSet}
          queryBar="none"
        >
          <Column name="key" />
          <Column name="value" />
        </Table>
      </div>
    );
  }

  function getConfigInfo() {
    return (
      <div className="configContainer">
        <div>
          <p>{intl.formatMessage({ id: `${intlPrefix}.configinfo` })}</p>
          <AceEditor
            readOnly="nocursor"
            value={(configInfo.yaml)}
          />
        </div>
        <div>
          <p>{intl.formatMessage({ id: `${intlPrefix}.envinfo` })}</p>
          <AceEditor
            readOnly="nocursor"
            value={(envInfo.yaml)}
          />
        </div>
      </div>
    );
  }

  function getEmpty() {
    const rightContent = (
      <div style={{
        display: 'flex',
        alignItems: 'center',
        height: 250,
        margin: '88px auto',
        padding: '50px 75px',
        width: '7rem', 
      }}
      >
        <img src={emptyApi} alt="" />
        <div style={{ marginLeft: 40 }}>
          <div style={{ fontSize: '14px', color: 'rgba(0,0,0,0.65)' }}>{intl.formatMessage({ id: `${intlPrefix}.empty.find.not` })}</div>
          <div style={{ fontSize: '20px', marginTop: 10 }}>{intl.formatMessage({ id: `${intlPrefix}.empty.try.choose` })}</div>
        </div>
      </div>
    );
    return rightContent;
  }

  function getTitle() {
    return <span><Icon type="instance_outline" />{instanceId}</span>;
  }

  if (instanceId === null) {
    return getEmpty();
  } else {
    return loading ? (<Spin size="large" style={{ paddingTop: 242, margin: '0 auto', width: '100%' }} />)
      : (
        <Content
          title={getTitle()}
        >
          <Tabs>
            <TabPane
              tab={intl.formatMessage({ id: `${intlPrefix}.instanceinfo` })}
              key="instanceinfo"
            >{getInstanceInfo()}
            </TabPane>
            <TabPane
              tab={intl.formatMessage({ id: `${intlPrefix}.configenvInfo` })}
              key="configenvInfo"
            >{getConfigInfo()}
            </TabPane>
          </Tabs>
        </Content>
      );
  }
}
