package com.example.joochankim.thelastchans;

public class GraphValue {
    String currentTime, hRate, steps;

    public GraphValue() {
    }

    public GraphValue(String tValue, String hrValue, String stepsValue) {
        this.currentTime = tValue;
        this.hRate = hrValue;
        this.steps = stepsValue;
    }

    public String gettimeValue() {
        return currentTime;
    }

    public String gethrValue() {
        return hRate;
    }

    public String getstepsValue() {
        return steps;
    }

}
