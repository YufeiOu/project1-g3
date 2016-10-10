This is the directory containing strategy files that we designed to project 1 of the Programming & Problem Solving course http://www.cs.columbia.edu/~kar/4444f16/

Notes are kept here: https://docs.google.com/document/d/1ncGg92R6kP-Ya_YtXm7ODJnehLG-UOQpZ64JcT7iNAk/edit

The files themselves are not meaningful unless they are compiled together with the simulator, for those who have the simulator and want to change/add files under this directory, please keep in mind we should rename this "project1-g3" to "g3" under your local directory ./pentos/

Tips to help you run/compile the code:

To run or compile the simulator, cd into the folder above pentos

To (re)compile the simulator on Unix & Mac OS X:   

    $ javac pentos/sim/*.java

To (re)compile the simulator on Windows:          

    $ javac pentos\sim\*.java

To run the simulator:  

    $ java pentos.sim.Simulator <arguments>
    
The simulator arguments are:
    
    -g, --groups <group name, e.g. g0>
    
    -s, --sequencer <folder name containing sequencer, e.g. random>
        --gui
        --verbose
        
For example, we should usually run: 

    $ java pentos.sim.Simulator -g g3 --gui

Update: after we tried to improve our strategy, we found the following facts that really matter

------------------------ Final report begins -----------------------------------
Architectural design:
1) No initialized hardcode road or park/water.
2) Resident and factory: search all possible buildable position (rotation included) and choose the best one according to our "building objective function".
3) Road: determined by "findShortestRoad" after a resident/factory is built.
4) to 2), it is possible a buildable position is never reached by any road, in this situation, we abandon this choice and find a new position.
5) Once the building and road are chosen, they are fixed in this Move, after that, we begin to care about the water and/or park.
6) We search a group of possible potential sets of water cells (could be empty), and use our "water objective function" to choose the best one.
7) Once the water is fixed, we search a group of possible sets of park cells (could also be empty), and use our "park objective function" to choose the best one.
When those steps finished, a Move is determined, we build them and then jump to the next turn.

Now we want to explain both in theorm and intuition why the above designs work.
First, let's start from the simple one: design rule 4), an alternative way to eliminate rule 4) is to add road checking to rule 2), the "building objective function", if a buildable position is detached from any roads, we give its objective function a worst score. That is theorematic correct but ineffcient in reality, for it cost too much to compute the shortest road of each position. Practically, we first pick the best 10 buidable positions and choose one if the "findShortestRoad" is applicable. We guarantee in our algorithm with high probability at least one of them is not detached from the road, in other words, we will explain our algorithm leaves very few "trapped holes".

Second, let's explain our main observation:

Third, let's talk about the magic "building objective function":

Here are the milestones that we planned to achieve:
// TO DO: explain our plan, our tries, what's bad and what's good, a time table of all our efforts that have been made

Experimental results:
// TO DO: talk about each milestones what we achieve, benchmark them
















