import React, { Component, useState, useEffect, useContext } from 'react';
import { Content, Header, Page, axios } from '@choerodon/master';
import { Col, Row, Table, Tabs, Spin } from 'choerodon-ui';
import { Icon } from 'choerodon-ui/pro';
import AceEditor from '../../../../components/yamlAce';
import emptyApi from '../list/img/noright.svg';
import Store from './stores';
import './index.less';

const { TabPane } = Tabs;
const intlPrefix = 'global.instance';

export default function InstanceDetail() {
  const { id: instanceId, intl } = useContext(Store);
  const [info, setInfo] = useState(null);
  const [metadata, setMetadata] = useState(null);
  const [loading, setLoading] = useState(true);
  useEffect(() => {
    if (instanceId) {
      setLoading(true);
      axios.get(`manager/v1/instances/${instanceId}`).then((data) => {
        if (data.failed) {
          setLoading(false);
          Choerodon.prompt(data.message);
        } else {
          let newMetadata = { ...data.metadata };
          newMetadata = Object.entries(newMetadata).map((item) => ({
            name: item[0],
            value: item[1],
          }));
          setInfo(data);
          setMetadata(newMetadata);
          setLoading(false);
        }
      });
    }
  }, [instanceId]);

  function getInstanceInfo() {
    const { formatMessage } = intl;
    const columns = [{
      title: formatMessage({ id: `${intlPrefix}.name` }),
      dataIndex: 'name',
      key: 'name',
    }, {
      title: formatMessage({ id: `${intlPrefix}.value` }),
      dataIndex: 'value',
      key: 'value',
    }];
    const infoList = [{
      key: formatMessage({ id: `${intlPrefix}.instanceid` }),
      value: info.instanceId,
    }, {
      key: formatMessage({ id: `${intlPrefix}.hostname` }),
      value: info.hostName,
    }, {
      key: formatMessage({ id: `${intlPrefix}.ip` }),
      value: info.ipAddr,
    }, {
      key: formatMessage({ id: `${intlPrefix}.service` }),
      value: info.app,
    }, {
      key: formatMessage({ id: `${intlPrefix}.port` }),
      value: info.port,
    }, {
      key: formatMessage({ id: `${intlPrefix}.version` }),
      value: info.version,
    }, {
      key: formatMessage({ id: `${intlPrefix}.registertime` }),
      value: info.registrationTime,
    }, {
      key: formatMessage({ id: `${intlPrefix}.metadata` }),
      value: '',
    }];
    return (
      <div className="instanceInfoContainer">
        <div className="instanceInfo">
          {
            infoList.map(({ key, value }) => (
              <Row key={key}>
                <Col span={5}>{key}:</Col>
                <Col span={19}>{value}</Col>
              </Row>
            ))
          }
        </div>
        <Table
          columns={columns}
          dataSource={metadata}
          rowKey="name"
          pagination={false}
          filterBarPlaceholder={formatMessage({ id: 'filtertable' })}
        />
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
            value={info.configInfoYml.yaml || ''}
          />
        </div>
        <div>
          <p>{intl.formatMessage({ id: `${intlPrefix}.envinfo` })}</p>
          <AceEditor
            readOnly="nocursor"
            value={info.envInfoYml.yaml || ''}
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
