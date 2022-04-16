package com.example.lifeline;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.os.HandlerCompat;
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
                    UserRoomDatabase.class, "users.db").addCallback(sRoomDatabaseCallback).build();
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
                UserTable userTable = new UserTable("william", "male", 2000,8,16,"sandy", "USA", 60, 150 ,
                        null);
                dao.insert(userTable);
            });
        }
    };

    private static RoomDatabase.Callback sRoomDatabaseCallback2 = new RoomDatabase.Callback(){
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbTask(mInstance).execute();
        }
    };
    private static class PopulateDbTask{
        private final UserDao mDao;

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());
        PopulateDbTask(UserRoomDatabase db){
            mDao = db.UserDao();
        }

        public void execute(){
            executorService.execute(new Runnable(){
                @Override
                public void run(){
                    mDao.deleteAll();
                    UserTable userTable = new UserTable("william", "male", 2000,8,16,"sandy", "USA", 60, 150 ,
                            null);
                    mDao.insert(userTable);
                }
            });
        }
    }
}

