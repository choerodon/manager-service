import { axios } from '@choerodon/boot';
import { message } from 'choerodon-ui/pro';

export default function (children) {
  const codeValidator = async (value, name, record) => {
    const res = await axios.post('manager/v1/route_rules/check', { code: value });
    if (value !== record.getPristineValue(name)) {
      if (res.failed) {
        message.error(res.message);
      }
      if (!res) {
        return '路由编码重复';
      }
    }
    return true;
  };
  return {
    autoQuery: true,
    selection: false,
    paging: true,
    children: {
      hostDTOS: children,
    },
    fields: [{
      name: 'code',
      type: 'string',
      label: '路由编码',
      validator: codeValidator,
      required: true,
    }, {
      name: 'description',
      type: 'string',
      label: '路由说明',
    }, {
      name: 'hostNumber',
      type: 'string',
      label: '主机数',
      ignore: 'always',
    }, {
      name: 'userNumber',
      type: 'string',
      label: '用户数',
      ignore: 'always',
    }, {
      name: 'creationDate',
      type: 'date',
      label: '创建时间',
      ignore: 'always',
    }, {
      name: 'hostArr',
      type: 'arr',
      ignore: 'always',
    }, {
      name: 'userIds',
      type: 'auto',
      textField: 'realName',
      valueField: 'id',
      label: '用户',
    }, {
      name: 'instanceIds',
      type: 'auto',
      textField: 'hostName',
      valueField: 'instanceId',
      label: '主机',
    }],
    transport: {
      read: {
        url: '/manager/v1/route_rules',
        method: 'get',
        transformResponse(JSONData) {
          const data = JSON.parse(JSONData);
          return {
            ...data,
            list: data.list.map((item) => ({
              ...item,
              userIds: item.routeMemberRuleDTOS.map((user) => ({ ...user, id: user.userId })),
              instanceIds: item.hostDTOS,
            })),
          };
        },
      },
      create: ({ data }) => ({
        url: '/manager/v1/route_rules/insert',
        method: 'post',
        data: {
          ...data[0],
          hostDTOS: undefined,
        },
      }),
      update: ({ data: [{ instanceIds, userIds, code, description, objectVersionNumber, id }] }) => ({
        url: '/manager/v1/route_rules/update',
        method: 'post',
        data: {
          instanceIds: instanceIds.map((item) => (typeof item === 'string' ? item : item.instanceId)),
          userIds: userIds.map((item) => (typeof item === 'number' ? item : item.userId)),
          code,
          description,
          objectVersionNumber,
          id,
        },
      }),
      destroy: ({ data }) => ({
        url: `/manager/v1/route_rules/${data[0].id}`,
        method: 'delete',
      }),
    },
  };
}
