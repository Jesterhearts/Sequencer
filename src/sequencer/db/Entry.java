package sequencer.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import org.bson.types.ObjectId;

/**
 * This is the base class for all entries.
 * @author Ron Coleman, Ph.D.
 */
public abstract class Entry {
    /** Mongo id */
    @JsonProperty("_id")
    private ObjectId key = null;
    
    /** Order system id */
    protected Integer id = -1;
    
    /** Order system update date */
    protected Date updateDate = null;


    /** Constructor */
    public Entry() {
        
    }
    
    /** Constructor
     * 
     * @param id Unique id of entry
     */
    public Entry(Integer id) {
        this.id = id;
    }
    
    /**
     * Get the value of updateDate
     *
     * @return the value of updateDate
     */
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * Set the value of updateDate
     *
     * @param updateDate new value of updateDate
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
    
    /**
     * Get the value of id
     *
     * @return the value of id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Set the value of id
     *
     * @param id new value of id
     */
    public void setId(Integer id) {
        this.id = id;
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
     * Gets the entry create date
     * @return Create date
     */
    public Date getCreateDate() {
        if(key == null)
            return null;
        
        return new Date(key.getTime());
    }

}
