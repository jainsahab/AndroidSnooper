package com.prateekj.snooper.database;


import android.database.Cursor;

public interface CursorParser<T> {
  T parse(Cursor cursor);
}
