package sequencer.db.util;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.WriteResult;
import java.util.Date;
import sequencer.db.Entry;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

/**
 * Convenience class for automating some MongoDB chores.
 * @author Ron Coleman, Ph.D.
 */
public class MongoHelper {
    
    /*
     * these static final strings have been added to avoid typos in db 
     *  collection references - Daniel Rogers
     */
    /**
     *
     */
    public static final String ACTIVITY_COLLECTION = "Activities";
    /**
     *
     */
    public static final String GROUP_COLLECTION = "Groups";
    /**
     *
     */
    public static final String SCHEDULE_COLLECTION = "Schedules";
    /**
     *
     */
    public static final String USER_COLLECTION = "Users";
    /**
     *
     */
    public static final String PASSWORD_COLLECTION = "Passwords";
    /**
     *
     */
    public static final String SEQUENCER_TEST_DB = "SEQUENCER_TEST_DB";
    
    private static DB db;
    private static Jongo jongo;
    private static String dbName;
    
    /**
     * Sets the database for future use. We usually only need to do this
     * during initialization or setup.
     * @param dbName Database name
     */
    public static void setDB(String dbName) {
        MongoHelper.dbName = dbName;
    }
        
    /**
     * Gets the database, for internal use only.
     * @return MongoDB database
     */
    private static DB getDB() {
        if(db != null)
            return db;
        
        try {
            db = new Mongo().getDB(dbName);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
                
        return db;
    }
    
    /**
     * Gets the jongo, for internal use only.
     * @return Jongo
     */
    private static Jongo getJongo() {
        if(jongo == null) 
            jongo = new Jongo(getDB());
                    
        return jongo;
    }
    
    /**
     * Saves an entry with a unique id, if the entry id == -1 in which case
     * the entry will be assigned a unique id; otherwise the method updates
     * the entry.
     * @param entry Entry
     * @param collectionName Collection
     * @return True if save successful, false otherwise
     */
    public static Boolean save(Entry entry, String collectionName) {
        
        if(entry == null) return false;
        
        MongoCollection collection = getJongo().getCollection(collectionName);
        
        Boolean dirty = false;
        
        Integer id = entry.getId();
        
        IdTracker tracker = null;
        
        if(id == -1) {
            tracker = getNextId(collection);
            
            entry.setId(tracker.getNextId());
                        
            dirty = true;
        }
        
        Long now = System.currentTimeMillis();
        
        entry.setUpdateDate(new Date(now));
        
        WriteResult result = collection.save(entry);
        
        if(!result.getLastError().ok())
            return false;
        
        
        if(dirty)
            result = updateId(tracker,collection);

        
        return result.getLastError().ok();
    }
    
    /**
     * Fetches an entry from the collection.
     * @param entry Entry
     * @param collectionName Collection name
     * @return Entry if it exists, null otherwise
     */
    public static Entry fetch(Entry entry,String collectionName) {
        MongoCollection collection = getCollection(collectionName);
        
        return collection.findOne("{id: "+entry.getId()+"}").as(entry.getClass());
    }
    
    /**
     * Queries a collection.
     * @param criteria Mongo template
     * @param clss Class
     * @param collectionName Collection name
     * @return Iterable which has zero or more entries
     */
    public static Iterable<Entry> query(String criteria, Class clss, String collectionName) {
        MongoCollection collection = getCollection(collectionName);
        
        return collection.find(criteria).as(clss);
    }  
    
    /**
     * Deletes an entry.
     * @param entry Entry
     * @param collectionName Collection name
     * @return 
     */
    public static Boolean delete(Entry entry,String collectionName) {
        MongoCollection collection = getCollection(collectionName);
        
        WriteResult result = collection.remove("{id:"+entry.getId()+"}");
        
        return result.getLastError().ok();
    }
    
    /**
     * Deletes an entry by query.
     * @param query Query, i.e., mongodb template.
     * @param collectionName Collection name
     * @return 
     */
    public static Boolean delete(String query,String collectionName) {
        MongoCollection collection = getCollection(collectionName);
        
        WriteResult result = collection.remove(query);
        
        return result.getLastError().ok();        
    }
    
    /**
     * Gets the mongodb collection
     * @param collectionName Collection name
     * @return Mongodb collection
     */
    public static MongoCollection getCollection(String collectionName) {
        return getJongo().getCollection(collectionName);
    }
    
    /**
     * Gets the next id for a collection, for internal use only
     * @param collection Mongodb collection
     * @return IdTracker which wraps the next unique id
     */
    private static IdTracker getNextId(MongoCollection collection) {
        IdTracker one = collection.findOne("{nextId: {$gte:0}}").as(IdTracker.class);
        
        if(one == null)
            return new IdTracker(0);
        
        return one;
        
    }
    
    /**
     * Updates the unique id, for internal use only
     * @param tracker
     * @param collection
     * @return 
     */
    private static WriteResult updateId(IdTracker tracker, MongoCollection collection) {
        Integer id = tracker.getNextId() + 1;
        
        tracker.setNextId(id);
        
        return collection.save(tracker);
    }    
}
