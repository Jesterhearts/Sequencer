/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequencer.test.db.update;

import java.util.Arrays;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sequencer.db.Activity;
import static org.junit.Assert.*;
import sequencer.db.Group;
import sequencer.db.util.MongoHelper;
import sequencer.test.util.TestHelper;

/**
 *
 * @author Daniel Rogers
 */
public class ActivityTest {
    private Integer instanceId;
    
    public ActivityTest() {
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
        MongoHelper.getCollection(MongoHelper.ACTIVITY_COLLECTION).drop();
        MongoHelper.getCollection(MongoHelper.GROUP_COLLECTION).drop();
        
        Activity instance = new Activity(new Integer(0), "activity1", 10, false,
                                         null, null,
                                         new boolean[7], 0,0,0,0);
        
        MongoHelper.save(instance, MongoHelper.ACTIVITY_COLLECTION);
        instanceId = instance.getId();
    }
    
    @After
    public void tearDown() {
        TestHelper.signoff(this);
    }

    /**
     * Test of setGroup method, of class Activity.
     */
    @Test
    public void testSetGroup() {
        System.out.println("Set Group");
        
        //make group for activity to update
        Group f = new Group(new Integer(0), "group1", 10, null);
        
        if(!MongoHelper.save(f, MongoHelper.GROUP_COLLECTION))
            TestHelper.failed("Save to DB failed");
        
        Integer groupId = f.getId();
        
        //read in the stored activity
        Activity instance = (Activity) MongoHelper.fetch(
                                               new Activity(instanceId),
                                               MongoHelper.ACTIVITY_COLLECTION);
        if(instance == null)
            TestHelper.failed("Activity: " + instanceId + " not found" );
        //update the group
        instance.setGroup(groupId);
        
        //put back in db and re-read
        if(!MongoHelper.save(instance, MongoHelper.ACTIVITY_COLLECTION))
            TestHelper.failed("Save to DB failed");
        
        Activity copy = (Activity) MongoHelper.fetch(
                                               new Activity(instanceId),
                                               MongoHelper.ACTIVITY_COLLECTION);
        
        //check that update was written correctly
        TestHelper.asserting(instance.equals(copy));
        f = (Group)MongoHelper.fetch(new Group(groupId), 
                                      MongoHelper.GROUP_COLLECTION);
        
        TestHelper.asserting(f.getActivities().contains(instanceId));
        
        System.out.println("Succesfully updated activity: " + instanceId +
                           "'s group to" + " group: " + groupId);
        TestHelper.passed();
    }

    /**
     * Test of setSchedule method, of class Activity.
     */
    @Test
    public void testSetSchedule() {
        System.out.println("setSchedule");
        Integer scheduleId = new Integer(0);
        
        //read in the stored activity
        Activity instance = (Activity) MongoHelper.fetch(
                                               new Activity(instanceId),
                                               MongoHelper.ACTIVITY_COLLECTION);
        if(instance == null)
            TestHelper.failed("Activity: " + instanceId + " not found" );
        //update the group
        instance.addSchedule(scheduleId);
        
        //put back in db and re-read
        if(!MongoHelper.save(instance, MongoHelper.ACTIVITY_COLLECTION))
            TestHelper.failed("Save to DB failed");
        Activity copy = (Activity) MongoHelper.fetch(
                                               new Activity(instanceId),
                                               MongoHelper.ACTIVITY_COLLECTION);
        
        //check that update was written correctly
        TestHelper.asserting(instance.equals(copy));
        
        System.out.println("Succesfully updated activity: " + instanceId + "'s schedule"
                            + " to schedule: " + scheduleId);
        TestHelper.passed();
    }

    /**
     * Test of updateDays method, of class Activity.
     */
    @Test
    public void testUpdateDays() {
        System.out.println("updateDays");
        boolean[] days = {false, true, false, true, false, false, false};
        
        //read in the stored activity
        Activity instance = (Activity) MongoHelper.fetch(
                                               new Activity(instanceId),
                                               MongoHelper.ACTIVITY_COLLECTION);
        if(instance == null)
            TestHelper.failed("Activity: " + instanceId + " not found" );
        //update the group
        instance.updateDays(days);
        
        //put back in db and re-read
        if(!MongoHelper.save(instance, MongoHelper.ACTIVITY_COLLECTION))
            TestHelper.failed("Save to DB failed");
        Activity copy = (Activity) MongoHelper.fetch(
                                               new Activity(instanceId),
                                               MongoHelper.ACTIVITY_COLLECTION);
        
        //check that update was written correctly
        TestHelper.asserting(instance.equals(copy));
        TestHelper.asserting(Arrays.equals(copy.getDays(), days));
        
        System.out.println("Succesfully updated activity: " + instanceId + "'s days"
                         + " to days: " + Arrays.toString(instance.getDays()));
        TestHelper.passed();
    }

    /**
     * Test of updateStartTime method, of class Activity.
     */
    @Test
    public void testUpdateStartTime() {
        System.out.println("updateStartTime");
        int hr = 1;
        int min = 1;
        
        //read in the stored activity
        Activity instance = (Activity) MongoHelper.fetch(
                                               new Activity(instanceId),
                                               MongoHelper.ACTIVITY_COLLECTION);
        if(instance == null)
            TestHelper.failed("Activity: " + instanceId + " not found" );
        //update the group
        instance.setStartTime(hr, min);
        
        //put back in db and re-read
        if(!MongoHelper.save(instance, MongoHelper.ACTIVITY_COLLECTION))
            TestHelper.failed("Save to DB failed");
        Activity copy = (Activity) MongoHelper.fetch(
                                               new Activity(instanceId),
                                               MongoHelper.ACTIVITY_COLLECTION);
        
        //check that update was written correctly
        System.out.println("i: " + instance.toString());
        System.out.println("C: " + copy.toString());
        TestHelper.asserting(instance.equals(copy));
        
        System.out.println("Succesfully updated activity: " + instanceId + "'s start"
                  + " to start time: " + Arrays.toString(copy.getTimeStart()));
        TestHelper.passed();
    }

    /**
     * Test of updateEndTime method, of class Activity.
     */
    @Test
    public void testUpdateEndTime() {
        System.out.println("updateEndTime");
        int hr = 0;
        int min = 0;
        
        //read in the stored activity
        Activity instance = (Activity) MongoHelper.fetch(
                                               new Activity(instanceId),
                                               MongoHelper.ACTIVITY_COLLECTION);
        if(instance == null)
            TestHelper.failed("Activity: " + instanceId + " not found" );
        //update the group
        instance.setEndTime(hr, min);
        
        //put back in db and re-read
        if(!MongoHelper.save(instance, MongoHelper.ACTIVITY_COLLECTION))
            TestHelper.failed("Save to DB failed");
        Activity copy = (Activity) MongoHelper.fetch(
                                               new Activity(instanceId),
                                               MongoHelper.ACTIVITY_COLLECTION);
        
        //check that update was written correctly
        System.out.println("i: " + instance.toString());
        System.out.println("C: " + copy.toString());
        TestHelper.asserting(instance.equals(copy));
        
        System.out.println("Succesfully updated activity: " + instanceId + "'s start"
                  + " to start time: " + Arrays.toString(copy.getTimeStart()));
        TestHelper.passed();
    }
    
   /**
     * Test of equals method, of class Schedule.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Activity instance = new Activity(new Integer(0), "activity1", 10, false,
                                         null, null,
                                         new boolean[7], 0,0,0,0);
        
        TestHelper.asserting(instance.equals(instance));
        
        TestHelper.passed();
    }

}