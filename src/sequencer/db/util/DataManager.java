/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequencer.db.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import sequencer.db.Activity;
import sequencer.db.Group;
import sequencer.db.Schedule;
import sequencer.db.User;

/**
 * Manages the data like a bulldog in a suit. Handles all user data interaction
 * with all the efficiency expected of a large greyhound, in a bulldog's body.
 *                              (.... badly)
 * 
 * @author Daniel Rogers
 */
public class DataManager {

    private Integer userId;
    private User user;
    private Schedule schedule;
    private HashMap<String, Activity> activities;
    private HashMap<Integer, String> activityIdNameMap;
    private String[] activityNames;
    private HashMap<String, Group> groups;
    private HashMap<Integer, String> groupIdNameMap;
    private String[] groupNames;

    /**
     *
     * @param userId
     */
    public DataManager(Integer userId) {

        this.userId = userId;

        user = (User) MongoHelper.fetch(new User(userId),
                MongoHelper.USER_COLLECTION);
    }

    /**
     * Gets a group this user holds using the name of the group
     *
     * @param name The name of the activity
     * @return A grope with the corresponding name or null if the user doesn't
     * have the group
     */
    public Group getGroup(String name) {
        if (groups == null) {
            updateGroups();
        }
        return groups.get(name);
    }
    
    /**
     * Gets a group this users holds using the id of the group
     * 
     * @param id The id of the group
     * @return A group with the corresponding id or null if the user doesn't
     * have the group
     */
    public Group getGroupById(Integer id) {
        if(groups == null) {
            updateGroups();
        }
        
        return groups.get(groupIdNameMap.get(id));
    }

    /**
     *
     * @return The names of the groups that this user has in alphabetical order
     */
    public String[] getGroupNames() {
        if (groupNames == null) {
            updateGroups();
        }

        return groupNames;
    }

    /**
     * Update the data associated with a particular group. saves the group to
     * the database, overwriting the old data.
     * @param f The group to update
     */
    public void updateGroup(Group f) {
        if (groups == null) {
            updateGroups();
        }
        //save changes to db
        MongoHelper.save(f, MongoHelper.GROUP_COLLECTION);

        if (!user.getGroups().contains(f.getId())) {
            //user doesn't have the group yet
            user.addGroup(f.getId());

            //brand new group, just store it in our map and save it
            groups.put(f.getName(), f);
            groupIdNameMap.put(f.getId(), f.getName());
            //add to our name list
            groupNames = groups.keySet().toArray(new String[0]);

            if (groupNames.length > 1) {
                Arrays.sort(groupNames);
            }
            
            //save user to DB
            MongoHelper.save(user, MongoHelper.USER_COLLECTION);
        } //update local group
        else if (f.getName().equals(groupIdNameMap.get(f.getId()))) {
            //no name change
            groups.put(f.getName(), f);
        } else {
            //update with new name
            //remove old maping
            groups.remove(groupIdNameMap.get(f.getId()));
            //put in new maping
            groups.put(f.getName(), f);
            groupIdNameMap.put(f.getId(), f.getName());
            
            groupNames = groups.keySet().toArray(new String[0]);

            if (groupNames.length > 1) {
                Arrays.sort(groupNames);
            }
        }

    }

    /**
     * Deletes a group from the manager and the db
     * 
     * @param f The group to delete
     */
    public void removeGroup(Group f) {
        if (user.getGroups().contains(f.getId())) {
            //user will actually be changed
            user.removeGroup(f.getId());
            //we got rid of it, so don't keep it around
            groups.remove(groupIdNameMap.get(f.getId()));
            groupIdNameMap.remove(f.getId());

            groupNames = groups.keySet().toArray(new String[0]);
            //cleanup
            Activity a;
            for (Integer i : f.getActivities()) {
                //get each activity
                a = getActivityById(i);
                if (a != null) {
                    a.setGroup(null);

                    MongoHelper.save(a, MongoHelper.ACTIVITY_COLLECTION);
                }
            }

            //make changes to db
            MongoHelper.save(user, MongoHelper.USER_COLLECTION);
            MongoHelper.save(schedule, MongoHelper.SCHEDULE_COLLECTION);
            MongoHelper.delete(f, MongoHelper.GROUP_COLLECTION);
        }
    }

    /**
     * Gets the activities this user holds
     * 
     * @param name The name of the activity
     * @return An Activity with the corresponding name or null if not found.
     */
    public Activity getActivity(String name) {
        if (activities == null) {
            updateActivities();
        }
        //System.out.println(name);
        //System.out.println(activities);
        return activities.get(name);
    }

    /**
     * Gets an activity using the id of the activity
     * 
     * @param id The id of the desired activity
     * @return An Activity with the matching id, or null if not found
     */
    public Activity getActivityById(Integer id) {
        if (activities == null) {
            updateActivities();
        }
        return activities.get(activityIdNameMap.get(id));
    }

    /**
     * Gets the names of the activities this has in alphabetical order
     * 
     * @return A String[] containing the activity names in alpha order
     */
    public String[] getActivityNames() {
        if (activities == null) {
            updateActivities();
        }
        return activityNames;
    }
    
    /**
     * The activities that this user
     * @return An Activity[] containing the activities sorted by time end in
     *              ascending order
     */
    public Activity[] getActivities() {
        if (activities == null) {
            updateActivities();
        }
        
        Activity[] ret = activities.values().toArray(new Activity[]{});
        
        Arrays.sort(ret, new Activity(-1));
        
        return ret;
    }

    /**
     * Gets the schedule this user has
     *
     * @return The schedule of the user
     */
    public Schedule getSchedule() {
        if (schedule == null) {
            updateSchedule();
        }
        return schedule;
    }

    /**
     * updates the user schedule
     * @param s The schedule to set as the user's schedule
     * 
     */
    public void updateSchedule(Schedule s) {
        MongoHelper.delete(new Schedule(user.getSchedule()),
                MongoHelper.SCHEDULE_COLLECTION);
        
        schedule = s;
        MongoHelper.save(s, MongoHelper.SCHEDULE_COLLECTION);
        user.setSchedule(s.getId());
        MongoHelper.save(user, MongoHelper.USER_COLLECTION);
    }

    /**
     * Gets the owning userId
     *
     * @return the id of the user
     */
    public Integer getUId() {
        return userId;
    }

    /**
     * Update an activity with new data, or add a new activity to the user's
     *  list of activities.
     * 
     * @param a The activity in the state desired
     */
    public void updateActivity(Activity a) {
        if (activities == null) {
            updateActivities();
        }
        //System.out.println(a);
        //save changes to db
        MongoHelper.save(a, MongoHelper.ACTIVITY_COLLECTION);

        if (!user.getActivities().contains(a.getId())) {
            //user doesn't have the activity yet
            user.addActivity(a.getId());

            //brand new activity, just store it in our map and save it
            activities.put(a.getName(), a);
            activityIdNameMap.put(a.getId(), a.getName());
            //add to our name list
            activityNames = activities.keySet().toArray(new String[0]);

            if (activityNames.length > 1) {
                Arrays.sort(activityNames);
            }
            
            //save user to DB
            MongoHelper.save(user, MongoHelper.USER_COLLECTION);
        } //update local activity
        else if (a.getName().equals(activityIdNameMap.get(a.getId()))) {
            //no name change
            activities.put(a.getName(), a);
        } else {
            //update with new name
            //remove old maping
            activities.remove(activityIdNameMap.get(a.getId()));
            //put in new maping
            activities.put(a.getName(), a);
            activityIdNameMap.put(a.getId(), a.getName());
            
            activityNames = activities.keySet().toArray(new String[0]);

            if (activityNames.length > 1) {
                Arrays.sort(activityNames);
            }
        }
    }

    /**
     * Gets rid of an activity
     *
     * @param a The activity to delete
     */
    public void removeActivity(Activity a) {
        if (user.getActivities().contains(a.getId())) {
            //user will actually be changed
            user.removeActivity(a.getId());
            //we got rid of it, so don't keep it around
            activities.remove(activityIdNameMap.get(a.getId()));
            activityIdNameMap.remove(a.getId());

            activityNames = activities.keySet().toArray(new String[0]);
            //cleanup
            //remove from schedule
            if (schedule != null) {
                schedule.removeActivity(a.getId());
                MongoHelper.save(schedule, MongoHelper.SCHEDULE_COLLECTION);
            }
            //get the group and clean
            if (groups != null && groupIdNameMap != null) {
                Group f = groups.get(groupIdNameMap.get(a.getGroup()));
                if (f != null) {
                    f.removeActivity(a.getId());
                    MongoHelper.save(f, MongoHelper.GROUP_COLLECTION);
                }
            }

            //make changes to db
            MongoHelper.save(user, MongoHelper.USER_COLLECTION);
            MongoHelper.delete(a, MongoHelper.ACTIVITY_COLLECTION);
        }
    }

    /**
     * Fetches a fresh copy of the schedule from the db
     */
    private void updateSchedule() {
        //get their schedule
        schedule = (Schedule) MongoHelper.fetch(
                new Schedule(user.getSchedule()),
                MongoHelper.SCHEDULE_COLLECTION);
    }

    /**
     * Fetches copies of all the user's activities from the db
     */
    private void updateActivities() {
        if (user != null) {
            activities = new HashMap<>();
            activityIdNameMap = new HashMap<>();

            //get their activities
            ArrayList<Integer> temp = user.getActivities();

            if (temp != null) {
                activityNames = new String[temp.size()];
                Activity a;

                for (int i = 0; i < activityNames.length; ++i) {
                    //get the activity
                    a = (Activity) MongoHelper.fetch(new Activity(temp.get(i)),
                            MongoHelper.ACTIVITY_COLLECTION);

                    //record the name
                    activityNames[i] = a.getName();
                    //store it in the map
                    activities.put(a.getName(), a);
                    activityIdNameMap.put(a.getId(), a.getName());
                }

                //sort
                if (activityNames.length > 1) {
                    Arrays.sort(activityNames);
                }
            }
        }
    }

    /**
     * Fetches groups from the db
     */
    private void updateGroups() {
        if (user != null) {
            groups = new HashMap<>();
            groupIdNameMap = new HashMap<>();
            //get their groups
            ArrayList<Integer> temp = user.getGroups();

            if (temp != null) {
                groupNames = new String[temp.size()];
                Group f;

                for (int i = 0; i < groupNames.length; ++i) {
                    //get the group
                    f = (Group) MongoHelper.fetch(new Group(temp.get(i)),
                            MongoHelper.GROUP_COLLECTION);
                    //record the name
                    //System.out.println(f);
                    if (f != null) {
                        groupNames[i] = f.getName();
                        //store it in the map
                        groups.put(f.getName(), f);
                        groupIdNameMap.put(f.getId(), f.getName());
                    }
                }

                //sort
                if (groupNames.length > 1) {
                    Arrays.sort(groupNames);
                }
            }
        }
    }
}
