/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequencer.test.db.read;

import sequencer.test.db.delete.*;
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
     * Test reading schedule saved in DB
     */
    @Test
    public void testReadSchedule()
    {
        System.out.println("Read schedule");
        
        Schedule schedule = new Schedule(new Integer(0), "test schedule", null);
        
        if(!MongoHelper.save(schedule, MongoHelper.SCHEDULE_COLLECTION))
            TestHelper.failed("Save to DB Failed");
        
        Schedule read = (Schedule) MongoHelper.fetch(
                                              new Schedule(schedule.getId()), 
                                              MongoHelper.SCHEDULE_COLLECTION);
        
        TestHelper.asserting(read.equals(schedule));
        
        System.out.println("Successfully read schedule:" + schedule.getId());
        TestHelper.passed();
    }
    
}