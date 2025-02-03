package com.lopez.julz.readandbillhv.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {
        TrackNames.class,
        Tracks.class,
        Users.class,
        ReadingSchedules.class,
        DownloadedPreviousReadings.class,
        Readings.class,
        Rates.class,
        Bills.class,
        ReadingImages.class,
        DisconnectionList.class,
        Settings.class,
    }, version = 86)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TrackNamesDao trackNamesDao();

    public abstract TracksDao tracksDao();

    public abstract UsersDao usersDao();

    public abstract ReadingSchedulesDao readingSchedulesDao();

    public abstract DownloadedPreviousReadingsDao downloadedPreviousReadingsDao();

    public abstract ReadingsDao readingsDao();

    public abstract RatesDao ratesDao();

    public abstract BillsDao billsDao();

    public abstract ReadingImagesDao readingImagesDao();

    public abstract DisconnectionListDao disconnectionListDao();

    public abstract SettingsDao settingsDao();
}
