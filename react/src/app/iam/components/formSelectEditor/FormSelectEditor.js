import React, { useRef, useContext, useState, useEffect } from 'react';
import { runInAction } from 'mobx';
import { Form, DataSet, Button, Icon } from 'choerodon-ui/pro';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react-lite';
import Store from './stores';
import './index.less';

export default observer(({ name, optionDataSetConfig, optionDataSet, record, children, addButton, maxDisable, canDeleteAll = true, idField, alwaysRequired = false, required = false, header }) => {
  const formElement = useRef(null);

  async function handleSubmit({ dataSet, data }) {
    const result = await formElement.current.checkValidity();
    return result;
  }
  useEffect(() => {
    if (record) {
      record.dataSet.addEventListener('submit', handleSubmit);
    }
    return () => {
      if (record) {
        record.dataSet.removeEventListener('submit', handleSubmit);
      }
    };
  });

  if (!record) { return null; }
  const { dsStore } = useContext(Store);
  const valueField = record && record.fields.get(name).get('valueField');
  const textField = record && record.fields.get(name).get('textField');
  
  function handleCreatOther() {
    if (idField) {
      record.set(name, (record.get(name) || []).concat({ [idField]: '' }));
    } else {
      record.set(name, (record.get(name) || []).concat(''));
    }
  }

  function handleChange(e, index) {
    debugger;
    const changedValue = record.get(name);
    if (idField) {
      const newValue = changedValue.get(index);
      newValue[idField] = e;
      // newValue.dirty = true;
      record.set('__status', 'update');
      record.set('dirty', true);
      changedValue.set(index, newValue);
    } else {
      changedValue[index] = e;
      record.set(name, changedValue);
    }
  }

  // 去除内部的重复选项
  function optionsFilter(optionRecord, selfValue) {
    // 不过滤自身
    if (optionRecord.get(valueField) === selfValue) {
      return true;
    }
    if (idField) {
      if (record.get(name) && (record.get(name).map((v) => v[idField]) || []).includes(optionRecord.get(valueField))) {
        return false;
      }
    }
    if (record.get(name) && (record.get(name).map((item) => (item instanceof Object ? item[valueField] : item)) || []).includes(optionRecord.get(valueField))) {
      return false;
    }
    return true;
  }

  // 判断添加按钮是否禁用
  function buttonDisable() {
    if (maxDisable && (record.get(name) || []).length >= optionDataSet.totalCount) {
      return true;
    }
    if (idField) {
      return (record.get(name) || []).some((v) => v[idField] === '');
    }
    return (record.get(name) || []).some((v) => v === '');
  }
  // 判断添加按钮是否可见
  function buttonVisibility() {
    if (maxDisable && (record.get(name) || []).length >= optionDataSet.totalCount) {
      return false;
    }
    return true;
  }

  function handleDeleteItem(index) {
    const arr = record.get(name) || [];
    arr.splice(index, 1);
    record.set('__status', 'update');
    record.set('dirty', true);
    runInAction(() => {
      dsStore.splice(index, 1);
      record.set(name, arr.slice());
    });
  }

  return (
    <React.Fragment>
      <Form ref={formElement} className="form-select-editor" columns={12} header={header}>
        {(record.get(name) || []).map((v, index) => {
          if (idField) {
            v = v[idField];
          }
          if (!dsStore.get(index)) {
            dsStore.set(index, optionDataSet || new DataSet(optionDataSetConfig));
          }
          return [
            React.createElement(children, { 
              onChange: (text) => handleChange(text, index),
              value: v,
              options: optionDataSet || dsStore.get(index),
              optionsFilter: (optionRecord) => optionsFilter(optionRecord, v),
              textField,
              valueField,
              allowClear: false,
              clearButton: false,
              colSpan: 11,
              label: record.fields.get(name).get('label'),
              required: (record.get(name).length > 1 ? false : required || record.fields.get(name).get('required')) || alwaysRequired,
            }),
            !canDeleteAll && (record.get(name) || []).length <= 1 ? undefined : (
              <Button 
                colSpan={1}
                className="form-select-editor-button"
                disabled={!canDeleteAll && (record.get(name) || []).length <= 1}
                onClick={() => handleDeleteItem(index)}
                icon="delete"
              />
            ),    
          ];
        })}
      
      </Form>
      {buttonVisibility() && (
        <Button
          colSpan={12}
          disabled={buttonDisable()} 
          color={buttonDisable() ? 'gray' : 'blue'}
          onClick={handleCreatOther}
          style={{ textAlign: 'left', marginTop: '-0.04rem' }}
          icon="add"
        >
          {addButton || '添加'}
        </Button>
      )}
    </React.Fragment>
  );
});
