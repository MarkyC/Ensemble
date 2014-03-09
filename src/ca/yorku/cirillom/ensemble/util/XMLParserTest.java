package ca.yorku.cirillom.ensemble.util;

import ca.yorku.cirillom.ensemble.models.DataValue;
import ca.yorku.cirillom.ensemble.models.PerformanceData;
import org.junit.Before;
import org.junit.Test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Iterator;

/**
 * User: Marco
 * Email: cirillom@yorku.ca
 * Date: 3/7/14 6:22 PM.
 * <p/>
 * Description
 * <p/>
 * <p/>
 * <p/>
 * Created with IntelliJ IDEA.
 */
public class XMLParserTest {

    XMLParser parser;

    @Before
    public void setUp() throws Exception {
        parser = new XMLParser(new File("C:\\Users\\Marco\\Desktop\\performance logs\\data.xml"));
    }

    @Test
    public void testDoInBackground() throws Exception {
        PerformanceData d = parser.doInBackground();
        for ( Iterator<DataValue> it = d.getDataValues().iterator(); it.hasNext(); ) {
            DataValue v = it.next();
            System.out.println(v);
        }
    }

    @Test
    public void testProcess() throws InterruptedException {

        // Doesn't work because no EDT?
        // Maybe needs a Swing Window to be instantiated?

        parser.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                System.out.println(evt.getPropertyName() + ": " + evt.getNewValue());
            }
        });

        parser.execute();
    }
}
