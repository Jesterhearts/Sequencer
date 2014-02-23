/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequencer.test.db.update;

import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sequencer.db.Schedule;
import static org.junit.Assert.*;
import sequencer.db.Activity;
import sequencer.db.util.MongoHelper;
import sequencer.test.util.TestHelper;

/**
 *
 * @author Daniel Rogers
 */
public class ScheduleTest {
    
    Integer instanceId;
    
    public ScheduleTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        TestHelper.signon(this);
        
        MongoHelper.setDB("SEQUENCER_TEST_DB");
        MongoHelper.getCollection(MongoHelper.SCHEDULE_COLLECTION).drop();
        MongoHelper.getCollection(MongoHelper.ACTIVITY_COLLECTION).drop();
        
        
        Schedule schedule = new Schedule(new Integer(0), "test schedule", null);
        
        if(!MongoHelper.save(schedule, MongoHelper.SCHEDULE_COLLECTION))
            TestHelper.failed("Save to DB Failed");
        
        instanceId = schedule.getId();
    }
    
    @After
    public void tearDown() {
        TestHelper.signoff(this);
    }

    /**
     * Test of addActivity method, of class Schedule.
     */
    @Test
    public void testAddActivity() {
        System.out.println("addActivity");
        
        Activity activity = new Activity(new Integer(0), "activity1", 10, false,
                                         null, null,
                                         new boolean[7], 0,0,0,0);
        
        MongoHelper.save(activity, MongoHelper.ACTIVITY_COLLECTION);
        Integer id = activity.getId();
        
        Schedule instance = (Schedule) MongoHelper.fetch(
                                                new Schedule(instanceId),
                                                MongoHelper.SCHEDULE_COLLECTION);
        
        instance.addActivity(id);
        if(!MongoHelper.save(instance, MongoHelper.SCHEDULE_COLLECTION))
            TestHelper.failed("Save to DB Failed");
        
        Schedule copy = (Schedule) MongoHelper.fetch(new Schedule(instanceId),
                                                MongoHelper.SCHEDULE_COLLECTION);
        
        TestHelper.asserting(instance.equals(copy));
        TestHelper.asserting(copy.getActivities().contains(id));
        //check that the activity properly registered the schedule
        activity = (Activity) MongoHelper.fetch(new Activity(id),
                                               MongoHelper.ACTIVITY_COLLECTION);
        TestHelper.asserting(activity.getSchedules().contains(instanceId));
        
        System.out.println("Succesfully added activity: " + id + " to"
                            + " schedule: " + instanceId);
        
        TestHelper.passed();
    }

    /**
     * Test of removeActivity method, of class Schedule.
     */
    @Test
    public void testRemoveActivity() {
        System.out.println("removeActivity");
         
        Activity activity = new Activity(new Integer(0), "activity1", 10, false,
                                         null, null,
                                         new boolean[7], 0,0,0,0);
        
        MongoHelper.save(activity, MongoHelper.ACTIVITY_COLLECTION);
        Integer id = activity.getId();
        
        Schedule instance = (Schedule) MongoHelper.fetch(
                                                new Schedule(instanceId),
                                                MongoHelper.SCHEDULE_COLLECTION);
        
        instance.addActivity(id);
        instance.removeActivity(id);
        
        if(!MongoHelper.save(instance, MongoHelper.SCHEDULE_COLLECTION))
            TestHelper.failed("Save to DB Failed");
        
        Schedule copy = (Schedule) MongoHelper.fetch(new Schedule(instanceId),
                                                MongoHelper.SCHEDULE_COLLECTION);
        
        TestHelper.asserting(instance.equals(copy));
        TestHelper.asserting(!copy.getActivities().contains(id));
        //check that the activity properly registered the schedule
        activity = (Activity) MongoHelper.fetch(new Activity(id),
                                               MongoHelper.ACTIVITY_COLLECTION);
        TestHelper.asserting(activity.getSchedules() == null || 
                             !activity.getSchedules().equals(instanceId));
        
        System.out.println("Succesfully removed activity: " + id + " from"
                            + " schedule: " + instanceId);
        
        TestHelper.passed();
    }

    /**
     * Test of equals method, of class Schedule.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Schedule schedule = new Schedule(new Integer(0), "test schedule", null);
        
        TestHelper.asserting(schedule.equals(schedule));
        
        TestHelper.passed();
    }
}