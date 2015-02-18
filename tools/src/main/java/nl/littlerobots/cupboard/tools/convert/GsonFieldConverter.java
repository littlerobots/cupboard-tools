package nl.littlerobots.cupboard.tools.convert;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;

import nl.qbusict.cupboard.convert.EntityConverter;
import nl.qbusict.cupboard.convert.FieldConverter;

/**
 * FieldConverter that uses Gson to serialize and deserialize fields
 *
 * @param <T> the field type
 */
public class GsonFieldConverter<T> implements FieldConverter<T> {

    private final Class<T> mType;
    private final Gson mGson;

    public GsonFieldConverter(Gson gson, Class<T> type) {
        this.mGson = gson;
        this.mType = type;
    }

    @Override
    public T fromCursorValue(Cursor cursor, int columnIndex) {
        return mGson.fromJson(cursor.getString(columnIndex), mType);
    }

    @Override
    public void toContentValue(T value, String key, ContentValues values) {
        values.put(key, mGson.toJson(value));
    }

    @Override
    public EntityConverter.ColumnType getColumnType() {
        return EntityConverter.ColumnType.TEXT;
    }
}
