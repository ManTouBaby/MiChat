package com.hy.chatlibrary.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.hy.chatlibrary.db.dao.ChatMessageDAO;
import com.hy.chatlibrary.db.dao.InstructDAO;
import com.hy.chatlibrary.db.dao.NoDisturbingDAO;
import com.hy.chatlibrary.db.entity.ChatMessage;
import com.hy.chatlibrary.db.entity.InstructBean;
import com.hy.chatlibrary.db.entity.NoDisturbing;

/**
 * @author:MtBaby
 * @date:2020/04/08 12:02
 * @desc:
 */
@Database(entities = {ChatMessage.class, InstructBean.class, NoDisturbing.class}, version = 3, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {
    public abstract ChatMessageDAO chatMessageDAO();

    public abstract InstructDAO instructDAO();

    public abstract NoDisturbingDAO noDisturbingDAO();
}
