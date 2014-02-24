package ca.yorku.cirillom.test.models;

import ca.yorku.cirillom.ensemble.models.DataValue;
import ca.yorku.cirillom.ensemble.models.MovingAverageModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * User: Marco
 * Email: cirillom@yorku.ca
 * Date: 2/22/14 4:03 PM.
 * <p/>
 * Description
 * <p/>
 * <p/>
 * <p/>
 * Created with IntelliJ IDEA.
 */
public class MovingAverageModelTest {

    MovingAverageModel model;

    static final List<DataValue> testData = new ArrayList<>();
    static {
        /*for (int i = 1; i <= 10; i++) {
            testData.add(new DataValue(i*10, new Date()));
        }*/
        testData.add(new DataValue(0, new Date()));
        testData.add(new DataValue(1000, new Date()));
        testData.add(new DataValue(2000, new Date()));
        testData.add(new DataValue(0, new Date()));
        testData.add(new DataValue(1000, new Date()));
        testData.add(new DataValue(0, new Date()));
    }

    @Before
    public void setUp() throws Exception {

        model = new MovingAverageModel(MovingAverageModel.DEFAULT_WINDOW);

        /*assertEquals("MovingAverageModel initializes with no data",
                model.getInput().size(), 0);*/

        assertEquals("MovingAverageModel initializes with 1/2 the size of our testData",
                model.getWindow(), MovingAverageModel.DEFAULT_WINDOW);
    }

    @After
    public void tearDown() throws Exception { }

    @Test
    public void testAddInput() {
        for (DataValue value : testData) {

            /*int oldSize = model.getInput().size();
            model.addInput(value);
            int newSize = model.getInput().size();*/

            assertEquals("The last added DataValue is the one we just inserted",
                    model.getLastInput(), value);

            /*assertTrue("Window is maintained",
                    model.getWindow() >= newSize);

            assertTrue("Data size has grown by one, or has hit window",
                    newSize == Math.min(oldSize + 1, model.getWindow()));*/
        }
    }

    private double computeAverage(List<DataValue> data) {
        double sum = 0;

        for (DataValue v : data) {
            sum += v.getValue();
        }

        return sum/data.size();
    }

    @Test
    public void testModel() throws Exception {

        for (DataValue aTestData : testData) {

            // Add to model
            model.addInput(aTestData);

            double modelResult  = model.model();

            // Convert the Deque to an array and then a List
           /* double oracleResult = computeAverage(dequeToList(model.getInput()));

            assertEquals("Model's Result is consistent with the Oracle's result within 1",
                    modelResult, oracleResult, 1);*/
        }
    }

    @Test
    public void testGetError() throws Exception {

        boolean firstRun = true;

        for (DataValue aTestData : testData) {

            // Add to model
            model.addInput(aTestData);

            // Run Model
            double result = model.model();

            double accuracy = Math.abs(aTestData.getValue() - model.getLastPrediction())/aTestData.getValue();
            if(Double.isNaN(accuracy)) accuracy = 0;
            if(Double.isInfinite(accuracy)) accuracy = 0;

           // printList(Arrays.asList(model.getInput().toArray(new DataValue[1])));

            System.out.print(model.getLastInput().getValue() + " ");
            System.out.println(model.getLastPrediction());

            assertEquals("Accuracy is the same as our Oracle's computed accuracy",
                    model.getError(), accuracy, 0.1);
        }
    }

    private List<DataValue> dequeToList(ArrayDeque<DataValue> d) {
        return Arrays.asList(d.toArray(new DataValue[1]));
    }

    private void printList(List<DataValue> list) {
        for (DataValue val : list) {
            System.out.print(val.getValue() + " ");
        }

        System.out.println();
    }
}
