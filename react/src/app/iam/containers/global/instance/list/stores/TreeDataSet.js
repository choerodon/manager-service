export default (intl, intlPrefix) => {
  const tenantFlag = intl.formatMessage({ id: `${intlPrefix}.model.visible` });
  return {
    autoQuery: true,
    pageSize: -1,
    selection: 'single',
    idField: 'instanceId',
    expandField: 'expand',
    parentField: 'service',
    fields: [
      { name: 'expand', type: 'boolean' },
    ],
    transport: {
      read: {
        url: '/manager/v1/instances',
        method: 'get',
        transformResponse: (data) => {
          const parsedData = JSON.parse(data);
          const parentNodes = new Set();
          parsedData.list.map((v) => {
            if (!parentNodes.has(v.service)) {
              parentNodes.add(v.service);
            }
            return true;
          });
          parsedData.list = parsedData.list.concat([...parentNodes].map((v, index) => {
            if (index === 0) {
              return { instanceId: v, service: null, expand: true };
            }
            return { instanceId: v, service: null };
          }));
          return parsedData;
        },
      },
    },
  };
};
