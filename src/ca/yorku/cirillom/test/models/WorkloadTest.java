package ca.yorku.cirillom.test.models;

import ca.yorku.cirillom.ensemble.models.Workload;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * WorkloadTest.java
 *
 * @author Marco
 * @since 2014-04-08
 */
public class WorkloadTest {

    Workload w;

    @Before
    public void setUp() throws Exception {
        w = new Workload("index", 250);
    }

    @Test
    public void testGetRequestsPerSecond() throws Exception {
        Date d1 = new Date(1396915200); // 4 / 8 / 2014 @ 0:0:0 UTC
        Date d2 = new Date(1396915215); // 4 / 8 / 2014 @ 0:0:15 UTC

        //assertEquals("Requests per second is 250requests/15seconds = 16.6", 250f/15f, w.getRequestsPerSecond(d1, d2), 0.1);
    }
}
