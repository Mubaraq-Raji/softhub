package com.harlharjj.softhub;

public class Models {

    String name;
    String type;
    String sensorType;
    String switchType;
    String socketType;
    String location;
    String roomName;
    String device_key;
    int status;
    long deviceId;
    int image, position;

    public Models() {
    }


    public Models (String name, String type, int image, int position, String sensorType, String switchType, String socketType, String location, String roomName, long deviceId, String device_key, int status) {
        this.name = name;
        this.type = type;
        this.image = image;
        this.position = position;
        this.roomName = roomName;
        this.sensorType = sensorType;
        this.socketType = socketType;
        this.switchType = switchType;
        this.location = location;
        this.deviceId = deviceId;
        this.device_key = device_key;
        this.status = status;
    }


    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition() {
        this.position = position;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public String getSwitchType() {
        return switchType;
    }

    public void setSwitchType(String switchType) {
        this.switchType = switchType;
    }

    public String getSocketType() {
        return socketType;
    }

    public void setSocketType(String socketType) {
        this.socketType = socketType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;

    }

    public String getDevice_key() {
        return device_key;
    }

    public void setDevice_key(String device_key) {
        this.device_key = device_key;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }




}
