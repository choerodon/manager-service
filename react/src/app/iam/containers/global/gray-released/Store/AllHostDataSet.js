export default {
  autoQuery: false,
  selection: 'single',
  paging: false,
  transport: {
    read: ({ data }) => ({
      url: '/manager/v1/hosts/search',
      method: 'get',
      params: {
        ...data,
      },
    }),
  },
  fields: [
    { name: 'hostName', type: 'string' },
    { name: 'instanceId', type: 'string', unique: true },
  ],
};
