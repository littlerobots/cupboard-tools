package nl.littlerobots.cupboard.tools;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.test.AndroidTestCase;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import nl.littlerobots.cupboard.tools.convert.ListFieldConverterFactory;
import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.CupboardBuilder;

public class ListFieldConverterFactoryTest extends AndroidTestCase {

    public static class MyTestEntity {
        public Long _id;
        public List<String> strings;
    }

    public void testListFieldConverter() {
        ListFieldConverterFactory factory = new ListFieldConverterFactory(new Gson());
        MyTestEntity entity = new MyTestEntity();
        entity.strings = Arrays.asList("test1", "test2");
        Cupboard cupboard = new CupboardBuilder().registerFieldConverterFactory(factory).build();
        cupboard.register(MyTestEntity.class);
        ContentValues values = cupboard.withEntity(MyTestEntity.class).toContentValues(entity);
        assertEquals("[\"test1\",\"test2\"]", values.getAsString("strings"));
        MatrixCursor cursor = new MatrixCursor(new String[] {"strings"});
        cursor.addRow(Arrays.asList("[\"test3\",\"test4\"]"));
        entity = cupboard.withCursor(cursor).get(MyTestEntity.class);
        assertNotNull(entity.strings);
        assertEquals(2, entity.strings.size());
        assertEquals("test3", entity.strings.get(0));
        assertEquals("test4", entity.strings.get(1));
    }
}
