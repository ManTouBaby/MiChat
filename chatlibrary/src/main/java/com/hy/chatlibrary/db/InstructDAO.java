package com.hy.chatlibrary.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * @author:MtBaby
 * @date:2020/04/29 17:56
 * @desc:
 */
@Dao
public interface InstructDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertChatMessage(InstructBean instructBean);

    @Query("Select * from instructModel order by createMillis desc")
    List<InstructBean> queryAllInstruct();

    @Update
    void updateInstruct(InstructBean instructBean);

    @Delete()
    void deleteInstruct(InstructBean instructBean);
}
