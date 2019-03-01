# Scheduler-Simulator
=======
How to compile and run Assignment 1 from command line:
1) Extract source code from compressed file.
2) Make sure java jdk is installed correctly, "java -version" to check. (Version of java I am running: java version "1.8.0_171"
                                                                            java(TM) SE Runtime Environment (build 1.8.0_171-b11)
                                                                            Java HotSpot(TM) 64-Bit Server VM (build 25.171-b11, mixed mode))
3) Change current directory to directory where source code file is located in using "cd <path-to-directory>".                                                                             
3) To compile: "javac SchedulerSimulator.java", or whatever the file name is. This will create 3 .class files.
4) To run: "java SchedulerSimulator".
5) The program will ask you to enter the testdata.txt file as the data input.
6) The program will then iteratively print through each scheduler using the data from the file.
   It will as you if you want to save the scheduler to a file. Type 'y' or 'n' based on your choice. 
6a) If 'y' was typed it will ask you for the name of the file you want to save the data as.
7) After going through each scheduler, the total and average turnaround time will print for each scheduler, then the program is terminated.
