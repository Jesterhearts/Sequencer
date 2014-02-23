/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequencer.test.db.read;

import java.util.Arrays;
import sequencer.test.db.delete.*;
import sequencer.test.db.create.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sequencer.db.Activity;
import sequencer.db.util.MongoHelper;
import sequencer.test.util.TestHelper;

/**
 *
 * @author Daniel Rogers
 */
public class ActivityTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        TestHelper.signon(this);
        
        MongoHelper.setDB("SEQUENCER_TEST_DB");
        MongoHelper.getCollection(MongoHelper.ACTIVITY_COLLECTION).drop();
    }

    @After
    public void tearDown() throws Exception {
        TestHelper.signoff(this);
    }
    
    /**
     * Test reading activity saved in DB
     */
    @Test
    public void testReadActivity()
    {
        System.out.println("Read activity");
        
        Activity activity = new Activity(new Integer(0), "activity1", 10, false,
                                          null, null,
                                         new boolean[7], 0,0,0,0);
        
        if(!MongoHelper.save(activity, MongoHelper.ACTIVITY_COLLECTION))
            TestHelper.failed("Save to DB Failed");
        
        Activity read = (Activity) MongoHelper.fetch(
                                              new Activity(activity.getId()), 
                                              MongoHelper.ACTIVITY_COLLECTION);
        
        System.out.println("live: " + activity.toString());
        System.out.println("Store: " + read.toString());
        TestHelper.asserting(read.equals(activity));
        
        System.out.println("Successfully read activity:" + activity.getId());
        TestHelper.passed();
    }
    
}