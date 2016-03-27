package nl.littlerobots.cupboard.tools.moshi;

import com.squareup.moshi.Moshi;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import android.content.ContentValues;
import android.database.Cursor;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MoshiFieldConverterTest {

    @Test
    public void testConvertFromCursor() {
        MoshiFieldConverter<TestObject> converter = new MoshiFieldConverter<>(new Moshi.Builder().build().adapter(TestObject.class));
        Cursor cursor = mock(Cursor.class);
        when(cursor.getString(0)).thenReturn("{ \"dummy\" : \"test\" }");
        TestObject object = converter.fromCursorValue(cursor, 0);
        assertNotNull(object);
        assertEquals("test", object.dummy);
    }

    @Test
    public void testConverterToValues() {
        MoshiFieldConverter<TestObject> converter = new MoshiFieldConverter<>(new Moshi.Builder().build().adapter(TestObject.class));
        ContentValues values = mock(ContentValues.class);
        TestObject testObject = new TestObject();
        testObject.dummy = "test";
        converter.toContentValue(testObject, "testkey", values);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(values).put(eq("testkey"), captor.capture());
        assertEquals("{\"dummy\":\"test\"}", captor.getValue());
    }

    private static class TestObject {

        public String dummy;
    }
}
