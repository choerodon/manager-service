export default function () {
  return {
    autoQuery: false,
    selection: false,
    paging: false,
    fields: [{
      name: 'hostName',
      type: 'string',
      label: '主机名称',
    }, {
      name: 'ipAddr',
      type: 'string',
      label: 'IP',
    }, {
      name: 'port',
      type: 'string',
      label: '端口',
    }, {
      name: 'appName',
      type: 'string',
      label: '服务',
    }, {
      name: 'sourceType',
      type: 'string',
      label: '来源',
    }],
  };
}
