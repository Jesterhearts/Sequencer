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
import sequencer.db.Group;
import sequencer.db.util.MongoHelper;
import sequencer.test.util.TestHelper;

/**
 *
 * @author Daniel Rogers
 */
public class GroupTest {

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
        MongoHelper.getCollection(MongoHelper.GROUP_COLLECTION).drop();
    }

    @After
    public void tearDown() throws Exception {
        TestHelper.signoff(this);
    }
    
    /**
     * Test reading group saved in DB
     */
    @Test
    public void testReadGroup()
    {
        System.out.println("Read group");
        
        Group group = new Group(new Integer(0), "group1", 10, null);
        
        if(!MongoHelper.save(group, MongoHelper.GROUP_COLLECTION))
            TestHelper.failed("Save to DB Failed");
        
        Group read = (Group) MongoHelper.fetch( new Group(group.getId()), 
                                                MongoHelper.GROUP_COLLECTION);
        
        TestHelper.asserting(read.equals(group));
        
        System.out.println("Successfully read group:" + group.getId());
        TestHelper.passed();
    }
    
}