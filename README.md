# react-native-invengo

## 安装

```
yarn add react-native-invengo
react-native link react-native-invengo
```

## 使用

```js
import { DeviceEventEmitter } from 'react-native';
import RFID from "react-native-invengo";

// 开始连接
RFID.connect((isSuccess) => {
    ...
})

// 断开连接
RFID.disConnect()

// 开始扫描
RFID.startSend()

// 停止扫描
RFID.stopSend()

// 扫描结果
DeviceEventEmitter.addListener('RFID.onTag', (params) => {
  console.log(params.epc)
})
```