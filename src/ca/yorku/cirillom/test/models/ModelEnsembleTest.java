package ca.yorku.cirillom.test.models;

import ca.yorku.cirillom.ensemble.models.DataValue;
import ca.yorku.cirillom.ensemble.models.MovingAverageModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: Marco
 * Email: cirillom@yorku.ca
 * Date: 2/22/14 7:20 PM.
 * <p/>
 * Description
 * <p/>
 * <p/>
 * <p/>
 * Created with IntelliJ IDEA.
 */
public class ModelEnsembleTest {

    MovingAverageModel model;

    static final List<DataValue> testData = new ArrayList<>();
    static {
        for (int i = 1; i <= 10; i++) {
            testData.add(new DataValue(i*10, new Date()));
        }
    }

    @Before
    public void setUp() throws Exception {

        model = new MovingAverageModel(testData.size()/2);

        assertEquals("MovingAverageModel initializes with no data",
                model.getInput().size(), 0);

        assertEquals("MovingAverageModel initializes with 1/2 the size of our testData",
                model.getWindow(), testData.size()/2);
    }

    @After
    public void tearDown() throws Exception { }

    @Test
    public void testStart() throws Exception {
        // TODO: Tomorrow :(
    }
}
