package storage

import android.content.Context
import android.database.sqlite.SQLiteDatabase

/**
 *
 * @author Osadchiy Artem osadchiyzp93@gmail.com
 * @since 2018.01.11
 */
object CliksDbManager {

    private lateinit var dataBase: SQLiteDatabase

    public fun initDataBase(context: Context) {
        dataBase = ClicksDbHelper(context).writableDatabase
    }

    public fun insertTimestamp(timestamp: Long) {
        dataBase.beginTransaction()

    }
}