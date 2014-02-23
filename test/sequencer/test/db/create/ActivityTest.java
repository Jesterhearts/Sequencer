/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequencer.test.db.create;

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
     * Test of activity saving to DB
     */
    @Test
    public void testSaveActivity()
    {
        System.out.println("Save activity");
        
        Activity activity = new Activity(new Integer(0), "activity1", 10, false,
                                          null, null,
                                         new boolean[7], 0,0,0,0);
        if(!MongoHelper.save(activity, MongoHelper.ACTIVITY_COLLECTION))
            TestHelper.failed("Save to DB Failed");
        
        System.out.println("Successfully saved activity:" + activity.getId());
        TestHelper.passed();
    }
    
}