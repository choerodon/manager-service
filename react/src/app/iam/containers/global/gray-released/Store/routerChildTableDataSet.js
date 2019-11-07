export default function (data) {
  return {
    autoQuery: false,
    selection: false,
    paging: false,
    fields: [{
      name: 'metadataHostName',
      type: 'string',
      label: '主机名称',
      bind: 'metadata.hostName',
    }, {
      name: 'metadata',
      type: 'object',
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
    data,
  };
}
