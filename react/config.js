const config = {
  server: 'http://api.staging.saas.hand-china.com',
  master: './node_modules/@choerodon/master/lib/master.js',
  projectType: 'choerodon',
  buildType: 'single',
  dashboard: {},
  modules: [
    '.',
  ],
  resourcesLevel: ['site', 'organization', 'project', 'user'],
};

module.exports = config;
