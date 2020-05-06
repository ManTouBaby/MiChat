package com.hy.chatlibrary.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * @author:MtBaby
 * @date:2020/04/08 12:02
 * @desc:
 */
@Database(entities = {ChatMessage.class, InstructBean.class}, version = 2, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {
    public abstract ChatMessageDAO chatMessageDAO();

    public abstract InstructDAO instructDAO();
}
