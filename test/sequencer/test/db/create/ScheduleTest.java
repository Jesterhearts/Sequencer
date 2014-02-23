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
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import sequencer.db.Schedule;
import sequencer.db.util.MongoHelper;
import sequencer.test.util.TestHelper;

/**
 *
 * @author Daniel Rogers
 */
public class ScheduleTest {

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
        MongoHelper.getCollection(MongoHelper.SCHEDULE_COLLECTION).drop();
    }

    @After
    public void tearDown() throws Exception {
        TestHelper.signoff(this);
    }
    
    /**
     * Test of schedule saving to DB
     */
    @Test
    public void testSaveSchedule()
    {
        System.out.println("Save schedule");
        
        Schedule sched = new Schedule(new Integer(0), "test_sched", null);
        if(!MongoHelper.save(sched, MongoHelper.SCHEDULE_COLLECTION))
            TestHelper.failed("Save to DB Failed");
        
        System.out.println("Successfully saved schedule:" + sched.getId());
        TestHelper.passed();
    }
    
}