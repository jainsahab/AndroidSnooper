package com.prateekj.snooper.database


import android.database.Cursor

interface CursorParser<out T> {
  fun parse(cursor: Cursor): T
}
