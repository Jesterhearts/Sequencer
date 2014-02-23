/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequencer.gui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import javax.swing.ImageIcon;
import sequencer.db.Activity;
import sequencer.db.Schedule;
import sequencer.db.util.DataManager;

/**
 *
 * @author Daniel Rogers
 */
public class MainInterface extends javax.swing.JFrame {

    /**
     * Creates new form MainInterface
     */
    private MainInterface() {
        initComponents();
        //customInit();
    }

    /**
     * Creates new form MainInterface
     * @param manager 
     */
    public MainInterface(DataManager manager) {
        //userId = user;
        this.manager = manager;
        //weekModel = new String[]{"test"};
        updateActivityList();

        initComponents();
        //customInit();
        //setActivityPane();
        setLocationRelativeTo(null);
        this.setIconImage(
                new ImageIcon(System.getProperty("user.dir") + 
                "\\images\\icon.png").getImage());
    }

    /**
     * Update the models for the activity lists
     */
    private void updateActivityList() {

        //get today
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE - MMM. dd");
        String today = formatter.format(new java.util.Date());
        //int day = Calendar.getInstance().get(Calendar.DATE);

        Schedule s = manager.getSchedule();

        //get activities in schedule
        if (s != null && s.getActivities() != null) {
            //System.out.println(s);
            ArrayList<ArrayList<Activity>> acts = new ArrayList<>();
            for (int i = 0; i < 7; ++i) {
                acts.add(new ArrayList<Activity>());
            }
            Activity temp;

            //ArrayList<String> actnames = new ArrayList<>();


            boolean[] days;
            int[] ts;
            int[] te;
            Integer[] ids = s.getActivities().toArray(new Integer[0]);

            //scheduleActivities = new String[ids.length];

            int wkDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;

            //model = new String[scheduleActivities.length + 1];
            //set title to today's date
            //model[0] = today;
            int tcount = 0;

            //sort the activities into their appropriate days
            for (int x = 0; x < ids.length; ++x) {
                //System.out.println(wkDay);
                temp = manager.getActivityById(ids[x]);
                days = temp.getDays();

                if (days[0]) {
                    //sunday
                    acts.get(0).add(temp);
                    ++tcount;
                }
                if (days[1]) {
                    //monday
                    acts.get(1).add(temp);
                    ++tcount;
                }
                if (days[2]) {
                    //tuesday
                    acts.get(2).add(temp);
                    ++tcount;
                }
                if (days[3]) {
                    //wednesday
                    acts.get(3).add(temp);
                    ++tcount;
                }
                if (days[4]) {
                    //thursday
                    acts.get(4).add(temp);
                    ++tcount;
                }
                if (days[5]) {
                    //friday
                    acts.get(5).add(temp);
                    ++tcount;
                }
                if (days[6]) {
                    //saturday
                    acts.get(6).add(temp);
                    ++tcount;
                }
            }

            //System.out.println(wkDay);
            //get the activity set for today
            Activity[] tacts = acts.get(wkDay).toArray(new Activity[0]);
            
            //sort by time
            if (tacts.length > 1) {
                Arrays.sort(tacts, new Activity(-1));
            }
            
            //update our model to be big enough
            model = new String[tacts.length + 1];

            for (int x = 0; x < tacts.length; ++x) {
                //get our times
                ts = tacts[x].getTimeStart();
                te = tacts[x].getTimeEnd();

                // format our output to look nice
                model[x + 1] = "    " + fmtTime(ts) + " - " + fmtTime(te)
                        + "  —  " + tacts[x].getName();
            }
            
            if(model.length == 1) {
                model = new String[2];
                model[1] = "    No scheduled activities.";
            }

            for (ArrayList<Activity> ar : acts) {
                if (ar.isEmpty()) {
                    ++tcount;
                }
            }
            
            //tcount = space for activities + any "no schedule" that appear
            // 14 is to account for week names and blank lines to separate the
            // sets of activities
            weekModel = new String[tcount + 14];
            String[] wkdays = new String[]{"Sunday", "Monday", "Tuesday",
                "Wednesday", "Thursday", "Friday",
                "Saturday"};
            
            int w = 0;
            int i = 0;
            //for each set of activites on each weekday
            for (ArrayList<Activity> ar : acts) {
                //select the next part of the model, and set to the right 
                // name weekday
                weekModel[i++] = wkdays[w++];
                //convert the weekday's activities to an array
                tacts = ar.toArray(new Activity[0]);
                if (tacts.length > 1) {
                    //sort by time
                    Arrays.sort(tacts, new Activity(-1));
                }

                //get the times for each
                for (int x = 0; x < ar.size(); ++x) {
                    ts = tacts[x].getTimeStart();
                    te = tacts[x].getTimeEnd();
                    //format nicely
                    weekModel[i + x] = "    " + fmtTime(ts) + " - " + fmtTime(te)
                            + "  —  " + tacts[x].getName();
                }
                //no activities happen
                if (ar.isEmpty()) {
                    //increment by one more slot
                    weekModel[i++] = "    No scheduled activities.";
                } else {
                    //we added ar.size() entries to our week model
                    i += ar.size();
                }
                //blank line for easy reading
                weekModel[i++] = " ";
            }
            

        } else {
            //they didn't do anything at all
            model = new String[2];
            model[1] = "    No scheduled activities.";
            
            //7 weekdays + 7 blank lines + 7 "no scheduled"
            weekModel = new String[21];
            String[] wkdays = new String[]{"Sunday", "Monday", "Tuesday",
                "Wednesday", "Thursday", "Friday",
                "Saturday"};
            //fill in indexes
            for (int i = 0; i < wkdays.length; ++i) {
                weekModel[i * 3] = wkdays[i];
                weekModel[i * 3 + 1] = "    No scheduled activities.";
                weekModel[i * 3 + 2] = " ";
            }
        }

        //the first index of today's model is today
        model[0] = today;

    }

    /**
     * formats the time of an activity
     * @param time an int[] representing the hr:min of the activity
     * @return A good representation of it eg. - 01:00 am/pm
     */
    private String fmtTime(int[] time) {
        String ret;

        if (time[0] >= 12) {
            ret = String.format("%1$02d:%2$02d pm",
                    ((time[0] == 12) ? 12 : (time[0] - 12)),
                    (time[1]));
        } else if (time[0] == 0) {
            ret = String.format("12:%1$02d am", (time[1]));

        } else {
            ret = String.format("%1$02d:%2$02d am", (time[0]), (time[1]));

        }

        return ret;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        todayActivityList = new javax.swing.JList();
        jScrollPane1 = new javax.swing.JScrollPane();
        weekActivityList = new javax.swing.JList();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem5 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItem10 = new javax.swing.JMenuItem();
        fileMenuExit = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        editMenuAct = new javax.swing.JMenuItem();
        editMenuGroups = new javax.swing.JMenuItem();
        editMenuGenSched = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem9 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sequencer: Main");

        todayActivityList.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        todayActivityList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = model;
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(todayActivityList);

        jTabbedPane1.addTab("Today", jScrollPane3);

        weekActivityList.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        weekActivityList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = weekModel;
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(weekActivityList);

        jTabbedPane1.addTab("Week", jScrollPane1);

        jMenu1.setText("File");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("New...");
        jMenu1.add(jMenuItem1);
        jMenu1.add(jSeparator1);

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem5.setText("Open...");
        jMenu1.add(jMenuItem5);
        jMenu1.add(jSeparator2);

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem6.setText("Save...");
        jMenu1.add(jMenuItem6);

        jMenuItem7.setText("Save as...");
        jMenu1.add(jMenuItem7);
        jMenu1.add(jSeparator3);

        jMenuItem10.setText("Logout...");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem10);

        fileMenuExit.setText("Exit");
        fileMenuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileMenuExitActionPerformed(evt);
            }
        });
        jMenu1.add(fileMenuExit);

        jMenuBar1.add(jMenu1);

        editMenu.setText("Edit");

        editMenuAct.setText("Activities...");
        editMenuAct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editMenuActActionPerformed(evt);
            }
        });
        editMenu.add(editMenuAct);

        editMenuGroups.setText("Groups...");
        editMenuGroups.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editMenuGroupsActionPerformed(evt);
            }
        });
        editMenu.add(editMenuGroups);

        editMenuGenSched.setText("Generate Schedule...");
        editMenuGenSched.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editMenuGenSchedActionPerformed(evt);
            }
        });
        editMenu.add(editMenuGenSched);

        jMenuBar1.add(editMenu);

        jMenu3.setText("View");

        jMenuItem9.setText("Month...");
        jMenu3.add(jMenuItem9);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 675, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Makes activity boxes
     * @param evt 
     */
    private void editMenuActActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editMenuActActionPerformed
        ActivityBox a = new ActivityBox(manager);

        //a.setModal(true);
        a.setVisible(true);
    }//GEN-LAST:event_editMenuActActionPerformed

    /**
     * Makes generator windows
     * @param evt 
     */
    private void editMenuGenSchedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editMenuGenSchedActionPerformed
        //System.out.println(userId);
        GeneratorWindow g = new GeneratorWindow(manager);
        g.setModal(true);
        g.setVisible(true);

        // we've changed our schedule potentially, make sure our list models are
        // up to date
        updateActivityList();
        //setActivityPane();

        //update the models
        todayActivityList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = model;

            @Override
            public int getSize() {
                return strings.length;
            }

            @Override
            public Object getElementAt(int i) {
                return strings[i];
            }
        });

        weekActivityList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = weekModel;

            @Override
            public int getSize() {
                return strings.length;
            }

            @Override
            public Object getElementAt(int i) {
                return strings[i];
            }
        });


        this.repaint();
    }//GEN-LAST:event_editMenuGenSchedActionPerformed

    /**
     * makes logout and loginbox
     * @param evt 
     */
    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        LoginBox l = new LoginBox();
        l.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    /**
     * kills itself
     * @param evt 
     */
    private void fileMenuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileMenuExitActionPerformed
        this.dispose();
    }//GEN-LAST:event_fileMenuExitActionPerformed

    private void editMenuGroupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editMenuGroupsActionPerformed
        ActivityGroupBox f = new ActivityGroupBox(manager);
        //f.setModal(true);
        f.setVisible(true);
    }//GEN-LAST:event_editMenuGroupsActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainInterface().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem editMenuAct;
    private javax.swing.JMenuItem editMenuGenSched;
    private javax.swing.JMenuItem editMenuGroups;
    private javax.swing.JMenuItem fileMenuExit;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JList todayActivityList;
    private javax.swing.JList weekActivityList;
    // End of variables declaration//GEN-END:variables
    String[] model;
    String[] weekModel;
    DataManager manager;
}
