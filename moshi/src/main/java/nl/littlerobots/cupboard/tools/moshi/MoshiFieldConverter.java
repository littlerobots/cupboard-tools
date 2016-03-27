package nl.littlerobots.cupboard.tools.moshi;

import com.squareup.moshi.JsonAdapter;

import android.content.ContentValues;
import android.database.Cursor;

import java.io.IOException;

import nl.qbusict.cupboard.convert.EntityConverter;
import nl.qbusict.cupboard.convert.FieldConverter;

public class MoshiFieldConverter<T> implements FieldConverter<T> {

    private final JsonAdapter<T> mJsonAdapter;

    public MoshiFieldConverter(JsonAdapter<T> adapter) {
        mJsonAdapter = adapter;
    }

    @Override
    public T fromCursorValue(Cursor cursor, int columnIndex) {
        try {
            return mJsonAdapter.fromJson(cursor.getString(columnIndex));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void toContentValue(T value, String key, ContentValues values) {
        values.put(key, mJsonAdapter.toJson(value));
    }

    @Override
    public EntityConverter.ColumnType getColumnType() {
        return EntityConverter.ColumnType.TEXT;
    }
}
