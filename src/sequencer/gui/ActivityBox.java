/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequencer.gui;

import java.util.Arrays;
import javax.swing.JOptionPane;
import sequencer.db.Activity;
import sequencer.db.Group;
import sequencer.db.util.DataManager;
import sequencer.db.util.MongoHelper;

/**
 * This gui manages the user's activities
 * 
 * @author Daniel Rogers
 */
public class ActivityBox extends javax.swing.JDialog {

    /**
     * Creates new form ActivityBox
     */
    private ActivityBox() {

        initComponents();
    }

    /**
     *
     * @param manager
     */
    public ActivityBox(DataManager manager) {
        //userId = user;
        this.manager = manager;
        getActivities();
        initComponents();

        oldIndx = 0;
        activityList.setSelectedIndex(0);
        successLabel.setVisible(false);
        setLocationRelativeTo(null);
    }

    /**
     * Update the listmodel for displaying the user's activities
     */
    private void getActivities() {


        String[] temp = manager.getActivityNames();
        activityNames = new String[temp.length + 1];
        activityNames[0] = "<New>";
        for (int i = 0; i < temp.length; ++i) {
            activityNames[i + 1] = temp[i];
        }

        if (activityList != null) {
            activityList.setModel(new javax.swing.AbstractListModel() {
                String[] strings = activityNames;

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

            if (editingAct != null) {
                int res = Arrays.binarySearch(activityNames, editingAct.getName());
                if (res >= 0) {
                    activityList.setSelectedIndex(res);
                } else {
                    activityList.setSelectedIndex(0);
                }
            } else {
                activityList.setSelectedIndex(0);
            }
        }


    }

    /**
     * Update all of the fields to represent the data in the activity that the
     * user has currently selected
     */
    private void setFields() {
        if (editingAct != null) {
            //show the name
            activityNameBox.setText(editingAct.getName());
            //match up day checkboxes to activity days
            boolean[] tempDays = editingAct.getDays();
            if (tempDays != null) {
                if (tempDays[0]) {
                    suCkBx.setSelected(true);
                } else {
                    suCkBx.setSelected(false);
                }
                if (tempDays[1]) {
                    mCkBx.setSelected(true);
                } else {
                    mCkBx.setSelected(false);
                }
                if (tempDays[2]) {
                    tCkBx.setSelected(true);
                } else {
                    tCkBx.setSelected(false);
                }
                if (tempDays[3]) {
                    wCkBx.setSelected(true);
                } else {
                    wCkBx.setSelected(false);
                }
                if (tempDays[4]) {
                    trCkBx.setSelected(true);
                } else {
                    trCkBx.setSelected(false);
                }
                if (tempDays[5]) {
                    fCkBx.setSelected(true);
                } else {
                    fCkBx.setSelected(false);
                }
                if (tempDays[6]) {
                    saCkBx.setSelected(true);
                } else {
                    saCkBx.setSelected(false);
                }
            }
            //set tstart
            int[] tempT = editingAct.getTimeStart();
            //check for pm
            if (tempT[0] == 12) {
                //12pm
                strtHrCoBx.setSelectedIndex(11);
                startAmPmGrp.setSelected(tsPm.getModel(), true);
            } else if (tempT[0] > 12) {
                //afternoon
                strtHrCoBx.setSelectedIndex(tempT[0] - 13);
                startAmPmGrp.setSelected(tsPm.getModel(), true);
            } else if (tempT[0] == 0) {
                //12am
                strtHrCoBx.setSelectedIndex(11);
                startAmPmGrp.setSelected(tsAm.getModel(), true);
            } else {
                //in the am
                strtHrCoBx.setSelectedIndex(tempT[0] - 1);
                startAmPmGrp.setSelected(tsAm.getModel(), true);
            }
            //set the selected index to the appropriate number of minutes
            strtMinCoBx.setSelectedIndex(tempT[1] / 5);

            //tempT = null;
            //set time end
            tempT = editingAct.getTimeEnd();

            //System.out.println("preload" + Arrays.toString(tempT));
            //check for pm
            if (tempT[0] == 12) {
                //12pm
                endHrCoBx.setSelectedIndex(11);
                endAmPmGrp.setSelected(tePm.getModel(), true);
            } else if (tempT[0] > 12) {
                //indexing starts at 0
                //in the afternoon
                endHrCoBx.setSelectedIndex(tempT[0] - 13);
                endAmPmGrp.setSelected(tePm.getModel(), true);
            } else if (tempT[0] == 0) {
                //12am
                endHrCoBx.setSelectedIndex(11);
                endAmPmGrp.setSelected(teAm.getModel(), true);
            } else {
                //in the am, do nothing special
                endHrCoBx.setSelectedIndex(tempT[0] - 1);
                endAmPmGrp.setSelected(teAm.getModel(), true);
            }

            endMinCoBx.setSelectedIndex(tempT[1] / 5);

            //display the name of the active group
            Group f = manager.getGroupById(editingAct.getGroup());
            if (f != null) {
                //the activity has a group
                groupLabel.setText(f.getName());
            } else {
                groupLabel.setText("Not Set");
            }

            //display set weight
            weightField.setValue(editingAct.getWeight());

            //if it's a required activity
            if (editingAct.isRequired()) {
                reqGrp.setSelected(reqYes.getModel(), true);
            } else {
                reqGrp.setSelected(reqNo.getModel(), true);
            }

        }

        //this.repaint();

    }

    /**
     * Move the changes made in the GUI to the object
     *
     * @param write true if the object should be written to the database, this
     * should only be true when updating the real object in its entirety. This
     * is necessary because of how sequencer links groups to activities.
     */
    private void transferChanges(boolean write) {
        //boolean newact = false;
        if (editingAct == null) {
            editingAct = new Activity(manager.getUId(), null,
                    0, false, null, null, null,
                    0, 0, 0, 0);
            //newact = true;
        }

        //new activity
        if (activityList.getSelectedIndex() == 0) {

            editingAct.setKey(null);
            editingAct.setId(-1);
        }

        //display the name if possible
        if (activityNameBox.getText() != null
                && !activityNameBox.getText().equals("")) {

            editingAct.setName(activityNameBox.getText());
        }
        
        //set days according to the selected check boxes
        boolean[] tempDays = new boolean[7];

        if (suCkBx.isSelected()) {
            tempDays[0] = true;
        }
        if (mCkBx.isSelected()) {
            tempDays[1] = true;
        }
        if (tCkBx.isSelected()) {
            tempDays[2] = true;
        }
        if (wCkBx.isSelected()) {
            tempDays[3] = true;
        }
        if (trCkBx.isSelected()) {
            tempDays[4] = true;
        }
        if (fCkBx.isSelected()) {
            tempDays[5] = true;
        }
        if (saCkBx.isSelected()) {
            tempDays[6] = true;
        }

        editingAct.updateDays(tempDays);

        //set tstart
        int[] tempT = new int[2];
        //check for midnight
        if (strtHrCoBx.getSelectedIndex() == 11
                && tsAm.isSelected()) {
            //hr = 12am = 0hr
            tempT[0] = 0;
        } else if (strtHrCoBx.getSelectedIndex() == 11
                && tsPm.isSelected()) {
            //noon = 12pm = 12hr
            tempT[0] = 12;
        } else if (tsPm.isSelected()) {
            tempT[0] = strtHrCoBx.getSelectedIndex() + 13;
        } else {
            //indexing starts at 0 == hour 1
            tempT[0] = strtHrCoBx.getSelectedIndex() + 1;
        }
        //set minute start, 5 minute increments on index
        tempT[1] = strtMinCoBx.getSelectedIndex() * 5;

        editingAct.setStartTime(tempT[0], tempT[1]);


        //set tend
        //check for midnight
        if (endHrCoBx.getSelectedIndex() == 11
                && teAm.isSelected()) {
            //hr = 12am = 0hr
            tempT[0] = 0;
        } else if (endHrCoBx.getSelectedIndex() == 11
                && tePm.isSelected()) {
            //noon = 12pm = 12hr
            tempT[0] = 12;
        } else if (tePm.isSelected()) {
            tempT[0] = endHrCoBx.getSelectedIndex() + 13;
        } else {
            //indexing starts at 0 == hour 1
            tempT[0] = endHrCoBx.getSelectedIndex() + 1;
        }
        //set minute start
        tempT[1] = endMinCoBx.getSelectedIndex() * 5;

        editingAct.setEndTime(tempT[0], tempT[1]);

        try {
            editingAct.setWeight(Integer.parseInt(weightField.getText()));
        } catch (Exception e) {
           //formatted text field crashed because they entered a non-int, or
           // something too big
        }

        //flag if it's required
        if (reqYes.isSelected()) {
            editingAct.setRequired(true);
        } else {
            editingAct.setRequired(false);
        }

        //if we're writing the physical object, rather than just checking for
        // changes
        if (write) {
            if (editingAct.getId() == -1) {
                //need to save it to get a real id
                MongoHelper.save(editingAct, MongoHelper.ACTIVITY_COLLECTION);
            }

            //set the group
            if (groupLabel.getText() != null
                    && !groupLabel.getText().equals("Not Set")) {
                
                Group f = manager.getGroup(groupLabel.getText());

                if (f != null) {
                    editingAct.setGroup(f.getId());
                    f.addActivity(editingAct.getId());
                    manager.updateGroup(f);
                } else {
                    editingAct.setGroup(null);
                }

            }
        } 
        //We might have a group change, even though we're not writing the
        // physical object
        else if (groupLabel.getText() != null
                && !groupLabel.getText().equals("Not Set")) {
            Group f = manager.getGroup(groupLabel.getText());
            if (f != null) {
                //update the activity that we're using
                editingAct.setGroup(f.getId());
            } else {
                editingAct.setGroup(null);
            }
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

        startAmPmGrp = new javax.swing.ButtonGroup();
        endAmPmGrp = new javax.swing.ButtonGroup();
        reqGrp = new javax.swing.ButtonGroup();
        jScrollPane3 = new javax.swing.JScrollPane();
        activityList = new javax.swing.JList();
        cancelBtn = new javax.swing.JButton();
        updateBtn = new javax.swing.JButton();
        activityNameBox = new javax.swing.JTextField();
        daysLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        mCkBx = new javax.swing.JCheckBox();
        tCkBx = new javax.swing.JCheckBox();
        wCkBx = new javax.swing.JCheckBox();
        trCkBx = new javax.swing.JCheckBox();
        fCkBx = new javax.swing.JCheckBox();
        saCkBx = new javax.swing.JCheckBox();
        suCkBx = new javax.swing.JCheckBox();
        strtHrCoBx = new javax.swing.JComboBox();
        strtMinCoBx = new javax.swing.JComboBox();
        endHrCoBx = new javax.swing.JComboBox();
        endMinCoBx = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        tsAm = new javax.swing.JRadioButton();
        tsPm = new javax.swing.JRadioButton();
        teAm = new javax.swing.JRadioButton();
        tePm = new javax.swing.JRadioButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        weightField = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        groupLabel = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        reqYes = new javax.swing.JRadioButton();
        reqNo = new javax.swing.JRadioButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel11 = new javax.swing.JLabel();
        deleteButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        successLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sequencer: Activities");
        setResizable(false);

        activityList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = activityNames;
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        activityList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        activityList.setAutoscrolls(false);
        activityList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                activityListValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(activityList);

        cancelBtn.setText("Okay");
        cancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBtnActionPerformed(evt);
            }
        });

        updateBtn.setText("Update");
        updateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateBtnActionPerformed(evt);
            }
        });

        activityNameBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                activityNameBoxFocusLost(evt);
            }
        });

        daysLabel.setText("Days:");

        jLabel3.setText("Time Start:");

        jLabel4.setText("Time End:");

        mCkBx.setText("Mo");

        tCkBx.setText("Tu");

        wCkBx.setText("We");

        trCkBx.setText("Th");

        fCkBx.setText("Fr");

        saCkBx.setText("Sa");

        suCkBx.setText("Su");

        strtHrCoBx.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" }));

        strtMinCoBx.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60" }));

        endHrCoBx.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" }));

        endMinCoBx.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60" }));

        jLabel5.setText(":");

        startAmPmGrp.add(tsAm);
        tsAm.setSelected(true);
        tsAm.setText("am");

        startAmPmGrp.add(tsPm);
        tsPm.setText("pm");

        endAmPmGrp.add(teAm);
        teAm.setSelected(true);
        teAm.setText("am");

        endAmPmGrp.add(tePm);
        tePm.setText("pm");

        jLabel6.setText(":");

        jLabel2.setText("Weight:");

        weightField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        weightField.setText("0");
        weightField.setFocusLostBehavior(javax.swing.JFormattedTextField.PERSIST);

        jLabel7.setText("Group:");

        groupLabel.setText("Not Set");
        groupLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                groupLabelMouseClicked(evt);
            }
        });

        jLabel8.setText("Require?:");

        reqGrp.add(reqYes);
        reqYes.setText("Yes");

        reqGrp.add(reqNo);
        reqNo.setSelected(true);
        reqNo.setText("No");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("?");
        jLabel9.setToolTipText("<html>\n<p>Allows you to use a custom group (Optional)</p>\n<p>&nbsp;&nbsp;&nbsp;&nbsp;Label: Name of the group</p>\n<p>--------------------------------------</p>\n<p>Groups can be created under </p>\n<p>&nbsp;&nbsp;&nbsp;&nbsp;\"Edit -> Groups...\"</p>\n<p>--------------------------------------</p>\n\n</html>");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("?");
        jLabel10.setToolTipText("<html>\n\n<p> Sets a relative base weight for this activity.</p>\n<p>----------------------------------------------</p>\n<p>If forced to choose between two activites,<br>\n       Sequencer will select the activity with the <br>\n       higher base + group weight.</p>\n<p>----------------------------------------------</p>\n\n</html>");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("?");
        jLabel11.setToolTipText("<html>\n<p>Tells Sequencer that this activity must occur in the schedule</p>\n<p>&nbsp;&nbsp;&nbsp;&nbsp;regardless of the relative weight</p>\n</html>");

        deleteButton.setText("Delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Name:");

        successLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        successLabel.setText("Success!");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(daysLabel)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(strtHrCoBx, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(strtMinCoBx, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tsAm)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tsPm))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(endHrCoBx, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(endMinCoBx, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(teAm)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tePm))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(mCkBx)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tCkBx)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(wCkBx)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(trCkBx)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(fCkBx)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(saCkBx)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(suCkBx))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(reqYes)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(reqNo)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel11))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(weightField)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(jLabel10))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(groupLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(jLabel9)))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jSeparator1)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(activityNameBox, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(deleteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(updateBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(successLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cancelBtn)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(activityNameBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(daysLabel)
                    .addComponent(mCkBx)
                    .addComponent(tCkBx)
                    .addComponent(wCkBx)
                    .addComponent(trCkBx)
                    .addComponent(fCkBx)
                    .addComponent(saCkBx)
                    .addComponent(suCkBx))
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(strtHrCoBx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(strtMinCoBx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tsAm)
                    .addComponent(tsPm))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(endMinCoBx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(endHrCoBx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(teAm)
                    .addComponent(tePm))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(groupLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(weightField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(reqYes)
                    .addComponent(reqNo)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelBtn)
                    .addComponent(updateBtn)
                    .addComponent(deleteButton)
                    .addComponent(successLabel))
                .addContainerGap())
            .addComponent(jScrollPane3)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void groupInfoBox(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_groupInfoBox
        // TODO add your handling code here:
    }//GEN-LAST:event_groupInfoBox

    private void groupLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_groupLabelMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_groupLabelMouseClicked

    private void jLabel11groupInfoBox(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel11groupInfoBox
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel11groupInfoBox

    /**
     * Check for unsaved changes and try to exit this screen
     *
     * @param evt
     */
    private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtnActionPerformed
        int indx = activityList.getSelectedIndex() - 1;
        transferChanges(false);
        int n = 0;

        //see if updated
        if (!activityNameBox.getText().equals("")
                && (editingAct != null
                && !editingAct.equals(original))) {
            //request confirmation of cancel
            n = confirmDiscard();
        }
        if (n == 0) {
            this.dispose();
        }
    }//GEN-LAST:event_cancelBtnActionPerformed

    /**
     * Do an update, moving changes to a real db object
     *
     * @param evt
     */
    private void updateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateBtnActionPerformed
        successLabel.setVisible(false);
        if (activityNameBox.getText() == null
                || activityNameBox.getText().equals("")) {
            //we don't want to do anything

            //custom title, warning icon
            JOptionPane.showMessageDialog(this,
                    "No activity name specified. Update failed.",
                    "Missing name",
                    JOptionPane.WARNING_MESSAGE);

            return;
        }

        transferChanges(true);

        int indx = activityList.getSelectedIndex() - 1;
        //.out.println(editingAct);
        //if(indx >= 0) System.out.println(activities[indx]);

        if ((editingAct != null
                && !editingAct.equals(original))) {

            int n = 0;
            //System.out.println(original);
            if (original != null
                    && !editingAct.getName().equals(original.getName())
                    && original.getName() != null) {

                //System.out.println(editingAct.getName() + " " + oldIndx);
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
            if (n == 0) {

                //write local object to db & update user
                //MongoHelper.save(editingAct, MongoHelper.ACTIVITY_COLLECTION);
                manager.updateActivity(editingAct);
                original = (Activity) editingAct.clone();
                getActivities();

                successLabel.setVisible(true);
            }
        } else {
            successLabel.setVisible(true);
        }

    }//GEN-LAST:event_updateBtnActionPerformed

    /**
     * Do deletions on user request, and confirm deletion
     *
     * @param evt
     */
    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        //confirm delete

        //do the deletion
        int indx = activityList.getSelectedIndex() - 1;
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
                manager.removeActivity(editingAct);

                /*User u = (User) MongoHelper.fetch(new User(userId),
                 MongoHelper.USER_COLLECTION);

                 u.removeActivity(activities[indx].getId());

                 MongoHelper.save(u, MongoHelper.USER_COLLECTION);
                 activities[indx].cleanUp();*/
                getActivities();
            }
        }
    }//GEN-LAST:event_deleteButtonActionPerformed
    private void activityNameBoxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_activityNameBoxFocusLost
    }//GEN-LAST:event_activityNameBoxFocusLost

    /**
     * Handle updating of fields to match a newly selected activity. Also
     * confirm discarding of unsaved changes
     *
     * @param evt
     */
    private void activityListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_activityListValueChanged

        int indx = activityList.getSelectedIndex() - 1;
        if (indx + 1 == oldIndx) {
            return;
        }

        //System.out.println(editingAct);

        transferChanges(false);
        //System.out.println(editingAct);

        int n = 0;
        //see if updated
        if (!activityNameBox.getText().equals("")
                && (editingAct != null
                && !editingAct.equals(original))) {
            //System.out.println(editingAct);
            //System.out.println(original);
            //request confirmation of swtiching with unwritten changes
            //System.out.println("switch from changes");
            //all done
            n = confirmDiscard();
        }

        if (n == 0) {
            if (indx == -1) {
                editingAct = new Activity(manager.getUId(), null,
                        0, false, null, null, null,
                        0, 0, 0, 0);

                original = (Activity) editingAct.clone();
            } else if (indx >= 0) {
                //&& !editingAct.getName().equals(getActivityNames[indx])) {
                original = manager.getActivity(activityNames[indx + 1]);
                editingAct = (Activity) original.clone();
            }
            //System.out.println("editset: " + editingAct.getWeight());

            //System.out.println("lst_precall " + indx);
            oldIndx = indx + 1;
            setFields();
            successLabel.setVisible(false);
        } else {
            activityList.setSelectedIndex(oldIndx);
        }
    }//GEN-LAST:event_activityListValueChanged

    /**
     * popup confirmation of unsaved changes about to be discareded
     *
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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ActivityBox.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ActivityBox().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList activityList;
    private javax.swing.JTextField activityNameBox;
    private javax.swing.JButton cancelBtn;
    private javax.swing.JLabel daysLabel;
    private javax.swing.JButton deleteButton;
    private javax.swing.ButtonGroup endAmPmGrp;
    private javax.swing.JComboBox endHrCoBx;
    private javax.swing.JComboBox endMinCoBx;
    private javax.swing.JCheckBox fCkBx;
    private javax.swing.JTextField groupLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JCheckBox mCkBx;
    private javax.swing.ButtonGroup reqGrp;
    private javax.swing.JRadioButton reqNo;
    private javax.swing.JRadioButton reqYes;
    private javax.swing.JCheckBox saCkBx;
    private javax.swing.ButtonGroup startAmPmGrp;
    private javax.swing.JComboBox strtHrCoBx;
    private javax.swing.JComboBox strtMinCoBx;
    private javax.swing.JCheckBox suCkBx;
    private javax.swing.JLabel successLabel;
    private javax.swing.JCheckBox tCkBx;
    private javax.swing.JRadioButton teAm;
    private javax.swing.JRadioButton tePm;
    private javax.swing.JCheckBox trCkBx;
    private javax.swing.JRadioButton tsAm;
    private javax.swing.JRadioButton tsPm;
    private javax.swing.JButton updateBtn;
    private javax.swing.JCheckBox wCkBx;
    private javax.swing.JFormattedTextField weightField;
    // End of variables declaration//GEN-END:variables
    //private Integer userId;
    //private Activity[] activities;
    private Activity editingAct;
    private Activity original;
    private String[] activityNames;
    private DataManager manager;
    private int oldIndx;
}
