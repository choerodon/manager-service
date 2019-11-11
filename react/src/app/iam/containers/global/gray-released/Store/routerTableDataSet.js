import { axios } from '@choerodon/boot';
import { message } from 'choerodon-ui/pro';

export default function (children) {
  const codeValidator = async (value, name, record) => {
    const res = await axios.get('manager/v1/route_rules/check_code', { params: { code: value } });
    if (value !== record.getPristineValue(name)) {
      if (res.failed) {
        message.error(res.message);
        return true;
      }
      // eslint-disable-next-line no-useless-escape
      if (!/^[a-zA-Z0-9.·\-_\s]+$/.test(value)) {
        return '路由编码只能由字母大小写、数字、"_"、"."、"-"、空格组成';
      }
      if (value && value.length > 30) {
        return '当前输入限制30字符';
      }
      if (!res) {
        return '路由编码重复';
      }
    }
    return true;
  };
  const descriptionValidator = (value) => {
    if (value && value.length > 200) {
      return '当前输入限制200字符';
    }
    return true;
  };
  return {
    autoQuery: true,
    selection: false,
    paging: true,
    queryFields: [
      { name: 'code', type: 'string', label: '路由编码' },
    ],
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
      validator: descriptionValidator,
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
      textField: 'metadata.hostName',
      valueField: 'instanceId',
      label: '主机',
    }, {
      name: 'hostDTOS',
      type: 'auto',
    }],
    // children: {
    //   hostDTOS: children,
    // },
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
              userIds: item.routeMemberRuleDTOS ? item.routeMemberRuleDTOS.map((user) => ({ ...user, id: user.userId })) : [],
              instanceIds: item.hostDTOS,
            })),
          };
        },
      },
      create: ({ data }) => ({
        url: '/manager/v1/route_rules',
        method: 'post',
        data: {
          ...data[0],
          instanceIds: data[0].instanceIds ? data[0].instanceIds.filter((item) => item !== '') : [],
          userIds: data[0].userIds ? data[0].userIds.filter((item) => item !== '') : [],
          hostDTOS: undefined,
        },
      }),
      update: ({ data: [{ instanceIds, userIds, code, description, objectVersionNumber, id }] }) => ({
        url: `/manager/v1/route_rules/${id}`,
        method: 'put',
        data: {
          instanceIds: instanceIds.filter((item) => item !== '').map((item) => (typeof item === 'string' ? item : item.instanceId)),
          userIds: userIds.filter((item) => item !== '').map((item) => (typeof item === 'number' ? item : item.userId)),
          code,
          description,
          objectVersionNumber,
        },
      }),
      destroy: ({ data }) => ({
        url: `/manager/v1/route_rules/${data[0].id}`,
        method: 'delete',
      }),
    },
  };
}
