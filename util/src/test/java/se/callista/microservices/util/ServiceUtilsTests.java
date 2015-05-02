package se.callista.microservices.util;

import org.junit.Test;
import org.slf4j.MDC;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ServiceUtilsTests {


    @Test
    public void someTest() {
        assertEquals("ok", "ok");
    }

    @Test
    public void mdcTest() {

        final String KEY = "A";

        try {

            {
                String a0 = MDC.get(KEY);
                assertNull(a0);
            }

            {
                MDC.put(KEY, "1");
                String a1 = MDC.get(KEY);
                assertEquals(a1, "1");
            }

            {
                MDC.remove(KEY);
                String a1_gone = MDC.get(KEY);
                assertNull(a1_gone);
            }

            {
                MDC.put(KEY, "2");
                String a2 = MDC.get(KEY);
                assertEquals(a2, "2");
            }

        } finally {
            MDC.remove(KEY);
            String a2_gone = MDC.get(KEY);
            assertNull(a2_gone);
        }

    }
}
