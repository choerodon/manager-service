export default {
  autoQuery: false,
  selection: 'single',
  paging: false,
  transport: {
    read: ({ data }) => ({
      url: '/base/v1/site/enableUsers',
      method: 'get',
      params: {
        ...data,
      },
    }),
  },
  fields: [
    { name: 'realName', type: 'string' },
    { name: 'loginName', type: 'string' },
    { name: 'id', type: 'number', unique: true },
  ],
};
