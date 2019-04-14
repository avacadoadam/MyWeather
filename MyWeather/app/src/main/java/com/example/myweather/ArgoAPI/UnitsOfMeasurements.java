package com.example.myweather.ArgoAPI;

/**
 * A enum represent the type of units measurements Agro API can return.
 */
public enum UnitsOfMeasurements {
        //TODO make enum get strings from xml values
    Metric("Metric"), Imperial("Imperial");

    private final String unitName;

    UnitsOfMeasurements(String unitName) {
        this.unitName = unitName;
    }

    public String getUnitName() {
        return unitName;
    }

}
