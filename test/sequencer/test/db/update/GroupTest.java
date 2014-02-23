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
import sequencer.db.Group;
import static org.junit.Assert.*;
import sequencer.db.util.MongoHelper;
import sequencer.test.util.TestHelper;

/**
 *
 * @author Daniel Rogers
 */
public class GroupTest {
    private Integer instanceId;
    
    public GroupTest() {
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
        MongoHelper.getCollection(MongoHelper.GROUP_COLLECTION).drop();
        
        Group group = new Group(new Integer(0), "group1", 10, null);
        
        if(!MongoHelper.save(group, MongoHelper.GROUP_COLLECTION))
            TestHelper.failed("Save to DB Failed");
        
        instanceId = group.getId();
    }
    
    @After
    public void tearDown() {
        TestHelper.signoff(this);
    }

    /**
     * Test of addActivity method, of class Group.
     */
    @Test
    public void testAddActivity() {
        System.out.println("addActivity");
        Integer id = new Integer(0);
        Group instance = (Group) MongoHelper.fetch(new Group(instanceId),
                                                MongoHelper.GROUP_COLLECTION);
        
        instance.addActivity(id);
        if(!MongoHelper.save(instance, MongoHelper.GROUP_COLLECTION))
            TestHelper.failed("Save to DB Failed");
        
        Group copy = (Group) MongoHelper.fetch(new Group(instanceId),
                                                MongoHelper.GROUP_COLLECTION);
        
        TestHelper.asserting(instance.equals(copy));
        TestHelper.asserting(copy.getActivities().contains(id));
        
        System.out.println("Succesfully added activity: " + id + " to"
                            + " group: " + instanceId);
        
        TestHelper.passed();
        
    }

    /**
     * Test of removeActivity method, of class Group.
     */
    @Test
    public void testRemoveActivity() {
        System.out.println("removeActivity");
        Integer id = new Integer(0);
        Group instance = (Group) MongoHelper.fetch(new Group(instanceId),
                                                MongoHelper.GROUP_COLLECTION);
        
        instance.addActivity(id);
        instance.removeActivity(id);
        
        if(!MongoHelper.save(instance, MongoHelper.GROUP_COLLECTION))
            TestHelper.failed("Save to DB Failed");
        
        Group copy = (Group) MongoHelper.fetch(new Group(instanceId),
                                                MongoHelper.GROUP_COLLECTION);
        
        TestHelper.asserting(instance.equals(copy));
        TestHelper.asserting(!copy.getActivities().contains(id));
        
        System.out.println("Succesfully deleted activity: " + id + " from"
                            + " group: " + instanceId);
        
        TestHelper.passed();
    }
    
   /**
     * Test of equals method, of class Group.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Group group = new Group(new Integer(0), "group1", 10, null);
        
        TestHelper.asserting(group.equals(group));
        
        TestHelper.passed();
    }
}