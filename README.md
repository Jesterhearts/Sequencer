Sequencer
=========

Capping project

This was my CS capping project.
It allows a user to specify events which have:
  - time(s) they occur
  - a preference weight which indicates how strongly the user desires it
    - higher is more important
  - a flag indicating if it is required
    - This tells the scheduler that the event MUST be in the schedule

Once the desired list of events has been created, the program will assemble
the highest value schedule possible with the events, while including all
required events.
  
