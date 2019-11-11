export default {
  autoQuery: false,
  selection: 'single',
  paging: false,
  transport: {
    read: ({ data }) => ({
      url: '/base/v1/users/enable_user/route_rule_unused',
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
