export default ({ intl, intlPrefix, metadataDataSet }) => ({
  autoQuery: false,
  selection: 'single',
  paging: false,
  transport: {
    read: ({ data: { instanceId } }) => ({
      url: `/hadm/v1/instances/${instanceId}?page=1`,
      method: 'get',
      dataKey: null,
    }),
  },
  fields: [
    { name: 'instanceId', type: 'string', label: intl.formatMessage({ id: `${intlPrefix}.instanceid` }) },
    { name: 'hostName', type: 'string', label: intl.formatMessage({ id: `${intlPrefix}.hostname` }) },
    { name: 'ipAddr', type: 'string', label: intl.formatMessage({ id: `${intlPrefix}.ip` }) },
    { name: 'app', type: 'string', label: intl.formatMessage({ id: `${intlPrefix}.service` }) },
    { name: 'port', type: 'string', label: intl.formatMessage({ id: `${intlPrefix}.port` }) },
    { name: 'version', type: 'string', label: intl.formatMessage({ id: `${intlPrefix}.version` }) },
    { name: 'registrationTime', type: 'string', label: intl.formatMessage({ id: `${intlPrefix}.registertime` }) },
    { name: 'metadata', type: 'string', label: intl.formatMessage({ id: `${intlPrefix}.metadata` }) },
  ],

  events: {
    load: ({ dataSet }) => {
      let metadata = dataSet.current && dataSet.current.get('metadata');
      metadata = Object.keys(metadata).map((key) => ({
        key,
        value: metadata[key], 
      }));
      metadataDataSet.loadData(metadata);
    },
  },
});
