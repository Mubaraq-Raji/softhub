package com.harlharjj.softhub;

public class Model {

    String name;
    String type;
    String sensorType;
    String switchType;
    String socketType;
    String location;
    int image, count;
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

//    public Model (String name, String type, int image, int count) {
//        this.name = name;
//        this.type = type;
//        this.image = image;
//        this.count = count;
//    }
//
//    public Model() {
//    }

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



}
