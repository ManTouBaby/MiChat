package com.hy.chatlibrary.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.hy.chatlibrary.db.dao.ChatMessageDAO;
import com.hy.chatlibrary.db.dao.InstructDAO;
import com.hy.chatlibrary.db.dao.NoDisturbingDAO;

/**
 * @author:MtBaby
 * @date:2020/04/08 12:01
 * @desc:
 */
public class DBHelper {
    private final AppDataBase mDataBase;
    private static DBHelper mDbHelper;

    public static DBHelper getInstance(Context context) {
        if (mDbHelper == null) {
            synchronized (DBHelper.class) {
                if (mDbHelper == null) {
                    mDbHelper = new DBHelper(context);
                }
            }
        }
        return mDbHelper;
    }

    private DBHelper(Context context) {
        Migration migration1_2 = new Migration(1, 2) {
            @Override
            public void migrate(@NonNull SupportSQLiteDatabase database) {
//                database.execSQL("alter table instructModel add duration integer NOT NULL DEFAULT 0");
//                database.execSQL("alter table instructModel add fileSize real  NOT NULL DEFAULT 0");
                database.execSQL("alter table ChatMessage add messageHolders text");
            }
        };
        Migration migration2_3 = new Migration(2, 3) {
            @Override
            public void migrate(@NonNull SupportSQLiteDatabase database) {
//                database.execSQL("alter table ChatMessage add isSynchronization integer NOT NULL DEFAULT 0");

//                database.execSQL("create table NoDisturbing(chatGroupHolderID text ,chatGroupId text ,isOpen integer NOT NUll default 0,primary key(chatGroupHolderID,chatGroupId) )");
                database.execSQL("alter table ChatMessage add messageThumbFilePath text");
                database.execSQL("alter table instructModel add netThumbFilePath text");
//                database.execSQL("alter table ChatMessage add latitude real");
//                database.execSQL("alter table ChatMessage add longitude real");
            }
        };
        mDataBase = Room.databaseBuilder(context, AppDataBase.class, "chatMessage.db")
                .addMigrations(migration1_2,migration2_3)
                .allowMainThreadQueries()
                .build();
    }

    public ChatMessageDAO getChatMessageDAO() {
        return mDataBase.chatMessageDAO();
    }

    public InstructDAO getInstructDAO() {
        return mDataBase.instructDAO();
    }
    public NoDisturbingDAO getNoDisturbingDAO(){
        return mDataBase.noDisturbingDAO();
    }

}
