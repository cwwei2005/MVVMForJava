package com.example.mvvmforjava.model.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TheaterDao {
    @Insert
    void insert(Theater theater);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void replaceInsert(Theater theater);

    @Insert
    void insertAll(List<Theater> theaters);

    @Update
    void update(Theater theater);

    @Update
    void updateAll(List<Theater> theaters);

    @Delete
    void delete(Theater theater);

    @Delete
    void deleteAll(List<Theater> theaters);

    @Query("select * from theater")  //查询所有
    Theater getSingle();

    @Query("select * from theater")  //查询所有
    LiveData<Theater> getSingle2();

//    @Query("select * from theater")  //查询所有
//    List<Theater> getAll2();

//    @Query("select * from theater")  //查询所有
//    LiveData<List<Theater>> getAll();
}
