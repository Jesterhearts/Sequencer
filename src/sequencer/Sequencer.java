/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequencer;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import sequencer.db.util.MongoHelper;
import sequencer.gui.LoginBox;

/**
 * Main class for Sequencer, responsible for getting the ball rolling.
 * 
 * @author Daniel Rogers
 */
public class Sequencer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("starting!");
        
        MongoHelper.setDB("demo_db");
        try {
            javax.swing.UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Sequencer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //dropall();

        LoginBox l = new LoginBox();
        
        l.setVisible(true);
    }
    
    /**
     * Dumps all data in the collections that sequencer uses in the current
     *      database
     */
    private static void dropall() {
        MongoHelper.getCollection(MongoHelper.USER_COLLECTION).drop();
        MongoHelper.getCollection(MongoHelper.ACTIVITY_COLLECTION).drop();
        MongoHelper.getCollection(MongoHelper.GROUP_COLLECTION).drop();
        MongoHelper.getCollection(MongoHelper.SCHEDULE_COLLECTION).drop();
    }
}
