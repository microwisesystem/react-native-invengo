package com.bastengao.invengo;

import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;


import invengo.javaapi.core.BaseReader;
import invengo.javaapi.core.IMessageNotification;
import invengo.javaapi.handle.IMessageNotificationReceivedHandle;
import invengo.javaapi.protocol.IRP1.IntegrateReaderManager;
import invengo.javaapi.protocol.IRP1.PowerOff;
import invengo.javaapi.protocol.IRP1.RXD_TagData;
import invengo.javaapi.protocol.IRP1.ReadTag;
import invengo.javaapi.protocol.IRP1.Reader;

public class RFIDModule extends ReactContextBaseJavaModule implements IMessageNotificationReceivedHandle {
    private final static String EventName = "RFID.onTag";
    private ReactApplicationContext reactContext;

    private Reader reader;
    private Callback cb;

    public RFIDModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;

        this.reader = IntegrateReaderManager.getInstance();
        this.reader.onMessageNotificationReceived.add(this);
    }

    @Override
    public String getName() {
        return "RFID";
    }


    @ReactMethod
    public void connect(Callback cb) {
        cb.invoke(this.reader.connect());
    }

    @ReactMethod
    public void disConnect() {
        this.reader.disConnect();
    }

    @ReactMethod
    public void isConnected(Callback cb) {
        cb.invoke(this.reader.isConnected());
    }

    @ReactMethod
    public void startSend() {
        ReadTag readTag = new ReadTag(ReadTag.ReadMemoryBank.EPC_6C);
        this.reader.send(readTag);
    }

    @ReactMethod
    public void stopSend() {
        this.reader.send(new PowerOff());
    }

    @Override
    public void messageNotificationReceivedHandle(BaseReader baseReader, IMessageNotification msg) {
        if (msg instanceof RXD_TagData) {
            Log.i("react-native-invengo", "msg " + msg);
            RXD_TagData tag = (RXD_TagData) msg;
            sendEvent(tag);
        }
    }


    private void sendEvent(RXD_TagData tagData) {
        DeviceEventManagerModule.RCTDeviceEventEmitter deviceEventEmitter = reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);

        byte[] epc = tagData.getReceivedMessage().getEPC();
        StringBuilder s = new StringBuilder();
        for(byte b : epc) {
            s.append("".format("%02X", b));
        }

        WritableMap params = Arguments.createMap();
        params.putString("ecp", s.toString());
        deviceEventEmitter.emit(EventName, params);
    }
}