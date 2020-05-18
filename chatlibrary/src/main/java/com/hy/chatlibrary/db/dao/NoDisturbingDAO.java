package com.hy.chatlibrary.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.hy.chatlibrary.db.entity.NoDisturbing;

/**
 * @author:MtBaby
 * @date:2020/05/10 22:41
 * @desc:免打扰操作
 */
@Dao
public interface NoDisturbingDAO {
    @Insert
    void insertNoDisturbing(NoDisturbing noDisturbing);

    @Update
    void upDateNoDisturbing(NoDisturbing noDisturbing);

    @Query("select * from NoDisturbing where chatGroupHolderID =:chatGroupHolderID and chatGroupId =:chatGroupId")
    NoDisturbing getNoDisturbing(String chatGroupHolderID, String chatGroupId);

}
