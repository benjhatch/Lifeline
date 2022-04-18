package com.example.lifeline;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {WeatherTable.class}, version = 1, exportSchema = false)
public abstract class WeatherRoomDatabase extends RoomDatabase {
    private static volatile WeatherRoomDatabase mInstance;
    public abstract WeatherDao WeatherDao();
    static final ExecutorService databaseExecutor =
            Executors.newFixedThreadPool(4);

    static synchronized WeatherRoomDatabase getDatabase(final Context context){
        if(mInstance==null) {
            mInstance = Room.databaseBuilder(context.getApplicationContext(),
                    WeatherRoomDatabase.class, "weather.db").addCallback(sRoomDatabaseCallback).build();
        }
        return mInstance;
    }
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseExecutor.execute(()->{
                WeatherDao dao = mInstance.WeatherDao();
                dao.deleteAll();
            });
        }
    };
}
