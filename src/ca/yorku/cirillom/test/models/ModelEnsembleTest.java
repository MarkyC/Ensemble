package ca.yorku.cirillom.test.models;

import ca.yorku.cirillom.ensemble.models.DataValue;
import ca.yorku.cirillom.ensemble.models.IEnsembleModel;
import ca.yorku.cirillom.ensemble.models.ModelEnsemble;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    ModelEnsemble model;

    static final List<DataValue> testData = new ArrayList<>();
    static {
        for (int i = 1; i <= 20; i++) {
            testData.add(new DataValue(i*10, new Date()));
        }
    }

    @Before
    public void setUp() throws Exception {

        model = new ModelEnsemble(testData);

    }

    @After
    public void tearDown() throws Exception { }

    @Test
    public void testStart() throws Exception {
        model.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                IEnsembleModel model = (IEnsembleModel) evt.getNewValue();
                System.out.println(model.getLastInput().getValue() +" "+ model.getLastPrediction() +" "+
                        model.getError());
            }
        });

        model.start();
    }

}
