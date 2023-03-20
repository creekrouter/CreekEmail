package com.creek.database.helper;

import android.database.Cursor;


public class MailCursor {

    private final static String DEFAULT_STR = "";
    private final static int DEFAULT_INT = 0;
    private final static long DEFAULT_LONG = 0l;

    private Cursor cursor;

    public MailCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public String getString(String column) {
        if (cursor == null) {
            return DEFAULT_STR;
        }
        int index = cursor.getColumnIndex(column);
        if (index < 0) {
            return DEFAULT_STR;
        }
        return cursor.getString(index);
    }

    public int getInt(String column) {
        if (cursor == null) {
            return DEFAULT_INT;
        }
        int index = cursor.getColumnIndex(column);
        if (index < 0) {
            return DEFAULT_INT;
        }
        return cursor.getInt(index);
    }

    public long getLong(String column) {
        if (cursor == null) {
            return DEFAULT_LONG;
        }
        int index = cursor.getColumnIndex(column);
        if (index < 0) {
            return DEFAULT_LONG;
        }
        return cursor.getLong(index);
    }


    public boolean moveToNext() {
        if (cursor == null) {
            return false;
        }
        return cursor.moveToNext();
    }

}
