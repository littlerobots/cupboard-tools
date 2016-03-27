package nl.littlerobots.cupboard.tools.moshi;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.convert.FieldConverter;
import nl.qbusict.cupboard.convert.FieldConverterFactory;

public class MoshiListFieldConverterFactory implements FieldConverterFactory {

    private final Moshi mMoshi;

    public MoshiListFieldConverterFactory(Moshi moshi) {
        mMoshi = moshi;
    }

    @Override
    public FieldConverter<?> create(Cupboard cupboard, Type type) {
        if (type == List.class || (type instanceof ParameterizedType
                && ((Class<?>) (((ParameterizedType) type).getRawType())).isAssignableFrom(List.class))) {
            final JsonAdapter<List<?>> adapter = mMoshi.adapter(Types.newParameterizedType(List.class, type));
            return new MoshiFieldConverter<>(adapter);
        }
        return null;
    }
}
