package com.example.mvvmforjava.model.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.mvvmforjava.App;

@Database(entities = {Theater.class}, version = 1, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase {
    private static MyDatabase instance;

    public static MyDatabase getInstance(){
        if (instance == null){
            synchronized (MyDatabase.class){
                if (instance == null) instance = buildDatabase();
            }
        }
        return instance;
    }

    private static MyDatabase buildDatabase() {
        return Room.databaseBuilder(App.instance, MyDatabase.class, "test.db").build();
    }

    //
    public abstract TheaterDao theaterDao();
}
