/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequencer.test.db.read;

import sequencer.test.db.delete.*;
import sequencer.test.db.create.*;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sequencer.db.User;
import static org.junit.Assert.*;
import sequencer.db.util.MongoHelper;
import sequencer.test.util.TestHelper;

/**
 *
 * @author Daniel Rogers
 */
public class UserTest {
    
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
    }
    
    @After
    public void tearDown() {
        TestHelper.signoff(this);
    }

    /**
     * Test reading user saved in DB
     */
    @Test
    public void testReadUser()
    {
        System.out.println("Read user");
        
        User user = new User("test", "abc123");
        
        if(!MongoHelper.save(user, MongoHelper.USER_COLLECTION))
            TestHelper.failed("Save to DB Failed");
        
        User read = (User) MongoHelper.fetch(new User(user.getId()), 
                                              MongoHelper.USER_COLLECTION);
        
        TestHelper.asserting(read.equals(user));
        
        System.out.println("Successfully read user:" + user.getId());
        TestHelper.passed();
    }
}