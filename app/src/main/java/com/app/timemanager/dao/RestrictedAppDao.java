package com.app.timemanager.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.app.timemanager.entities.RestrictedApp;

import java.util.List;

@Dao
public interface RestrictedAppDao {
    @Query("SELECT * FROM RestrictedApp")
    List<RestrictedApp> getAllRestrictedApps();
    @Insert
    void insert(RestrictedApp... restrictedApp);
    @Update
    void update(RestrictedApp... restrictedApp);
    @Delete
    void delete(RestrictedApp... restrictedApp);
    @Query("SELECT * FROM RestrictedApp WHERE restrictedAppName=:restrictedAppName")
    RestrictedApp getAppByName(String restrictedAppName);
}
