This is the directory containing strategy files that we designed to project 1 of the Programming & Problem Solving course http://www.cs.columbia.edu/~kar/4444f16/
The files themselves are not meaningful unless they are compiled together with the simulator, for those who have the simulator and want to change/add files under this directory, please keep in mind we should rename this "project1-g3" to "g3" under your local directory ./pentos/
Tips to help you run/compile the code:

To run or compile the simulator, cd into the folder above pentos
To (re)compile the simulator on Unix & Mac OS X:   javac pentos/sim/*.java
To (re)compile the simulator on Windows:           javac pentos\sim\*.java

To run the simulator:  java pentos.sim.Simulator <arguments>
The simulator arguments are:
    -g, --groups <group name, e.g. g0>
    -s, --sequencer <folder name containing sequencer, e.g. random>
        --gui
        --verbose
For example, we should usually run: java pentos.sim.Simulator -g g3 --gui
