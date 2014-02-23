/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequencer.db.util;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import sequencer.db.Activity;
import sequencer.db.Group;
import sequencer.db.Schedule;
import sequencer.db.User;

/**
 *
 * @author Daniel Rogers
 */
public class DataManagerTest {

    private static DataManager instance;

    public DataManagerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        User u = new User("TEST_MANAGER");
        MongoHelper.setDB("MANAGER_TEST");

        MongoHelper.getCollection(MongoHelper.USER_COLLECTION).drop();
        MongoHelper.getCollection(MongoHelper.ACTIVITY_COLLECTION).drop();
        MongoHelper.getCollection(MongoHelper.GROUP_COLLECTION).drop();
        MongoHelper.getCollection(MongoHelper.SCHEDULE_COLLECTION).drop();

        MongoHelper.save(u, MongoHelper.USER_COLLECTION);

        instance = new DataManager(u.getId());
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getGroup method, of class DataManager.
     */
    @Test
    public void testGetGroup() {
        System.out.println("getGroup");
        String name = "testGetGroup";

        Group expResult = new Group(instance.getUId(), name,
                0, null);

        instance.updateGroup(expResult);

        Group result = instance.getGroup(name);
        assertEquals(expResult, result);
    }

    /**
     * Test of getGroupById method, of class DataManager.
     */
    @Test
    public void testGetGroupById() {
        System.out.println("getGroupById");
        Group expResult = new Group(instance.getUId(), "testGetGroupById",
                0, null);
        instance.updateGroup(expResult);

        Integer id = expResult.getId();

        Group result = instance.getGroupById(id);
        assertEquals(expResult, result);
    }

    /**
     * Test of removeGroup method, of class DataManager.
     */
    @Test
    public void testRemoveGroup() {
        System.out.println("removeGroup");
        Group f = new Group(instance.getUId(), "testRemoveGroup",
                0, null);
        instance.updateGroup(f);
        instance.removeGroup(f);
        Group result = instance.getGroup(f.getName());
        
        assertEquals(null, result);
    }

    /**
     * Test of getActivity method, of class DataManager.
     */
    @Test
    public void testGetActivity() {
        System.out.println("getActivity");
        String name = "testGetActivity";

        Activity expResult = new Activity(instance.getUId(), name,
                0, false, null, null, null,
                0, 0, 0, 0);

        instance.updateActivity(expResult);

        Activity result = instance.getActivity(name);
        assertEquals(expResult, result);
    }

    /**
     * Test of getActivityById method, of class DataManager.
     */
    @Test
    public void testGetActivityById() {
        System.out.println("getActivityById");
        Integer id = null;
        Activity expResult = new Activity(instance.getUId(),
                "testGetActivityById",
                0, false, null, null, null,
                0, 0, 0, 0);
        instance.updateActivity(expResult);
        id = expResult.getId();

        Activity result = instance.getActivityById(id);
        assertEquals(expResult, result);
    }

    /**
     * Test of getSchedule method, of class DataManager.
     */
    @Test
    public void testGetSchedule() {
        System.out.println("getSchedule");

        Schedule expResult = new Schedule(instance.getUId(), "testGetSchedule",
                null);
        instance.updateSchedule(expResult);

        Schedule result = instance.getSchedule();

        assertEquals(expResult, result);
    }

    /**
     * Test of removeActivity method, of class DataManager.
     */
    @Test
    public void testRemoveActivity() {
        System.out.println("removeActivity");
        String name = "testRemoveActivity";

        Activity a = new Activity(instance.getUId(), name,
                0, false, null, null, null,
                0, 0, 0, 0);

        instance.updateActivity(a);
        instance.removeActivity(a);

        Activity result = instance.getActivity(name);
        assertEquals(null, result);
    }
}