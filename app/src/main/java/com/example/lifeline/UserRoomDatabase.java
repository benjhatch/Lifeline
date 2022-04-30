package com.example.lifeline;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.*;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Database(entities = {UserTable.class}, version = 1, exportSchema = false)
public abstract class UserRoomDatabase extends RoomDatabase {
    private static volatile UserRoomDatabase mInstance;
    public abstract UserDao UserDao();
    static final ExecutorService databaseExecutor =
            Executors.newFixedThreadPool(4);

    static synchronized UserRoomDatabase getDatabase(final Context context){
        if(mInstance==null) {
            mInstance = Room.databaseBuilder(context.getApplicationContext(),
                    UserRoomDatabase.class, "users.db")
                    .setJournalMode(JournalMode.TRUNCATE)
                    .addCallback(sRoomDatabaseCallback).build();
        }
        return mInstance;
    }
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseExecutor.execute(()->{
                UserDao dao = mInstance.UserDao();
                dao.deleteAll();
            });
        }
    };
}

