package com.innocnetcoder.drplantae.modal;

public class Plants {

    String PlantName, PlantDescription, PlantPrice, PlantType, PlantSize, CurrentTime, CurrentDate, PlantID, ImageOne;

    public Plants() {
    }

    public Plants(String plantName, String plantDescription, String plantPrice, String plantType, String plantSize, String currentTime, String currentDate, String plantID, String imageOne) {
        PlantName = plantName;
        PlantDescription = plantDescription;
        PlantPrice = plantPrice;
        PlantType = plantType;
        PlantSize = plantSize;
        CurrentTime = currentTime;
        CurrentDate = currentDate;
        PlantID = plantID;
        ImageOne = imageOne;
    }

    public String getPlantName() {
        return PlantName;
    }

    public void setPlantName(String plantName) {
        PlantName = plantName;
    }

    public String getPlantDescription() {
        return PlantDescription;
    }

    public void setPlantDescription(String plantDescription) {
        PlantDescription = plantDescription;
    }

    public String getPlantPrice() {
        return PlantPrice;
    }

    public void setPlantPrice(String plantPrice) {
        PlantPrice = plantPrice;
    }

    public String getPlantType() {
        return PlantType;
    }

    public void setPlantType(String plantType) {
        PlantType = plantType;
    }

    public String getPlantSize() {
        return PlantSize;
    }

    public void setPlantSize(String plantSize) {
        PlantSize = plantSize;
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

    public String getPlantID() {
        return PlantID;
    }

    public void setPlantID(String plantID) {
        PlantID = plantID;
    }

    public String getImageOne() {
        return ImageOne;
    }

    public void setImageOne(String imageOne) {
        ImageOne = imageOne;
    }
}
