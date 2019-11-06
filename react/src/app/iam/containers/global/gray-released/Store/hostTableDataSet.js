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
  return {
    autoQuery: true,
    selection: false,
    parentField: 'parentServiceName',
    idField: 'serviceName',
    expandField: 'expand',
    queryFields: [
      { name: 'app_name', type: 'string', label: '服务名称' },
      { name: 'host_name', type: 'string', label: '主机名称' },
      { name: 'ip_addr', type: 'string', label: 'IP' },
      { name: 'port', type: 'string', label: '端口' },
      { name: 'source_type', type: 'string', label: '来源', options: sourceTypeDataSet },
    ],
    fields: [
      { name: 'serviceHostName', type: 'string', label: '主机/服务名称', ignore: 'always' },
      { name: 'appName', type: 'string', label: '服务名称' },
      { name: 'hostName', type: 'string', label: '主机名称' },
      { name: 'ipAddr', type: 'string', label: 'IP' },
      { name: 'port', type: 'string', label: '端口' },
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
          const serviceArr = JSON.parse(JSONData).list.map((item) => ({ serviceHostName: item.appName, serviceName: item.appName, expand: true }));
          const hostArr = [];
          JSON.parse(JSONData).list.forEach((item) => hostArr.push(...item.hosts.map((hostItem) => ({
            ...hostItem,
            parentServiceName: hostItem.appName,
            serviceHostName: hostItem.hostName,
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
