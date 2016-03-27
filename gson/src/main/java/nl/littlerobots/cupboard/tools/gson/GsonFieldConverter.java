/*
 *    Copyright 2015 Little Robots
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package nl.littlerobots.cupboard.tools.gson;

import com.google.gson.Gson;

import android.content.ContentValues;
import android.database.Cursor;

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
