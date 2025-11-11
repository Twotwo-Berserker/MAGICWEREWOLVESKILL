package com.twotwo.model;



public class Location {
    public enum LocationType {
        DRAGON_CAVE, KITCHEN, STORE, LIBRARY
    }

    private LocationType locationType;

    public Location(LocationType locationType) {
        this.locationType = locationType;
    }

    public LocationType getType() {
        return this.locationType;
    }
}
