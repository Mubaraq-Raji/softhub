package com.harlharjj.softhub;

public class TempModel {

    String temperatures;
    String humiditys;

    public TempModel(String temperatures, String humiditys) {
        this.temperatures = temperatures;
        this.humiditys = humiditys;
    }

    public TempModel() {
    }

    public String getTemperatures() {
        return temperatures;
    }

    public void setTemperatures(String temperatures) {
        this.temperatures = temperatures;
    }

    public String getHumiditys() {
        return humiditys;
    }

    public void setHumiditys(String humiditys) {
        this.humiditys = humiditys;
    }


}
