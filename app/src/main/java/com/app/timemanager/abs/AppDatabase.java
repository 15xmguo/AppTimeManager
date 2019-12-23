package com.app.timemanager.abs;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.app.timemanager.dao.RestrictedAppDao;
import com.app.timemanager.entities.RestrictedApp;

@Database(entities = {RestrictedApp.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DB_NAME = "AppDatabase.db";
    private static volatile AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static AppDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                AppDatabase.class,
                DB_NAME).allowMainThreadQueries().build();
    }
    public abstract RestrictedAppDao getRestrictedAppDao();
}
