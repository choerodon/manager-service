import React from 'react';
import { Form, TextField } from 'choerodon-ui/pro';

const CreateHost = ({ record }) => (
  <Form record={record}>
    <TextField name="hostName" />
    <TextField name="ipAddr" />
    <TextField name="port" />
    <TextField name="appName" />
  </Form>
);

export default CreateHost;
