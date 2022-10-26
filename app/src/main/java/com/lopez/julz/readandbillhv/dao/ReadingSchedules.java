package com.lopez.julz.readandbillhv.dao;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ReadingSchedules {
    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name = "ServicePeriod")
    private String ServicePeriod;

    @ColumnInfo(name = "Town")
    private String Town;

    @ColumnInfo(name = "BAPAName")
    private String BAPAName;

    @ColumnInfo(name = "Status")
    private String Status;

    @ColumnInfo(name = "created_at")
    private String created_at;

    @ColumnInfo(name = "update_at")
    private String update_at;

    @ColumnInfo(name = "Disabled")
    private String Disabled;

    public ReadingSchedules() {
    }

    public ReadingSchedules(@NonNull String id, String servicePeriod, String town, String BAPAName, String status, String created_at, String update_at, String disabled) {
        this.id = id;
        ServicePeriod = servicePeriod;
        Town = town;
        this.BAPAName = BAPAName;
        Status = status;
        this.created_at = created_at;
        this.update_at = update_at;
        Disabled = disabled;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getServicePeriod() {
        return ServicePeriod;
    }

    public void setServicePeriod(String servicePeriod) {
        ServicePeriod = servicePeriod;
    }

    public String getTown() {
        return Town;
    }

    public void setTown(String town) {
        Town = town;
    }

    public String getBAPAName() {
        return BAPAName;
    }

    public void setBAPAName(String BAPAName) {
        this.BAPAName = BAPAName;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdate_at() {
        return update_at;
    }

    public void setUpdate_at(String update_at) {
        this.update_at = update_at;
    }

    public String getDisabled() {
        return Disabled;
    }

    public void setDisabled(String disabled) {
        Disabled = disabled;
    }
}
