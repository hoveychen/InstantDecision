package com.google.instantdecision.model;

/**
 * Created by chenyuheng on 12/16/14.
 */
public class Identifier {
    private String deviceId;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Identifier)) {
            return false;
        }
        return deviceId.equals(((Identifier) o).deviceId);
    }
}
