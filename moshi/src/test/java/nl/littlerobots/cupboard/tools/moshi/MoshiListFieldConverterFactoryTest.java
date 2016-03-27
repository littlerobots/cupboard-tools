package nl.littlerobots.cupboard.tools.moshi;

import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertNotNull;

public class MoshiListFieldConverterFactoryTest {

    @Test
    public void testReturnsConverter() {
        MoshiListFieldConverterFactory fieldConverterFactory = new MoshiListFieldConverterFactory(new Moshi.Builder().build());
        assertNotNull(fieldConverterFactory.create(null, Types.newParameterizedType(List.class, String.class)));
    }
}
