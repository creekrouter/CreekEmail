package com.creek.database.helper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.creek.common.CreekPath;
import com.creek.database.constants.SQLiteConstant;
import com.creek.database.init.GlobalVar;
import com.creek.router.annotation.CreekMethod;


public class SQLiteHelper extends SQLiteOpenHelper {

    private static String DBName = null;
    private static SQLiteHelper dbHelper = null;


    private SQLiteDatabase db;

    private SQLiteHelper(String dbName) {
        super(GlobalVar.getAppContext(), dbName, null, SQLiteConstant.MAIL_DB_VERSION);
        /*
        there is no need to use WritableDatabase
        */
        db = getReadableDatabase();
    }


    @CreekMethod(path = CreekPath.Mail_BroadCast_User_Init)
    public static void init(String userEmail) {
        if (DBName == null) {
            DBName = userEmail;
            dbHelper = new SQLiteHelper(DBName);
        } else {
            if (!DBName.equals(userEmail)) {
                DBName = userEmail;
                dbHelper = new SQLiteHelper(DBName);
            }
        }
    }

    public static SQLiteHelper getInstance() {
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //db_version=1
        sqLiteDatabase.execSQL(SQLiteConstant.CREATE_TABLE_MAIL_INFO);
        sqLiteDatabase.execSQL(SQLiteConstant.CREATE_TABLE_MAIL_FOLDER);
        sqLiteDatabase.execSQL(SQLiteConstant.CREATE_TABLE_MAIL_ATTACHMENT);
        sqLiteDatabase.execSQL(SQLiteConstant.CREATE_TABLE_MAIL_COMMON_CONTACTS);
        //db_version=2
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void execSQL(String sql) {
        db.execSQL(sql);
    }

    public Cursor rawQuery(String sql, String[] selectionArgs) {
        return db.rawQuery(sql, selectionArgs);
    }

    public long insert(String table, String nullColumnHack, ContentValues values) {
        return db.insert(table, nullColumnHack, values);
    }

    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        return db.update(table, values, whereClause, whereArgs);
    }

    public void beginTransaction() {
        db.beginTransaction();
    }

    public long replace(String table, String nullColumnHack, ContentValues initialValues) {
        return db.replace(table, nullColumnHack, initialValues);
    }

    public void setTransactionSuccessful() {
        db.setTransactionSuccessful();
    }

    public void endTransaction() {
        db.endTransaction();
    }

    public static boolean exe(SqlCallBack callBack) {
        SQLiteHelper db = SQLiteHelper.getInstance();
        db.beginTransaction();  //开启事务
        try {
            Cursor c = callBack.crud(db);
            //关闭游标
            if (null != c) {
                c.close();
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            return false;
        } finally {
            db.endTransaction();
        }
        return true;
    }
}
