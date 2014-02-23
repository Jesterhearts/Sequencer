/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequencer.db;

import java.util.ArrayList;
import java.util.Date;
import sequencer.db.util.MongoHelper;

/**
 * Represents a user schedule with name, associated user, and activities in the
 * schedule
 *
 * @author Daniel Rogers
 */
public class Schedule extends Entry {

    private String name;
    private Integer userId;
    private ArrayList<Integer> activityIds;

    /**
     * don't make free floating empty schedules
     */
    private Schedule() {
    }

    /**
     * User for querying the database for a schedule
     * @param id 
     */
    public Schedule(Integer id) {
        super.id = id;
    }

    /**
     * Creates a schedule with a user, name, and activities
     *
     * @param user The userid of the owning user
     * @param name The name of the schedule
     * @param activities The ids of the activities that occur in this schedule
     */
    public Schedule(Integer user, String name, ArrayList<Integer> activities) {
        this.userId = user;
        this.name = name;

        if (activities != null) {
            this.activityIds = activities;
        } else {
            this.activityIds = new ArrayList<>();
        }
    }

    /**
     * Gets the userId of the owning user for this activity
     *
     * @return An Integer corresponding to the db id of the owning user
     */
    public Integer user() {
        return userId;
    }

    /**
     * Gets the name of this activity
     *
     * @return A string representing the name of this activity
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the activities that occur in this schedule
     *
     * @return The integer ids of the activities that occur
     */
    public ArrayList<Integer> getActivities() {
        return activityIds;
    }

    /**
     * Adds an activity that is part of this schedule. Also registers this
     * schedule with the activity.
     *
     * @param activityId The id of the activity to link
     */
    public void addActivity(Integer activityId) {
        if (!this.activityIds.contains(activityId)) {
            Activity a = (Activity) MongoHelper.fetch(new Activity(activityId),
                    MongoHelper.ACTIVITY_COLLECTION);

            if (a == null) {
                System.out.println("Activity " + activityId + " not found in db. \n"
                        + "Activity not registered with schedule " + id);
                return;
            }

            a.addSchedule(this.id);

            MongoHelper.save(a, MongoHelper.ACTIVITY_COLLECTION);

            activityIds.add(activityId);

            super.updateDate = new Date();
        }
    }

    /**
     * Removes an activity that is part of this schedule. Also un-registers this
     * schedule with the activity.
     *
     * @param activityId The id of the activity to unlink
     */
    public void removeActivity(Integer activityId) {
        if (activityIds != null && activityIds.contains(activityId)) {
            Activity a = (Activity) MongoHelper.fetch(new Activity(activityId),
                    MongoHelper.ACTIVITY_COLLECTION);

            activityIds.remove(activityId);
            super.updateDate = new Date();

            if (a == null) {
                System.out.println("Activity " + activityId
                        + " not found in db.");
                return;
            }

            a.removeSchedule(this.id);
            MongoHelper.save(a, MongoHelper.ACTIVITY_COLLECTION);
        }
    }

    /**
     *
     * @param s
     * @return
     */
    public boolean equals(Schedule s) {
        if (!this.activityIds.equals(s.activityIds)) {
            return false;
        }

        if (!this.name.equals(s.name)) {
            return false;
        }

        if (!this.userId.equals(s.userId)) {
            return false;
        }

        return true;
    }
    
    /**
     * More twine generation involving schedules
     * 
     * @return Schedule: <>, User: <>, Activities: <>, name: <>
     */
    @Override
    public String toString() {
        
        
        return "Schedule: " + this.id + ", User: " + this.userId + 
                ", Activities: " + this.activityIds + ", name: " + this.name;
    }
}
