import { DataSet } from 'choerodon-ui/pro';

export default function (children) {
  const sourceTypeDataSet = new DataSet({
    fields: [
      { name: 'text', type: 'string' },
      { name: 'value', type: 'string' },
    ],
    data: [
      { text: '自定义', value: 'custom' },
      { text: '系统预置', value: 'pod' },
    ],
  });

  const hostNameValidator = (value) => {
    // eslint-disable-next-line no-useless-escape
    const pattern = /^[a-zA-Z0-9.·\-_\s\u4e00-\u9fa5]*$/;
    if (!pattern.test(value)) {
      return '主机名称只能由汉字、字母大小写、数字、"_"、"."、"-"、空格组成';
    }
    if (value.length > 30) {
      return '当前描述限制30字符';
    }
    return true;
  };

  const ipAddressValidator = (value) => {
    const pattern = /^((25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(25[0-5]|2[0-4]\d|[01]?\d\d?)$/;
    if (!pattern.test(value) || value === '0.0.0.0' || value === '255.255.255.255') {
      return '请输入合法IP';
    }
    return true;
  };

  const appNameValidator = (value) => {
    // eslint-disable-next-line no-useless-escape
    const pattern = /^[a-zA-Z0-9.·\-_\/\s]*$/;
    if (!pattern.test(value)) {
      return '服务名称只能由字母大小写、数字、"_"、"."、"-"、"/"、空格组成';
    }
    if (value && value.length > 30) {
      return '当前描述限制30字符';
    }
    return true;
  };

  const portValidator = (value) => {
    if (value < 0 || value > 65535) {
      return '请输入合法端口号';
    }
    return true;
  };

  return {
    autoQuery: true,
    selection: false,
    parentField: 'parentServiceName',
    idField: 'serviceName',
    // expandField: 'expand',
    queryFields: [
      { name: 'app_name', type: 'string', label: '服务名称' },
      { name: 'host_name', type: 'string', label: '主机名称' },
      { name: 'ip_addr', type: 'string', label: 'IP' },
      { name: 'port', type: 'string', label: '端口' },
      { name: 'source_type', type: 'string', label: '来源', options: sourceTypeDataSet, textField: 'text', valueField: 'value' },
    ],
    fields: [
      { name: 'serviceHostName', type: 'string', label: '主机/服务名称', ignore: 'always' },
      { name: 'appName', type: 'string', label: '服务名称', required: true, validator: appNameValidator },
      { name: 'hostName', type: 'string', label: '主机名称', validator: hostNameValidator, required: true },
      { name: 'ipAddr', type: 'string', label: 'IP', required: true, validator: ipAddressValidator },
      { name: 'port', type: 'string', label: '端口', validator: portValidator, required: true },
      { name: 'sourceType', type: 'string', label: '来源', ignore: 'always' },
      { name: 'createDate', type: 'date', label: '创建时间', ignore: 'always' },
    ],
    children: {
      hosts: children,
    },
    transport: {
      read: {
        url: '/manager/v1/hosts',
        method: 'get',
        transformResponse(JSONData) {
          const serviceArr = JSON.parse(JSONData).list.map((item) => ({ serviceHostName: item.appName, serviceName: item.appName }));
          const hostArr = [];
          JSON.parse(JSONData).list.forEach((item) => hostArr.push(...item.hosts.map((hostItem) => ({
            ...hostItem,
            parentServiceName: hostItem.appName,
            serviceHostName: hostItem.metadata ? hostItem.metadata.hostName : null,
          }))));
          return {
            ...JSON.parse(JSONData),
            list: [...serviceArr, ...hostArr],
          };
        },
      },
      create: ({ data }) => ({
        url: `manager/v1/hosts/${data[0].appName}`,
        method: 'post',
        data: {
          ...data[0],
          appName: undefined,
        },
      }),
      destroy: ({ data: [{ appName, instanceId }] }) => ({
        url: `manager/v1/hosts/${appName}/${instanceId}`,
        method: 'delete',
      }),
    },
  };
}
