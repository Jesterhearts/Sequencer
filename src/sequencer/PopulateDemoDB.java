/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequencer;

import sequencer.BCrypt.BCrypt;
import sequencer.db.Activity;
import sequencer.db.Group;
import sequencer.db.User;
import sequencer.db.util.DataManager;
import sequencer.db.util.MongoHelper;

/**
 * Populates a demo database with some test data
 * User: ExampleUser 
 *          (case sensitive)
 * Password: password
 *              (case sensitive)
 * 
 * @author Daniel Rogers
 */
public class PopulateDemoDB {

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        //set to our demo db and clear it out
        MongoHelper.setDB("demo_db");
        MongoHelper.getCollection(MongoHelper.USER_COLLECTION).drop();
        MongoHelper.getCollection(MongoHelper.ACTIVITY_COLLECTION).drop();
        MongoHelper.getCollection(MongoHelper.GROUP_COLLECTION).drop();
        MongoHelper.getCollection(MongoHelper.SCHEDULE_COLLECTION).drop();
        System.out.println("Cleared demo_db");

        String pass = BCrypt.hashpw("password", BCrypt.gensalt());

        User testUser = new User("ExampleUser", pass);
        MongoHelper.save(testUser, MongoHelper.USER_COLLECTION);

        System.out.println("created ExampleUser");

        DataManager manager = new DataManager(testUser.getId());

        System.out.println("setup datamanager");

        Activity a = new Activity(testUser.getId(), "Creative Writing",
                0, false, null, null,
                new boolean[]{false, false, true, false, true, false, false},
                13, 14, 10, 30);

        manager.updateActivity(a);

        a = new Activity(testUser.getId(), "Calculus I",
                0, true, null, null,
                new boolean[]{false, true, false, true, false, true, false},
                9, 10, 20, 40);

        manager.updateActivity(a);

        a = new Activity(testUser.getId(), "Yoga",
                0, false, null, null,
                new boolean[]{false, false, true, false, true, false, false},
                12, 13, 00, 00);

        manager.updateActivity(a);

        a = new Activity(testUser.getId(), "Spanish I",
                0, true, null, null,
                new boolean[]{false, false, true, false, true, false, false},
                9, 11, 30, 30);

        manager.updateActivity(a);

        a = new Activity(testUser.getId(), "Lg. Animal Diseases & Nursing",
                0, true, null, null,
                new boolean[]{false, false, false, true, false, false, false},
                11, 13, 0, 0);

        manager.updateActivity(a);

        a = new Activity(testUser.getId(), "Fencing",
                0, false, null, null,
                new boolean[]{false, true, false, false, false, true, false},
                18, 19, 0, 0);

        manager.updateActivity(a);

        a = new Activity(testUser.getId(), "Astronomy",
                0, false, null, null,
                new boolean[]{false, true, false, false, false, false, false},
                20, 23, 0, 0);

        manager.updateActivity(a);

        a = new Activity(testUser.getId(), "Exotic Animal D&N",
                0, true, null, null,
                new boolean[]{false, false, true, false, true, false, false},
                12, 14, 30, 30);

        manager.updateActivity(a);

        a = new Activity(testUser.getId(), "Computer Skills",
                0, false, null, null,
                new boolean[]{false, true, false, true, false, true, false},
                21, 12, 0, 0);

        manager.updateActivity(a);

        a = new Activity(testUser.getId(), "Modern Poetry and Art",
                0, true, null, null,
                new boolean[]{false, false, false, false, false, true, false},
                10, 11, 40, 55);

        manager.updateActivity(a);


        System.out.println("populated activities");

        Group g = new Group(testUser.getId(), "Evening Classes",
                10, null);
        
        manager.updateGroup(g);
        
        System.out.println("populated groups");
    }
}
