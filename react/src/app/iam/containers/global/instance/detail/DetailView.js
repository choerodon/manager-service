/**
 * Created by hulingfangzi on 2018/6/26.
 */
import React, { Component } from 'react';
import { Content, Header, Page } from '@choerodon/master';
import { Col, Row, Table, Tabs, Spin } from 'choerodon-ui';
import AceEditor from '../../../../components/yamlAce';
import InstanceStore from '../../../../stores/global/instance';
import emptyApi from '../list/img/noright.svg';
import './index.less';

const { TabPane } = Tabs;
const intlPrefix = 'global.instance';


export default class InstanceDetail extends Component {
  state = this.getInitState();

  instanceId = null;

  getInitState() {
    return {
      info: null,
      metadata: null,
      loading: true,
    };
  }

  constructor(props) {
    super(props);
    this.instanceId = props.id;
  }

  componentWillReceiveProps(nextProps) {
    this.instanceId = nextProps.id;
    this.setState({
      loading: true,
    });
    if (this.instanceId) {
      InstanceStore.loadInstanceInfo(this.instanceId).then((data) => {
        if (data.failed) {
          this.setState({
            loading: false,
          });
          Choerodon.prompt(data.message);
        } else {
          let metadata = { ...data.metadata };
          metadata = Object.entries(metadata).map((item) => ({
            name: item[0],
            value: item[1],
          }));
          this.setState({
            info: data,
            metadata,
            loading: false,
          });
        }
      });
    }
  }

  componentDidMount() {
    this.setState({
      loading: true,
    });
    if (this.instanceId) {
      InstanceStore.loadInstanceInfo(this.instanceId).then((data) => {
        if (data.failed) {
          this.setState({
            loading: false,
          });
          Choerodon.prompt(data.message);
        } else {
          let metadata = { ...data.metadata };
          metadata = Object.entries(metadata).map((item) => ({
            name: item[0],
            value: item[1],
          }));
          this.setState({
            info: data,
            metadata,
            loading: false,
          });
        }
      });
    }
  }

  getInstanceInfo = () => {
    const { info, metadata } = this.state;
    const { intl: { formatMessage } } = this.props;
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
  };

  getConfigInfo = () => {
    const { info } = this.state;
    const { intl } = this.props;
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
  };

  render() {
    const { loading } = this.state;
    const { intl, id } = this.props;
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
    if (id === null) {
      return rightContent;
    }
    
        
    return loading ? (<Spin size="large" style={{ paddingTop: 242, margin: '0 auto', width: '100%' }} />)
      : (
        <Content
          code={`${intlPrefix}.detail`}
          values={{ name: this.instanceId }}
        >
          <Tabs>
            <TabPane
              tab={intl.formatMessage({ id: `${intlPrefix}.instanceinfo` })}
              key="instanceinfo"
            >{this.getInstanceInfo()}
            </TabPane>
            <TabPane
              tab={intl.formatMessage({ id: `${intlPrefix}.configenvInfo` })}
              key="configenvInfo"
            >{this.getConfigInfo()}
            </TabPane>
          </Tabs>
        </Content>
      );
  }
}
