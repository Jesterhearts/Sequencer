/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequencer.db;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

/**
 * Represents a group with name, weight, associated user, and activities that
 * use this group.
 *
 * @author Daniel Rogers
 */
public class Group extends Entry implements Comparator {
    
    private String name;
    private int weight;
    private Integer userId;
    private ArrayList<Integer> activityIds;

    /**
     * don't make free-floating users
     */
    private Group() {
    }

    /**
     * User for querying db for group with id fId
     *
     * @param fId The id of the group to query for
     */
    public Group(Integer fId) {
        super.id = fId;
    }
    
    /**
     *
     * @param name
     */
    public Group(String name) {
        this.name = name;
    }

    /**
     * Creates a group with associated activities that utilize it
     *
     * @param userId The id of the owning user
     * @param name The name of this group
     * @param weight The weight of this group
     * @param activityIds The ids of the activities that apply this group
     */
    public Group(Integer userId, String name, int weight,
            ArrayList<Integer> activityIds) {
        this.userId = userId;
        this.name = name;
        this.weight = weight;
        super.id = name.hashCode();
        
        if (activityIds != null) {
            this.activityIds = activityIds;
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
     *
     * @param name
     */
    public void updateName(String name) {
        this.name = name;
    }

    /**
     * The weight of this group
     * @return
     */
    public int getWeight() {
        return weight;
    }
    
    /**
     * Changes the weight of this group
     * @param w The weight to use
     */
    public void updateWeight(int w) {
        this.weight = w;
    }

    /**
     * Gets the activities that reference this group
     *
     * @return The integer ids of the activities that reference this group
     */
    public ArrayList<Integer> getActivities() {
        return activityIds;
    }

    /**
     * Adds an activity that is linked to this group
     *
     * @param id The id of the activity to link
     */
    public void addActivity(Integer id) {
        if (!activityIds.contains(id)) {
            activityIds.add(id);
        }
        
        super.updateDate = new Date();
    }

    /**
     * Removes an activity from this group
     *
     * @param id The id of the activity to unlink
     */
    public void removeActivity(Integer id) {
        activityIds.remove(id);
        
        super.updateDate = new Date();
    }
    
    /**
     * Checks if this equals a group
     * 
     * @param f The group to check against
     * @return true if equal, false if not
     */
    public boolean equals(Group f) {
        if(f == null) {
            return false;
        }
        
        if (!this.activityIds.equals(f.activityIds)) {
            return false;
        }
        
        if (!this.name.equals(f.name)) {
            return false;
        }
        
        if (!this.userId.equals(f.userId)) {
            return false;
        }
        
        if (this.weight != f.weight) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Compares the names of these activities to make for easy alphabetical
     * sorting
     * @param t The group to compare to t1
     * @param t1 The group to compare to t
     * @return t.name().compareTo(t1.name())
     */
    @Override
    public int compare(Object t, Object t1) {
        return (((Group) t).getName().compareTo(((Group) t1).getName()));
    }
    
    /**
     * Clones this oject
     * @return A clone of this object
     */
    @Override
    public Object clone() {
        Group ret = new Group(userId, name, weight,
                (ArrayList<Integer>) activityIds.clone());
        ret.setKey(this.getKey());
        ret.setId(id);
        
        return ret;
    }
}
