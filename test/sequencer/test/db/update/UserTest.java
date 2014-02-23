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
import sequencer.db.User;
import static org.junit.Assert.*;
import sequencer.BCrypt.BCrypt;
import sequencer.db.util.MongoHelper;
import sequencer.test.util.TestHelper;

/**
 *
 * @author Daniel Rogers
 */
public class UserTest {
    
    private Integer instanceId;
    
    public UserTest() {
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
        MongoHelper.getCollection(MongoHelper.USER_COLLECTION).drop();
        
        User user = new User("test", "abc123");
        
        if(!MongoHelper.save(user, MongoHelper.USER_COLLECTION))
            TestHelper.failed("Save to DB Failed");
        
        instanceId = user.getId();
    }
    
    @After
    public void tearDown() {
        TestHelper.signoff(this);
    }

    /**
     * Test of checkPass method, of class User.
     */
    @Test
    public void testCheckPass() {
        System.out.println("checkPass");
        String password = "abc123";
        String hashed_password = BCrypt.hashpw(password, BCrypt.gensalt());
        
        User user = new User("test", hashed_password);
        
        TestHelper.asserting(user.checkPass(password));
        System.out.println("Password check okay");
        TestHelper.passed();
    }

    /**
     * Test of updateUname method, of class User.
     */
    /*@Test
    public void testUpdateUname() {
        System.out.println("updateUname");
        String newName = "test2";
        User instance = (User) MongoHelper.fetch(new User(instanceId),
                                                MongoHelper.USER_COLLECTION);
        
        instance.updateUname(newName);
        if(!MongoHelper.save(instance, MongoHelper.USER_COLLECTION))
            TestHelper.failed("Save to DB Failed");
        
        User copy = (User) MongoHelper.fetch(new User(instanceId),
                                                MongoHelper.USER_COLLECTION);
        
        TestHelper.asserting(instance.equals(copy));
        TestHelper.asserting(copy.getUname().equals(newName));
        
        System.out.println("Succesfully " + " user: " + instanceId + "'s name to " 
                            + newName);
        
        TestHelper.passed();
    }*/

    /**
     * Test of updatePass method, of class User.
     */
    @Test
    public void testUpdatePass() {
        System.out.println("updatePass");
        String newPass = "abcdefg";
        String hashed_newPass = BCrypt.hashpw(newPass, BCrypt.gensalt());
        
        User instance = (User) MongoHelper.fetch(new User(instanceId),
                                                MongoHelper.USER_COLLECTION);
        
        instance.updatePass(hashed_newPass);
        if(!MongoHelper.save(instance, MongoHelper.USER_COLLECTION))
            TestHelper.failed("Save to DB Failed");
        
        User copy = (User) MongoHelper.fetch(new User(instanceId),
                                                MongoHelper.USER_COLLECTION);
        
        TestHelper.asserting(instance.equals(copy));
        TestHelper.asserting(copy.checkPass(newPass));
        
        System.out.println("Succesfully " + " user: " + instanceId + "'s "
                + "password to " + newPass);
        
        TestHelper.passed();
    }

    /**
     * Test of addActivity method, of class User.
     */
    @Test
    public void testAddActivity() {
        System.out.println("addActivity");
        Integer id = new Integer(0);
        User instance = (User) MongoHelper.fetch(new User(instanceId),
                                                MongoHelper.USER_COLLECTION);
        
        instance.addActivity(id);
        if(!MongoHelper.save(instance, MongoHelper.USER_COLLECTION))
            TestHelper.failed("Save to DB Failed");
        
        User copy = (User) MongoHelper.fetch(new User(instanceId),
                                                MongoHelper.USER_COLLECTION);
        
        TestHelper.asserting(instance.equals(copy));
        TestHelper.asserting(copy.getActivities().contains(id));
        
        System.out.println("Succesfully added activity: " + id + " to"
                            + " user: " + instanceId);
        
        TestHelper.passed();
    }

    /**
     * Test of addGroup method, of class User.
     */
    @Test
    public void testAddGroup() {
        System.out.println("addGroup");
        
        Integer id = new Integer(0);
        User instance = (User) MongoHelper.fetch(new User(instanceId),
                                                MongoHelper.USER_COLLECTION);
        
        instance.addGroup(id);
        if(!MongoHelper.save(instance, MongoHelper.USER_COLLECTION))
            TestHelper.failed("Save to DB Failed");
        
        User copy = (User) MongoHelper.fetch(new User(instanceId),
                                                MongoHelper.USER_COLLECTION);
        
        TestHelper.asserting(instance.equals(copy));
        TestHelper.asserting(copy.getGroups().contains(id));
        
        System.out.println("Succesfully added group: " + id + " to"
                            + " user: " + instanceId);
        
        TestHelper.passed();
    }

    /**
     * Test of setSchedule method, of class User.
     */
    @Test
    public void testSetSchedule() {
        System.out.println("setSchedule");
        
        Integer id = new Integer(0);
        User instance = (User) MongoHelper.fetch(new User(instanceId),
                                                MongoHelper.USER_COLLECTION);
        
        instance.setSchedule(id);
        if(!MongoHelper.save(instance, MongoHelper.USER_COLLECTION))
            TestHelper.failed("Save to DB Failed");
        
        User copy = (User) MongoHelper.fetch(new User(instanceId),
                                                MongoHelper.USER_COLLECTION);
        
        TestHelper.asserting(instance.equals(copy));
        TestHelper.asserting(copy.getSchedule().equals(id));
        
        System.out.println("Succesfully set user: " + instanceId + "'s schedule"
                + " to schedule: " + id);
        
        TestHelper.passed();
    }

    /**
     * Test of equals method, of class User.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        User u = new User("test", "abc123");
        
        TestHelper.asserting(u.equals(u));
        
        TestHelper.passed();
    }
}