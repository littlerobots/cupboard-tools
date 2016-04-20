package nl.littlerobots.cupboard.tools.sqlcipher;

import android.content.ContentValues;
import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import nl.qbusict.cupboard.CupboardDatabase;

public class SQLCipherCupboardDatabase implements CupboardDatabase {
    private final SQLiteDatabase mDatabase;

    public SQLCipherCupboardDatabase(SQLiteDatabase mDatabase) {
        this.mDatabase = mDatabase;
    }

    @Override
    public long insertOrThrow(String table, String nullColumnHack, ContentValues values) {
        return mDatabase.insertOrThrow(table, nullColumnHack, values);
    }

    @Override
    public long replaceOrThrow(String table, String nullColumnHack, ContentValues values) {
        return mDatabase.replaceOrThrow(table, nullColumnHack, values);
    }

    @Override
    public int update(String table, ContentValues values, String selection, String[] selectionArgs) {
        return mDatabase.update(table, values, selection, selectionArgs);
    }

    @Override
    public Cursor query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        return mDatabase.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    @Override
    public Cursor rawQuery(String sql, String[] selectionArgs) {
        return mDatabase.rawQuery(sql, selectionArgs);
    }

    @Override
    public int delete(String table, String selection, String[] selectionArgs) {
        return mDatabase.delete(table, selection, selectionArgs);
    }

    @Override
    public boolean inTransaction() {
        return mDatabase.inTransaction();
    }

    @Override
    public void beginTransaction() {
        mDatabase.beginTransaction();
    }

    @Override
    public void yieldIfContendedSafely() {
        mDatabase.yieldIfContendedSafely();
    }

    @Override
    public void setTransactionSuccessful() {
        mDatabase.setTransactionSuccessful();
    }

    @Override
    public void endTransaction() {
        mDatabase.endTransaction();
    }

    @Override
    public void execSQL(String sql) {
        mDatabase.execSQL(sql);
    }
}
