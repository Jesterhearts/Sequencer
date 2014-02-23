/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequencer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import sequencer.db.Activity;
import sequencer.db.Schedule;
import sequencer.db.util.DataManager;
import sequencer.gui.GeneratorWindow;

/**
 * This class handles the generation from a set of activities.
 *
 * @author Daniel Rogers
 */
public class GenerationEngine extends SwingWorker<Void, Void> {

    private GeneratorWindow window;
    private int continueWarning;
    private String[] options;
    private Schedule[] schedules;
    private DataManager manager;
    private int numSchedules;

    /**
     * This creates a basic instance of this class needed to do the actual
     * generation work asynchronously
     *
     * @param g The window that spawns this class, it will be used as the parent
     * of warning windows notifying the user of conflicts in req. activities
     * @param m The datamanager responsible for handling access to the user's
     * activities
     * @param scheds The desired number of schedules to generate
     */
    public GenerationEngine(GeneratorWindow g, DataManager m, int scheds) {
        window = g;
        continueWarning = 0;
        options = new String[]{"Yes", "No"};
        manager = m;
        numSchedules = scheds;
        //progress = 0;
    }

    /**
     * This contains the actual algorithm for generating the schedules When this
     * finishes, the schedule list can be obtained using getSchedules
     *
     * @return null
     * @throws Exception
     */
    @Override
    protected Void doInBackground() throws Exception {
        int progress = 0;
        setProgress(0);

        //get a list of activities sorted by time
        Activity[] acts = manager.getActivities();
        //used to flag if a set will remove a required activity
        boolean[] removesReq = new boolean[acts.length];
        //store weight lost for a particular set of deletions
        int[][] removedWeight = new int[acts.length][2];

        //hold our sets of deletions to permit a particular activity
        ArrayList<HashSet<Activity>> removalSet = new ArrayList<>();
        //used to store the deletions for the examined activity
        HashSet<Activity> deletions = new HashSet<>();
        HashSet<Activity> temp;

        //stores the result of looking up if an activity conflicts
        int confRes;

        for (int i = 0; i < acts.length; ++i) {
            //reset for our currently examining activity
            deletions.clear();
            //update index mapping to weight removed
            removedWeight[i][1] = i;

            for (int x = i - 1; x >= 0; --x) {
                //see if they conflict
                confRes = conflicts(acts[x], acts[i]);

                //occur on same day with overlapping time
                if (confRes == 2) {
                    //we would need to delete a[x] to include a[i]
                    deletions.add(acts[x]);
                    removedWeight[i][0] += acts[x].getRealWeight();
                    //we would also need to delete a[i] to include a[x]
                    removalSet.get(x).add(acts[i]);
                    removedWeight[x][0] += acts[i].getRealWeight();

                    if (acts[x].isRequired()) {
                        //would have to delete a required activity
                        removesReq[i] = true;
                    }

                    if (!removesReq[x] && acts[i].isRequired()) {
                        //would have to delete a[i], a required activity
                        removesReq[x] = true;
                    }
                } //occur same day with non-overlapping time
                else if (confRes == 1) {
                    //since our activities are ordered by end time and
                    //we are iterating through in reverse and these activities
                    //occured on the same day but their times do not overlap
                    //we know that no other activities will occur
                    break;
                }
            }
            //add the set of deletions that a[i] would have to perform
            removalSet.add((HashSet<Activity>) deletions.clone());

            //update progress bar
            progress = ((i + 1) * 50) / acts.length;
            setProgress(Math.min(progress, 100));
        }

        //sort the arrays based on the ammount of weight removed
        // removedWeight[1] will have the index of the removalSet
        // required to produce that weight
        Arrays.sort(removedWeight, new IntArray2D());

        schedules = new Schedule[numSchedules];
        //clear our list to prepair
        deletions.clear();

        for (int s = 0; s < schedules.length; ++s) {

            //make a new schedule with a defaulted name
            schedules[s] = new Schedule(manager.getUId(), "Schedule " + (s + 1),
                    null);

            //if our current activities do not conflict with the activity
            //    and it doesn't remove a required activity, add it to our
            //    schedule
            //This adds in all non-required activities that do not override a
            //   required activity, and adds in the best activities first
            //This will also add in all required activities that do not conflict
            //   with other required activities
            for (int x = 0; x < acts.length; ++x) {
                if (!deletions.contains(acts[removedWeight[x][1]])
                        && !removesReq[removedWeight[x][1]]) {

                    schedules[s].addActivity(acts[removedWeight[x][1]].getId());
                    deletions.addAll(removalSet.get(removedWeight[x][1]));
                }
            }

            //If our activity does conflict with a required activity
            //    and it does not conflict with an activity we've already added
            //    add it to our schedule
            for (int x = 0; x < acts.length; ++x) {
                if (removesReq[removedWeight[x][1]]
                        && !deletions.contains(acts[removedWeight[x][1]])) {

                    schedules[s].addActivity(acts[removedWeight[x][1]].getId());
                    deletions.addAll(removalSet.get(removedWeight[x][1]));
                }
            }

            //update progress bar
            progress = 50 + (((s + 1) * 50) / schedules.length);
            setProgress(Math.min(progress, 100));
        }

        return null;

    }

    /**
     * Checks if two activities conflict. It will try to generate a warning
     * window to the user informing them if this happens.
     *
     * @param a An activity to compare to b
     * @param b An activity to compare to a
     *
     * @return 0 if no conflict in day - 1 if same day but no time conflict - 2
     * if same day and time conflicts
     */
    private int conflicts(Activity a, Activity b) {
        if (a == null || b == null) {
            return 0;
        }

        if (a.equals(b)) {
            return 2;
        }

        //see if they even happen on the same day
        boolean dayconflict = false;
        for (int x = 0; x < 7; ++x) {
            if (a.getDays()[x] && b.getDays()[x]) {
                dayconflict = true;
                break;
            }
        }
        if (!dayconflict) {
            return 0;
        }

        //now check time
        //if hour end 1 after hour start 2
        if ((b.getTimeEnd()[0] > a.getTimeStart()[0]
                //and hour start 1 is before hour end 2
                && b.getTimeStart()[0] < a.getTimeEnd()[0])
                //OR
                //end hour 1 is same as start hour 2
                || (b.getTimeEnd()[0] == a.getTimeStart()[0]
                //and end minute 1 is after start minute 2
                && b.getTimeEnd()[1] > a.getTimeStart()[1])
                //OR
                //start hour 1 is same as end hour 2
                || (b.getTimeStart()[0] == a.getTimeEnd()[0]
                //and start minute 1 is before start minute 2
                && b.getTimeStart()[1] < a.getTimeEnd()[1])) {

            if (continueWarning == 0
                    && b.isRequired()
                    && a.isRequired()) {
                //conflict in required activities, warn user
                continueWarning = JOptionPane.showOptionDialog(window,
                        "There is a conflict in required activities: "
                        + b.getName() + " and " + a.getName()
                        + "\n Scheduler will use the required activity with the"
                        + " higher weight.\n\n"
                        + "Do you wish to continue being warned about conflicts?",
                        "Conflict Warning",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        options,
                        options[1]);
            }
            return 2;
        }

        return 1;

    }

    /**
     * Gets the schedules generated by this generator
     *
     * @return null if no schedules have been generated. Otherwise a array
     * containing the best schedules in descending order.
     */
    public Schedule[] getSchedules() {
        return schedules;
    }

    /**
     * This exists to allow sorting of int[][] using Arrays.sort(), It will
     *      sort based on int[x][0]
     */
    private class IntArray2D implements Comparator<int[]> {

        @Override
        public int compare(int[] lhs, int[] rhs) {
            return lhs[0] - rhs[0];
        }
    }
}