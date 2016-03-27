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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.convert.EntityConverter;
import nl.qbusict.cupboard.convert.FieldConverter;
import nl.qbusict.cupboard.convert.FieldConverterFactory;

/**
 * FieldConverter that uses Gson to serialize and deserialize {@link java.util.List}s
 */
public class GsonListFieldConverterFactory implements FieldConverterFactory {

    private final Gson mGson;

    public GsonListFieldConverterFactory(Gson gson) {
        this.mGson = gson;
    }

    @Override
    public FieldConverter<?> create(Cupboard cupboard, final Type type) {
        if (type == List.class || (type instanceof ParameterizedType
                && ((Class<?>) (((ParameterizedType) type).getRawType())).isAssignableFrom(List.class))) {
            return new FieldConverter<List<?>>() {
                @Override
                public List<?> fromCursorValue(Cursor cursor, int columnIndex) {
                    String json = cursor.getString(columnIndex);
                    return mGson.fromJson(json, type);
                }

                @Override
                public void toContentValue(List<?> value, String key, ContentValues values) {
                    values.put(key, mGson.toJson(value));
                }

                @Override
                public EntityConverter.ColumnType getColumnType() {
                    return EntityConverter.ColumnType.TEXT;
                }
            };
        }
        return null;
    }
}
