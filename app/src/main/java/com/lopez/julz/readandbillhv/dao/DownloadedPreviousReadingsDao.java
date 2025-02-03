package com.lopez.julz.readandbillhv.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DownloadedPreviousReadingsDao {
    @Query("SELECT * FROM DownloadedPreviousReadings")
    List<DownloadedPreviousReadings> getAll();

    @Query("SELECT * FROM DownloadedPreviousReadings WHERE id = :id")
    DownloadedPreviousReadings getOne(String id);

    @Query("SELECT * FROM DownloadedPreviousReadings WHERE AccountId = :id AND ServicePeriod = :period")
    DownloadedPreviousReadings getOneSpecific(String id, String period);

    @Insert
    void insertAll(DownloadedPreviousReadings... downloadedPreviousReadings);

    @Update
    void updateAll(DownloadedPreviousReadings... downloadedPreviousReadings);

    @Query("DELETE FROM DownloadedPreviousReadings WHERE ServicePeriod = :servicePeriod")
    void deleteAllByServicePeriod(String servicePeriod);

    @Query("SELECT * FROM DownloadedPreviousReadings WHERE ServicePeriod = :servicePeriod AND OrganizationParentAccount = :bapaName ORDER BY ServiceAccountName")
    List<DownloadedPreviousReadings> getAllFromSchedule(String servicePeriod, String bapaName);

    @Query("SELECT * FROM DownloadedPreviousReadings WHERE CAST(SequenceCode AS INT) > :sequenceCode ORDER BY CAST(SequenceCode AS INT) LIMIT 1")
    DownloadedPreviousReadings getNext(int sequenceCode);

    @Query("SELECT * FROM DownloadedPreviousReadings WHERE CAST(SequenceCode AS INT) < :sequenceCode ORDER BY CAST(SequenceCode AS INT) DESC LIMIT 1")
    DownloadedPreviousReadings getPrevious(int sequenceCode);

    @Query("SELECT * FROM DownloadedPreviousReadings ORDER BY CAST(SequenceCode AS INT) LIMIT 1")
    DownloadedPreviousReadings getFirst();

    @Query("SELECT * FROM DownloadedPreviousReadings ORDER BY CAST(SequenceCode AS INT) DESC LIMIT 1")
    DownloadedPreviousReadings getLast();

    @Query("SELECT * FROM DownloadedPreviousReadings d WHERE d.AccountId NOT IN (SELECT AccountNumber FROM Readings WHERE ServicePeriod=d.ServicePeriod) ORDER BY OldAccountNo")
    List<DownloadedPreviousReadings> getAllUnread();

    @Query("SELECT * FROM DownloadedPreviousReadings d WHERE d.AccountId IN (SELECT AccountNumber FROM Readings WHERE ServicePeriod=d.ServicePeriod AND UploadStatus = 'UPLOADABLE') ORDER BY OldAccountNo")
    List<DownloadedPreviousReadings> getAllRead();

    @Query("SELECT * FROM DownloadedPreviousReadings d WHERE (d.ServiceAccountName LIKE :regex OR d.MeterSerial LIKE :regex OR d.OldAccountNo LIKE :regex) AND " +
            " d.AccountId NOT IN (SELECT AccountNumber FROM Readings WHERE ServicePeriod=d.ServicePeriod)" +
            " ORDER BY ServiceAccountName")
    List<DownloadedPreviousReadings> getSearch(String regex);
}
