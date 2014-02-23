/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequencer.test.db.delete;

import sequencer.test.db.create.*;
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
     * Test of schedule deleting from DB
     */
    @Test
    public void testDeleteSchedule()
    {
        System.out.println("Delete schedule");
        
        Schedule schedule = new Schedule(new Integer(0), "test schedule", null);
        
        if(!MongoHelper.save(schedule, MongoHelper.SCHEDULE_COLLECTION))
            TestHelper.failed("Save to DB Failed");
        
        if(!MongoHelper.delete(schedule, MongoHelper.SCHEDULE_COLLECTION))
            TestHelper.failed("Delete Schedule failed id:" + schedule.getId());
        
        System.out.println("Successfully deleted schedule:" + schedule.getId());
        TestHelper.passed();
    }
    
}