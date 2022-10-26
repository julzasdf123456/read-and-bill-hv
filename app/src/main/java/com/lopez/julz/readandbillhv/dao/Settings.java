package com.lopez.julz.readandbillhv.dao;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Settings {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;

    @ColumnInfo(name = "DefaultServer")
    private String DefaultServer;

    @ColumnInfo(name = "DefaultOffice")
    private String DefaultOffice;

    @ColumnInfo(name = "BAPAName")
    private String BAPAName;

    public Settings() {
    }

    public Settings(String defaultServer, String defaultOffice, String BAPAName) {
        DefaultServer = defaultServer;
        DefaultOffice = defaultOffice;
        this.BAPAName = BAPAName;
    }

    public Settings(int id, String defaultServer, String defaultOffice) {
        this.id = id;
        DefaultServer = defaultServer;
        DefaultOffice = defaultOffice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDefaultServer() {
        return DefaultServer;
    }

    public void setDefaultServer(String defaultServer) {
        DefaultServer = defaultServer;
    }

    public String getDefaultOffice() {
        return DefaultOffice;
    }

    public void setDefaultOffice(String defaultOffice) {
        DefaultOffice = defaultOffice;
    }

    public String getBAPAName() {
        return BAPAName;
    }

    public void setBAPAName(String BAPAName) {
        this.BAPAName = BAPAName;
    }
}
