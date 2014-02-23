package sequencer.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import sequencer.db.util.MongoHelper;

/**
 * Represents an activity with a name, weight, associated user, group, and
 * active times. Also specifies if the activity is required to be in a schedule
 *
 * @author Daniel Rogers
 */
public class Activity extends Entry implements Comparator {

    private String name;
    private int weight;
    private boolean required;
    //weight + group weight
    private boolean[] days;
    private int[] tStart;
    private int[] tEnd;
    private Integer userId;
    private Integer activeGroupId;
    private ArrayList<Integer> scheduleIds;

    /**
     * Don't make free-floating empty activities
     */
    private Activity() {
    }

    /**
     * Used to query database for an Activity
     * @param id 
     */
    public Activity(Integer id) {
        super.id = id;
    }

    /**
     * Creates an activity with a name, weight, times it occurs, a group, an
     * associated schedule, and an associated user. This will not allow a time
     * of less than 1 hour, the db object will automatically make the start time
     * 1 hour later than the end time if they are <=
     *
     * @param userId The owning user of this activity
     * @param name The name of this activity
     * @param weight The weight of this activity
     * @param required Indicates if the activity must be included in a schedule
     * @param GroupId The id of a group applied to this id
     * @param scheduleId The id of a schedule that this activity is a part of
     * @param days a boolean[7] containing the days that this activity occurs
     * @param tHrStart The start hour of this activity
     * @param tHrEnd the end our of this activity
     * @param tMinStart the start minute of this activity
     * @param tMinEnd the end minute of this activity
     */
    public Activity(Integer userId, String name, int weight, boolean required,
            Integer GroupId, Integer scheduleId,
            boolean[] days, int tHrStart, int tHrEnd, int tMinStart,
            int tMinEnd) {

        this.days = new boolean[7];
        this.tStart = new int[2];
        this.tEnd = new int[2];

        if (days != null && days.length == 7) {
            this.days = days;
        }

        //default to 0 if out of bounds
        if (tHrStart < 24 && tHrStart >= 0) {
            this.tStart[0] = tHrStart;
        }

        if (tHrEnd < 24 && tHrEnd >= 0) {
            if (tHrEnd > tHrStart) {
                this.tEnd[0] = tHrEnd;
            } else {
                this.tEnd[0] = tHrStart + 1;
            }
        }

        if (tMinStart < 60 && tMinStart >= 0) {
            this.tStart[1] = tMinStart;
        }

        if (tMinEnd < 60 && tMinEnd >= 0) {
            this.tEnd[1] = tMinEnd;
        }

        this.userId = userId;
        this.name = name;
        this.required = required;
        this.weight = weight;
        this.scheduleIds = new ArrayList<>();

        if (GroupId != null) {
            this.setGroup(GroupId);
        }
        if (scheduleId != null) {
            this.addSchedule(scheduleId);
        }

    }

    /**
     * Gets the group associated with this activity
     *
     * @return the id of the group associated with this activity null if no
     * active group
     */
    public Integer getGroup() {
        return activeGroupId;
    }

    /**
     * Gets the schedule this activity is a part of
     *
     * @return the id of the schedule this activity is a part of null if not in
     * a schedule
     */
    public ArrayList<Integer> getSchedules() {
        return scheduleIds;
    }

    /**
     * The id of the schedule that this activity is no longer part of
     * @param scheduleId
     */
    public void removeSchedule(Integer scheduleId) {
        this.scheduleIds.remove(scheduleId);
    }

    /**
     * Gets the weight of this activity, including active group (if applicable)
     *
     * @return The base weight of this activity + the weight of the active group
     */
    public int getRealWeight() {
        Group f = (Group) MongoHelper.fetch(new Group(activeGroupId),
                MongoHelper.GROUP_COLLECTION);
        if (f != null) {
            return weight + f.getWeight();
        } else {
            return weight;
        }
    }

    /**
     * Gets the weight of this activity
     *
     * @return an int representing how preferred this activity is
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Sets the weight of this activity
     *
     * @param weight The weight to use
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * Checks if this activity is required -- e.g. The schedule must attempt to
     * include this activity unless it conflicts with another required activity
     *
     * @return True if the activity is required
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Flags or unflags this activity as required
     *
     * @param req a boolean representing the state of requiredness
     */
    public void setRequired(boolean req) {
        this.required = req;
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
     * Sets the name of this activity
     *
     * @param name The name to use
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the active days in Su,M,T,W,Tr,F,Sa order
     *
     * @return A boolean array with true corresponding to active days
     */
    public boolean[] getDays() {
        return days;
    }

    /**
     * Gets the {hr,min} that the activity starts in 24hr time format This will
     * not allow a time of less than 1 hour, the db object will automatically
     * make the start time 1 hour later than the end time if they are <=
     *
     * @return An int array containing the hr, min
     */
    public int[] getTimeStart() {
        return tStart;
    }

    /**
     * Gets the {hr,min} that the activity ends in 24hr time format This will
     * not allow a time of less than 1 hour, the db object will automatically
     * make the start time 1 hour later than the end time if they are <=
     *
     * @return An int array containing the hr, min
     */
    public int[] getTimeEnd() {
        return tEnd;
    }

    /**
     * Updates the group associated with this activity. This will register this
     * activity's id with the new group, and attempt to unregister it with the
     * old group.
     *
     * @param GroupId The id of the new group for this activity
     */
    public void setGroup(Integer GroupId) {

        Group old_f = null;
        Group new_f = null;

        //need to update active group to not include this entry
        if (activeGroupId != null) {
            old_f = (Group) MongoHelper.fetch(new Group(activeGroupId),
                    MongoHelper.GROUP_COLLECTION);
            //no db copy, so nothing to do really
            if (old_f == null) {
                System.out.println("Old group " + activeGroupId + " not "
                        + "found. Could not unregister activity with db copy "
                        + "of group");
            } else {
                old_f.removeActivity(id);
            }
        }

        //need to update the new group to include this activity
        if (GroupId != null) {
            //add the activity to the group's activity list
            new_f = (Group) MongoHelper.fetch(new Group(GroupId),
                    MongoHelper.GROUP_COLLECTION);
            if (new_f == null) {
                System.out.println("New group " + GroupId + " not found. ");
                //no need to do anything further, changes not written to db yet
                //so returning will just drop the changes
            } else {
                new_f.addActivity(id);
            }
        }

        //write the updated group back to the db
        if (old_f != null) {
            MongoHelper.save(old_f, MongoHelper.GROUP_COLLECTION);
        }

        if (new_f != null) {
            MongoHelper.save(new_f, MongoHelper.GROUP_COLLECTION);
        }

        //perform the update
        this.activeGroupId = GroupId;

        super.updateDate = new Date();
    }

    /**
     * Sets the schedule that this activity is associated with
     *
     * @param scheduleId The id of the schedule this activity is a part of
     */
    public void addSchedule(Integer scheduleId) {
        //this.scheduleIds = scheduleId;
        if (this.scheduleIds == null) {
            this.scheduleIds = new ArrayList<>();
        }
        if (!this.scheduleIds.contains(scheduleId)) {
            this.scheduleIds.add(scheduleId);
        }

        super.updateDate = new Date();
    }

    /**
     * Sets the active days of the week to a new set of days
     *
     * @param d A boolean array containing true for every day that the activity
     * occurs.
     */
    //NOTE THST THIS MUST BE "updateDays". When using "setDays", jongo crashed
    //  when attempting to read in Activities from the database
    public void updateDays(boolean[] d) {
        if (d != null && d.length == 7) {
            days = d;
            super.updateDate = new Date();
        }
    }

    /**
     * Sets the start time of this activity in 24 hour time
     *
     * @param hr The hour it starts in 24hr time
     * @param min The minute it starts
     */
    public void setStartTime(int hr, int min) {
        if (hr < 24 && hr >= 0) {
            if (hr < tEnd[0]) {
                this.tStart[0] = hr;
            } else {
                this.tStart[0] = hr;
                this.tEnd[0] = tStart[0] + 1;
            }
        }

        if (min < 60 && min >= 0) {
            this.tStart[1] = min;
        }

        super.updateDate = new Date();
    }

    /**
     * Sets the end time of this activity in 24 hour time
     *
     * @param hr The hour it ends in 24hr time
     * @param min The minute it ends
     */
    public void setEndTime(int hr, int min) {
        if (hr < 24 && hr >= 0) {
            if (hr > this.tStart[0]) {
                this.tEnd[0] = hr;
            } else {
                this.tEnd[0] = tStart[0] + 1;
            }
        }

        if (min < 60 && min >= 0) {
            this.tEnd[1] = min;
        }

        super.updateDate = new Date();
    }

    /**
     * Sees if this activity equals another activity
     *
     * @param a The activity to compare to
     * @return false if they differ, true if they don't
     */
    public boolean equals(Activity a) {
        //System.out.println("a");
        if (a == null) {
            return false;
        }

        if (this.required != a.required) {
            return false;
        }

        //System.out.println("b");
        //System.out.println(this);
        if ((this.name == null && a.name != null)
                || (this.name != null && !this.name.equals(a.name))) {
            return false;
        }

        //System.out.println("c");

        if (this.activeGroupId != null
                && !this.activeGroupId.equals(a.activeGroupId)) {
            return false;
        }

        if (this.activeGroupId == null && a.activeGroupId != null) {
            return false;
        }

        //System.out.println("d");
        if (!Arrays.equals(this.days, a.days)) {
            return false;
        }

        if (!Arrays.equals(this.tStart, a.tStart)) {
            return false;
        }

        if (!Arrays.equals(this.tEnd, a.tEnd)) {
            return false;
        }

        //System.out.println("e");
        if (!this.userId.equals(a.userId)) {
            return false;
        }

        //System.out.println("f");
        if (this.scheduleIds != null && !this.scheduleIds.equals(a.scheduleIds)) {
            return false;
        }

        if (this.scheduleIds == null && a.scheduleIds != null) {
            return false;
        }

        if (this.weight != a.weight) {
            return false;
        }

        return true;
    }

    /**
     * Makes this into a piece of string
     *
     * @return A 6inch string with all the data regarding this activity. Quality
     * of twined fibers is not guaranteed. String will be wound around the
     * following format: name: <>, weight: <>, group: <>, schedule: <>, userid:
     * <>, activeDays: <>, timeStart: <>, timeEnd: <>, required: <>
     */
    @Override
    public String toString() {
        return "name: " + name + ", weight: " + weight
                + ", group: "
                + activeGroupId + ", schedule: " + scheduleIds
                + ", userid: " + userId + ", activeDays: "
                + Arrays.toString(days) + ", timeStart: "
                + Arrays.toString(tStart) + ", timeEnd: " + Arrays.toString(tEnd)
                + ", required: " + required;
    }

    /**
     * Compares an activity based on time
     *
     * @param t An activity to compare to t1
     * @param t1 An activity to compare to t
     * @return The difference in the end times between t and t1
     */
    @Override
    public int compare(Object t, Object t1) {
        //System.out.println(t.toString());
        //System.out.println(t1.toString());
        int[] tend = ((Activity) t).tEnd;
        int[] t1end = ((Activity) t1).tEnd;

        int dif = (tend[0] - t1end[0]) * 60 + (tend[1] - t1end[1]);

        return dif;

    }

    /**
     * Clones this activity Warning: may cause sudden jedi death.
     *
     * @return A perfect clone of this activity
     */
    @Override
    public Object clone() {
        Activity ret = new Activity(userId, name, weight, required, activeGroupId,
                null, days, tStart[0], tEnd[0], tStart[1], tEnd[1]);
        if (scheduleIds != null) {
            for (Integer i : scheduleIds) {
                ret.addSchedule(i);
            }
        }
        ret.setId(this.id);
        ret.setKey(this.getKey());

        return ret;
    }
}
