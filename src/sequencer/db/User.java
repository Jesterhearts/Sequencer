/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequencer.db;

import java.util.ArrayList;
import java.util.Date;
import sequencer.BCrypt.BCrypt;

/**
 * Represents a user, with associated username, password, activities, groups,
 * and schedule.
 *
 * @author Daniel Rogers
 */
public class User extends Entry {

    private String uname;
    private String pass;
    private ArrayList<Integer> activityIds;
    private ArrayList<Integer> groupIds;
    private Integer activeScheduleId;

    /**
     * don't make null users
     */
    private User() {
    }

    /**
     * The user get by id
     * @param id their id
     */
    public User(Integer id) {
        super.id = id;
    }

    /**
     * Use to query database by name
     * @param uname their name
     */
    public User(String uname) {
        this.uname = uname;

        this.activityIds = new ArrayList<>();
        this.groupIds = new ArrayList<>();

        super.id = uname.hashCode();
    }

    /**
     * Creates a user with only a username and password
     *    Password should be encrypted using BCrypt package
     * 
     * @param uname The username for the user
     * @param pass The password for the user
     */
    public User(String uname, String pass) {
        this.uname = uname;
        this.pass = pass;

        this.activityIds = new ArrayList<>();
        this.groupIds = new ArrayList<>();

        super.id = uname.hashCode();

    }

    /**
     * Creates a user with activities, groups, and an active schedule
     *
     * @param uname the username
     * @param pass the password
     * @param activities the ids for the user's activities in the db
     * @param groups the ids for the user's groups in the db
     * @param schedule the id for the user's schedule in the db
     */
    public User(String uname, String pass,
            ArrayList<Integer> activities,
            ArrayList<Integer> groups,
            Integer schedule) {
        this.uname = uname;
        this.pass = pass;
        this.activityIds = activities;
        this.groupIds = groups;
        this.activeScheduleId = schedule;

        super.id = uname.hashCode();
    }

    /**
     * Gets the username
     *
     * @return a String representing the username
     */
    public String getUname() {
        return uname;
    }

    /**
     * Checks the user password against a supplied password using BCryptcheckpw
     *
     * @param password The password to check
     * @return true if the passwords match
     */
    public boolean checkPass(String password) {
        return BCrypt.checkpw(password, pass);
    }

    /**
     * Gets the activity list for this user
     *
     * @return A list of db ids corresponding to the user's activities
     */
    public ArrayList<Integer> getActivities() {
        return activityIds;
    }

    /**
     *
     * @param a
     */
    public void removeActivity(Integer a) {
        activityIds.remove(a);
    }

    /**
     * gets the group list for this user
     *
     * @return A list of db ids corresponding to the user's groups
     */
    public ArrayList<Integer> getGroups() {
        return groupIds;
    }

    /**
     *
     * @param f
     */
    public void removeGroup(Integer f) {
        if (groupIds != null) {
            groupIds.remove(f);
        }
    }

    /**
     * gets the user's active schedule
     *
     * @return An integer corresponding to the db id of the user's schedule
     */
    public Integer getSchedule() {
        return activeScheduleId;
    }

    /**
     * Updates the password to a new password
     *
     * @param newPass The new password to use
     */
    public void updatePass(String newPass) {
        pass = newPass;

        super.updateDate = new Date();
    }

    /**
     * Adds a new activity to this user
     *
     * @param activityId An integer id representing the activity in the db
     */
    public void addActivity(Integer activityId) {
        activityIds.add(activityId);

        super.updateDate = new Date();
    }

    /**
     * Adds a new group to this user
     *
     * @param groupId An integer id representing the group in the db
     */
    public void addGroup(Integer groupId) {
        if (groupIds == null) {
            groupIds = new ArrayList<>();
        }

        groupIds.add(groupId);

        super.updateDate = new Date();
    }

    /**
     * Sets the user's active schedule
     *
     * @param scheduleId An integer id representing the schedule in the db
     */
    public void setSchedule(Integer scheduleId) {
        activeScheduleId = scheduleId;

        super.updateDate = new Date();
    }

    /**
     *
     * @param u
     * @return
     */
    public boolean equals(User u) {
        if (this.activeScheduleId != null
                && !this.activeScheduleId.equals(u.activeScheduleId)) {
            return false;
        }

        if (this.activeScheduleId == null && u.activeScheduleId != null) {
            return false;
        }

        if (!pass.equals(u.pass)) {
            return false;
        }

        if (!this.uname.equals(u.uname)) {
            return false;
        }

        if (!this.groupIds.equals(u.groupIds)) {
            return false;
        }

        if (!this.activityIds.equals(u.activityIds)) {
            return false;
        }

        return true;
    }
}
