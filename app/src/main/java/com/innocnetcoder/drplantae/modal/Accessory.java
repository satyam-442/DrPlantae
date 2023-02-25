package com.innocnetcoder.drplantae.modal;

public class Accessory {

    String AccessoryName, AccessoryDescription, AccessoryPrice, AccessoryType, AccessorySize, AccessoryID, CurrentTime, CurrentDate, ImageOne;

    public Accessory() {
    }

    public Accessory(String accessoryName, String accessoryDescription, String accessoryPrice, String accessoryType, String accessorySize, String accessoryID, String currentTime, String currentDate, String imageOne) {
        AccessoryName = accessoryName;
        AccessoryDescription = accessoryDescription;
        AccessoryPrice = accessoryPrice;
        AccessoryType = accessoryType;
        AccessorySize = accessorySize;
        AccessoryID = accessoryID;
        CurrentTime = currentTime;
        CurrentDate = currentDate;
        ImageOne = imageOne;
    }

    public String getAccessoryName() {
        return AccessoryName;
    }

    public void setAccessoryName(String accessoryName) {
        AccessoryName = accessoryName;
    }

    public String getAccessoryDescription() {
        return AccessoryDescription;
    }

    public void setAccessoryDescription(String accessoryDescription) {
        AccessoryDescription = accessoryDescription;
    }

    public String getAccessoryPrice() {
        return AccessoryPrice;
    }

    public void setAccessoryPrice(String accessoryPrice) {
        AccessoryPrice = accessoryPrice;
    }

    public String getAccessoryType() {
        return AccessoryType;
    }

    public void setAccessoryType(String accessoryType) {
        AccessoryType = accessoryType;
    }

    public String getAccessorySize() {
        return AccessorySize;
    }

    public void setAccessorySize(String accessorySize) {
        AccessorySize = accessorySize;
    }

    public String getAccessoryID() {
        return AccessoryID;
    }

    public void setAccessoryID(String accessoryID) {
        AccessoryID = accessoryID;
    }

    public String getCurrentTime() {
        return CurrentTime;
    }

    public void setCurrentTime(String currentTime) {
        CurrentTime = currentTime;
    }

    public String getCurrentDate() {
        return CurrentDate;
    }

    public void setCurrentDate(String currentDate) {
        CurrentDate = currentDate;
    }

    public String getImageOne() {
        return ImageOne;
    }

    public void setImageOne(String imageOne) {
        ImageOne = imageOne;
    }
}