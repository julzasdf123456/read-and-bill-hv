package com.lopez.julz.readandbillhv.dao;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Readings {
    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name = "AccountNumber")
    private String AccountNumber;

    @ColumnInfo(name = "ServicePeriod")
    private String ServicePeriod;

    @ColumnInfo(name = "ReadingTimestamp")
    private String ReadingTimestamp;

    @ColumnInfo(name = "KwhUsed")
    private String KwhUsed;

    @ColumnInfo(name = "DemandKwhUsed")
    private String DemandKwhUsed;

    @ColumnInfo(name = "Notes")
    private String Notes;

    @ColumnInfo(name = "Latitude")
    private String Latitude;

    @ColumnInfo(name = "Longitude")
    private String Longitude;

    @ColumnInfo(name = "UploadStatus")
    private String UploadStatus;

    @ColumnInfo(name = "FieldStatus")
    private String FieldStatus;

    @ColumnInfo(name = "MeterReader")
    private String MeterReader;

    @ColumnInfo(name = "SolarKwhUsed")
    private String SolarKwhUsed;

    public Readings() {}

    public Readings(@NonNull String id, String accountNumber, String servicePeriod, String readingTimestamp, String kwhUsed, String demandKwhUsed, String notes, String latitude, String longitude, String uploadStatus, String fieldStatus, String meterReader, String solarKwhUsed) {
        this.id = id;
        AccountNumber = accountNumber;
        ServicePeriod = servicePeriod;
        ReadingTimestamp = readingTimestamp;
        KwhUsed = kwhUsed;
        DemandKwhUsed = demandKwhUsed;
        Notes = notes;
        Latitude = latitude;
        Longitude = longitude;
        UploadStatus = uploadStatus;
        FieldStatus = fieldStatus;
        MeterReader = meterReader;
        SolarKwhUsed = solarKwhUsed;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return AccountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        AccountNumber = accountNumber;
    }

    public String getServicePeriod() {
        return ServicePeriod;
    }

    public void setServicePeriod(String servicePeriod) {
        ServicePeriod = servicePeriod;
    }

    public String getReadingTimestamp() {
        return ReadingTimestamp;
    }

    public void setReadingTimestamp(String readingTimestamp) {
        ReadingTimestamp = readingTimestamp;
    }

    public String getKwhUsed() {
        return KwhUsed;
    }

    public void setKwhUsed(String kwhUsed) {
        KwhUsed = kwhUsed;
    }

    public String getDemandKwhUsed() {
        return DemandKwhUsed;
    }

    public void setDemandKwhUsed(String demandKwhUsed) {
        DemandKwhUsed = demandKwhUsed;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getUploadStatus() {
        return UploadStatus;
    }

    public void setUploadStatus(String uploadStatus) {
        UploadStatus = uploadStatus;
    }

    public String getFieldStatus() {
        return FieldStatus;
    }

    public void setFieldStatus(String fieldStatus) {
        FieldStatus = fieldStatus;
    }

    public String getMeterReader() {
        return MeterReader;
    }

    public void setMeterReader(String meterReader) {
        MeterReader = meterReader;
    }

    public String getSolarKwhUsed() {
        return SolarKwhUsed;
    }

    public void setSolarKwhUsed(String solarKwhUsed) {
        SolarKwhUsed = solarKwhUsed;
    }
}
