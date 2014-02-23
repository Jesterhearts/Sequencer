/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequencer.db.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;

/**
 *
 * @author roncoleman125
 */
public class IdTracker {
    @JsonProperty("_id")
    private ObjectId key;
    
    private Integer nextId;
   
    /** Constructor */
    public IdTracker() {
        
    }
    
    /**
     * Constructor
     * @param nextId Next id
     */
    public IdTracker(Integer nextId) {
        this.nextId = nextId;
    }
    
    /**
     * Get the value of key
     *
     * @return the value of key
     */
    public ObjectId getKey() {
        return key;
    }

    /**
     * Set the value of key
     *
     * @param key new value of key
     */
    public void setKey(ObjectId key) {
        this.key = key;
    }

    /**
     *
     * @return
     */
    public Integer getNextId() {
        return nextId;
    }

    /**
     *
     * @param nextId
     */
    public void setNextId(Integer nextId) {
        this.nextId = nextId;
    }
    
    /**
     *
     */
    public void increment() {
        nextId = nextId + 1;
    }
}
