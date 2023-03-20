package com.creek.database.helper;

import android.database.Cursor;

public interface SqlCallBack {
    Cursor crud(SQLiteHelper db);
}
