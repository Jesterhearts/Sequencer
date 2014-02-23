/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequencer.test.db.suite;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import sequencer.test.*;

/**
 *
 * @author Daniel Rogers
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(
        { 
            //test DB CRUD
            //create
            sequencer.test.db.create.ActivityTest.class,
            sequencer.test.db.create.GroupTest.class,
            sequencer.test.db.create.ScheduleTest.class,
            sequencer.test.db.create.UserTest.class,
            
            //delete
            sequencer.test.db.delete.ActivityTest.class,
            sequencer.test.db.delete.GroupTest.class,
            sequencer.test.db.delete.ScheduleTest.class,
            sequencer.test.db.delete.UserTest.class,
            
            //read
            sequencer.test.db.read.ActivityTest.class,
            sequencer.test.db.read.GroupTest.class,
            sequencer.test.db.read.ScheduleTest.class,
            sequencer.test.db.read.UserTest.class,
            
            //update
            sequencer.test.db.update.ActivityTest.class,
            sequencer.test.db.update.GroupTest.class,
            sequencer.test.db.update.ScheduleTest.class,
            sequencer.test.db.update.UserTest.class,
            
        })
public class SequencerSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
}