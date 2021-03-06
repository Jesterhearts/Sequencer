/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequencer.gui;

import java.util.Arrays;
import javax.swing.JOptionPane;
import sequencer.db.Group;
import sequencer.db.util.DataManager;

/**
 * This GUI manages the user's groups
 * 
 * @author Daniel Rogers
 */
public class ActivityGroupBox extends javax.swing.JDialog {

    /**
     * Creates new form GroupBox
     */
    private ActivityGroupBox() {
        initComponents();
    }

    /**
     * Create 
     * @param manager
     */
    public ActivityGroupBox(DataManager manager) {
        //userId = user;
        this.manager = manager;
        getGroups();
        initComponents();
        oldIndx = 0;
        groupList.setSelectedIndex(0);

        setLocationRelativeTo(null);

        successLabel.setVisible(false);
    }

    /**
     * Gets the groups that the user holds and updates the list model
     */
    private void getGroups() {

        String[] temp = manager.getGroupNames();
        //build a new array to hold all our names plus the new value
        if (temp != null) {
            groupNames = new String[temp.length + 1];

            groupNames[0] = "<New>";

            for (int i = 0; i < temp.length; ++i) {
                groupNames[i + 1] = temp[i];
            }

        } else {
            //no groups
            groupNames = new String[]{"<New>"};
        }
        if (groupList != null) {
            //update our model for our list display
            groupList.setModel(new javax.swing.AbstractListModel() {
                String[] strings = groupNames;

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

            //search our groups and select the one with matching name to
            // whatever group the user is working on
            if (editingFilt != null) {
                int res = Arrays.binarySearch(groupNames, editingFilt.getName());
                if (res >= 0) {
                    groupList.setSelectedIndex(res);
                } else {
                    groupList.setSelectedIndex(0);
                }
            } else {
                //they are working on a new group
                groupList.setSelectedIndex(0);
            }
        }


    }

    /**
     * Sets the displayed fields to be equal to the data for the group
     */
    private void setFields() {
        if (editingFilt != null) {

            groupNameBox.setText(editingFilt.getName());
            groupWeightBox.setValue(editingFilt.getWeight());
        }

    }

    /**
     * Realize changes in the data in the field to the actual object
     */
    private void transferChanges() {
        //brand new group
        if (editingFilt == null) {
            editingFilt = new Group(manager.getUId(), "", 0, null);
            //original = (Group) editingFilt.clone();
        }

        if (groupList.getSelectedIndex() == 0) {
            editingFilt.setKey(null);
            editingFilt.setId(-1);
        }

        //there is a name to use
        if (groupNameBox.getText() != null
                && !groupNameBox.getText().equals("")) {

            editingFilt.updateName(groupNameBox.getText());
        }

        try {
            editingFilt.updateWeight(Integer.parseInt(groupWeightBox.getText()));
        } catch (Exception e) {
            //they didn't enter a number and java formated text field sucks
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        groupList = new javax.swing.JList();
        groupNameBox = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        groupWeightBox = new javax.swing.JFormattedTextField();
        updateButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        deleteButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        successLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sequencer: Groups");
        setResizable(false);

        groupList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = groupNames;

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });
        groupList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        groupList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                groupListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(groupList);

        groupNameBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                groupNameBoxFocusLost(evt);
            }
        });

        jLabel1.setText("Weight:");

        groupWeightBox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        groupWeightBox.setText("0");
        groupWeightBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                groupWeightBoxActionPerformed(evt);
            }
        });

        updateButton.setText("Update");
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Okay");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("?");
        jLabel2.setToolTipText("<html>\n\n<p> Adds this relative weight to all activities in this group</p>\n<p>-----------------------------------------------------------------</p>\n<p>If forced to choose between two activites, Sequencer<br>\n       will select the activity with the higher total weight.</p>\n<p>-----------------------------------------------------------------</p>\n\n\n\n\n</html>");

        deleteButton.setText("Delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        jLabel3.setText("Name:");

        successLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        successLabel.setText("Success!");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(groupNameBox))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(groupWeightBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(deleteButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(updateButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(successLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                                .addComponent(cancelButton)))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(groupNameBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(groupWeightBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(updateButton)
                    .addComponent(cancelButton)
                    .addComponent(deleteButton)
                    .addComponent(successLabel))
                .addContainerGap())
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Check to make sure there are no unsaved changes and exit
     * @param evt 
     */
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        int indx = groupList.getSelectedIndex() - 1;

        transferChanges();

        int n = 0;
        //see if updated
        if (!groupNameBox.getText().equals("")
                && (editingFilt != null
                && !editingFilt.equals(original))) {
            //request confirmation of swtiching with unwritten changes
            //all done
            n = confirmDiscard();
        }

        //they want to exit
        if (n == 0) {

            this.dispose();
        }
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * Do an update when the user requests it
     * @param evt 
     */
    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        successLabel.setVisible(false);

        if (groupNameBox.getText() == null
                || groupNameBox.getText().equals("")) {
            //we don't want to do anything, no name set
            JOptionPane.showMessageDialog(this,
                    "No group name specified. Update failed.",
                    "Missing name",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        transferChanges();

        if ((editingFilt != null
                && !editingFilt.equals(original))) {

            int n = 0;
            if (original != null
                    && !editingFilt.getName().equals(original.getName())
                    && original.getName() != null) {

                //have a name, name differs, and not changing index
                Object[] options = {"Yes", "Cancel"};
                n = JOptionPane.showOptionDialog(this,
                        "This will edit the name of the current activity.\n"
                        + "Are you sure you wish to continue?",
                        "Activity name change",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[1]);
            }
            //they want to change the name
            if (n == 0) {
                //write local object to db & update user
                manager.updateGroup(editingFilt);
                original = (Group) editingFilt.clone();
                //update our display
                getGroups();
                successLabel.setVisible(true);
            }
        } else {
            successLabel.setVisible(true);
        }

    }//GEN-LAST:event_updateButtonActionPerformed

    /**
     * Confirm and perform deletions on user request
     * @param evt 
     */
    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        //confirm delete

        //do the deletion
        int indx = groupList.getSelectedIndex() - 1;
        //not a new activity
        if (indx != -1) {
            
            Object[] options = {"Delete", "Cancel"};
            int n = JOptionPane.showOptionDialog(this,
                    "Are you sure you want to delete?",
                    "Deleting activity",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[1]);

            if (n == 0) {
                manager.removeGroup(editingFilt);
                getGroups();
            }
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    /**
     * Update displayed data when the user selects a different activity
     * @param evt 
     */
    private void groupListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_groupListValueChanged

        int indx = groupList.getSelectedIndex() - 1;
        //System.out.println("old: " + oldIndx + " new: " + (indx + 1));
        if (indx + 1 == oldIndx) {
            return;
        }

        //System.out.println(editingAct);

        transferChanges();
        //System.out.println(editingAct);

        int n = 0;
        //see if updated
        if (!groupNameBox.getText().equals("")
                && (editingFilt != null
                && !editingFilt.equals(original))) {
            //request confirmation of swtiching with unwritten changes
            //all done
            n = confirmDiscard();
        }

        //they wanted to go ahead and change
        if (n == 0) {

            if (indx == -1) {
                editingFilt = new Group(manager.getUId(), "", 0, null);
                original = (Group) editingFilt.clone();
            } else if (indx >= 0) {
                original = manager.getGroup(groupNames[indx + 1]);

                editingFilt = (Group) original.clone();
            }
            setFields();
            oldIndx = indx + 1;
            successLabel.setVisible(false);
        } else {
            groupList.setSelectedIndex(oldIndx);
        }
    }//GEN-LAST:event_groupListValueChanged

    private void groupNameBoxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_groupNameBoxFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_groupNameBoxFocusLost

    private void groupWeightBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_groupWeightBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_groupWeightBoxActionPerformed

    /**
     * Popup confirmation of unsaved changes being discarded
     * @return 
     */
    private int confirmDiscard() {
        Object[] options = {"Yes", "Cancel"};
        int n = JOptionPane.showOptionDialog(this,
                "You have unsaved changes. Are you sure you want to discard?",
                "Unsaved changes!",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);

        return n;
    }

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ActivityGroupBox.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ActivityGroupBox.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ActivityGroupBox.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ActivityGroupBox.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ActivityGroupBox().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JList groupList;
    private javax.swing.JTextField groupNameBox;
    private javax.swing.JFormattedTextField groupWeightBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel successLabel;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables
    //private Integer userId;
    //private Group[] groups;
    private Group editingFilt;
    private Group original;
    private String[] groupNames;
    private DataManager manager;
    private int oldIndx;
}
