/*
 * Michael Trinh
 * CS 4310: Operating Systems\
 * Professor Atanasio
 * Assignment 1: Scheduler Simulator
 * Description: Simulates an operating system scheduler by reading processes 
 *     from test data files, where the first line is id of the process, 
 *     the second is the burst time that the process requires, the third line
 *     will be the priority of the process. 
 *     The priority is only to be used in the lottery scheduler.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

/**
 *
 * @author Michael Trinh
 */
public class SchedulerSimulator {
    public final int switchCost;
    public final int numberOfAlgorithms;
    // ok since 1 algorithm uses totalPriority
    public int totalPriority;
    public int totalTurnaroutTime;
    public float averageTurnaroundTime;
    private final List<Process> processList;
    private Queue<Process> processQueue;
    private List<ScheduleColumn> scheduler;
    private List<Integer> totalTurnaroundTimeList;
    private List<Float> averageTurnaroundTimeList;
    
    public SchedulerSimulator() throws FileNotFoundException {
        switchCost = 3;
        numberOfAlgorithms = 5;
        totalPriority = 0;
        processList = new ArrayList<>();
        totalTurnaroundTimeList = new ArrayList<>();
        averageTurnaroundTimeList = new ArrayList<>();
        initializeInput(); // load the test/input data
        printProcesses(processList); // prints input data
        iterateSchedulers(); // iterates through each of the scheduler's algorithms
        printTurnarounds(); // prints total and avg turnaround times of each scheduler
    }
    
    private void initializeInput(){
        System.out.print("Enter test data file name: ");
        Scanner input = new Scanner(System.in);
        readFile(input.nextLine());
    }
    
    private void iterateSchedulers() throws FileNotFoundException{
        Scanner input;
        // iterate through each algorithm
        for(int i = 1; i <= numberOfAlgorithms; i++){
            resetVariables(); // resets variables for next scheduler
            System.out.println("\n Algorithm " + i + ": ");
            initializeProcessQueue(i);
            schedulingAlgorithm(i);
            totalTurnaroundTimeList.add(totalTurnaroutTime);
            printScheduler(); // also calculates average turnaround time
            averageTurnaroundTimeList.add(averageTurnaroundTime);
            // asks user if they want to save output
            System.out.print("\n Save algorithm " + i + "? 'y' for yes, 'n' for no. ");
            input = new Scanner(System.in);
            if(input.nextLine().charAt(0) == 'y') 
                saveAsFile(i);
        }
    }
    
    private void resetVariables() {
            totalTurnaroutTime = 0;
            averageTurnaroundTime = 0;
            processQueue = new ArrayDeque<>();
            scheduler = new ArrayList<>();
    }
    
    private void readFile(String fileName) {
        File file = new File(fileName);
        try(Scanner fileScanner = new Scanner(file)){
            Process process;
            while(fileScanner.hasNextLine()){
                process = new Process();
                process.id = Integer.parseInt(fileScanner.nextLine());
                process.burstTime = Integer.parseInt(fileScanner.nextLine());
                process.priority = Integer.parseInt(fileScanner.nextLine());
                processList.add(process);
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }
    
    private void saveAsFile(int algorithm) throws FileNotFoundException {
        // Name each output file: scheduler_name-testfile_name.csv
        // Your output must be as a CSV file.
        System.out.print("Enter filename for algorithm " + algorithm + ": ");
        Scanner input = new Scanner(System.in);
        String fileName = input.nextLine();
        try (PrintWriter pw = new PrintWriter(fileName)) {
            pw.write("CpuTime,PID,StartingBurstTime,EndingBurstTime,CompletionTime \n");
            for(ScheduleColumn s: scheduler){
                pw.write(Integer.toString(s.cpuTime) + ","
                    + Integer.toString(s.pid) + ","
                    + Integer.toString(s.startBT) + ","
                    + Integer.toString(s.endBT) + ","
                    + Integer.toString(s.completionTime) + "\n");
            }
        }
    }
    
    public class Process {
        public int id;
        private int burstTime;
        public int priority;
        private int startRange;
        private int endRange;
        
        public Process() {}
        
        public Process(int id, int burstTime, int priority) {
            this.id = id;
            this.burstTime = burstTime;
            this.priority = priority;
        }
        
        // used to set a range of tickets for lottery scheduling
        private void setRange(int startRange, int endRange){
            this.startRange = startRange;
            this.endRange = endRange;
        }

        public void printProcess(){
            System.out.print("ID: " + this.id);
            System.out.print("\tBurst Time: " + this.burstTime);
            System.out.println("\tPriority: " + this.priority);
        }
    }
    
    public class ScheduleColumn {
        public int cpuTime;
        public int pid;
        public int startBT;
        public int endBT;
        public int completionTime;

        public ScheduleColumn(int cpuTime, int pid, int startBT, int endBT, int completionTime) {
            this.cpuTime = cpuTime;
            this.pid = pid;
            this.startBT = startBT;
            this.endBT = endBT;
            this.completionTime = completionTime;
        }
        
        public void printScheduleColumn(){
            System.out.print("CPUTime: " + this.cpuTime);
            System.out.print("\tPID: " + this.pid);
            System.out.print("\tStartingBurstTime: " + this.startBT);
            System.out.print("\tEndingBurstTime: " + this.endBT);
            System.out.println("\tCompletionTime: " + this.completionTime);
        }
    }
    
    public final void printProcesses(Collection<Process> processes){
        System.out.println("Processes: ");
        processes.forEach((process) -> { 
            process.printProcess();
        });
    }
    
    public final void printScheduler(){
        float temp = scheduler.size(); // used to calculate average turnaround time
        for(ScheduleColumn scheduleColumn:scheduler){
            if(scheduleColumn.completionTime == 0) temp--;
            scheduleColumn.printScheduleColumn();
        }
        averageTurnaroundTime = (float)totalTurnaroutTime / temp;
    }
    
    // prints total and avg turnaround time for each of the schedulers
    public final void printTurnarounds(){
        for(int i = 0; i < totalTurnaroundTimeList.size(); i++){
            System.out.println("\n algorithm " + (i+1) + ": ");
            System.out.println("Total Turnaround Time: " + totalTurnaroundTimeList.get(i));
            System.out.println("Average Turnaround Time: " + averageTurnaroundTimeList.get(i));
        }
    }
    
    /**
     * @param algorithm scheduling algorithm number 
     * 2) shortest job first ordered by burst time ascending
     * 5) lottery ordered during real time
     * 1,3,4) other algorithms ordered by id ascending
     */
    private void initializeProcessQueue(int algorithm){
        if(algorithm == 2) {
            List<Process>list = new ArrayList<>();
            processList.forEach((process) -> { 
                list.add(process); 
            });
            list.sort((Process p1, Process p2) -> (p1.burstTime-p2.burstTime));
            processQueue.addAll(list);
        } else if(algorithm != 5) // case 1,3,4 = FCFS
            processQueue.addAll(processList);
    }
    
    /**
     * @param algorithm scheduling algorithm number 
     * 1) First-Come-First-Serve (FCFS)
     * 2) Shortest-Job-First (SJF)
     * 3) Round-Robin with time quantum = 20
     * 4) Round-Robin with time quantum = 40
     * 5) Lottery with time quantum = 40
     */
    private void schedulingAlgorithm(int algorithm){
        int cpuTime = 0;
        int timeQuantum = 40;
        Process process;
        // setup lottery
        if(algorithm == 5){
            initializeLottery(processList);
            addLotteryProcess(processList);
        }
        while(!processQueue.isEmpty()){
            process = processQueue.remove();
            switch(algorithm){
                // intentional no break statement for case 1
                case 1:
                case 2: cpuTime = batchAlgorithm(cpuTime, process);
                        break;
                // intentional no break statement for case 3 & 4
                case 3: timeQuantum = 20;
                case 4: 
                case 5: cpuTime = interactiveAlgorithm(cpuTime, timeQuantum, 
                            process, algorithm);
                        break;
            }
            cpuTime += switchCost; // apply switch cost to each algorithm
        }
    }
    
    // gives each process a range of lottery tickets in ascending order
    private void initializeLottery(Collection<Process> processes){
        // range of lottery numbers between 1 and sum of priorities
        for(Process p:processes) {
            p.setRange(totalPriority + 1, totalPriority + p.priority);
            totalPriority += p.priority;
        }
    }
    
    // adds a process selected by random through lottery via ticket
    private void addLotteryProcess(Collection<Process> processes){
        int lotteryNum = (int) (Math.random() * totalPriority + 1);
        for(Process p:processes) {
            if(p.startRange <= lotteryNum && p.endRange >= lotteryNum) {
                processQueue.add(p);
                break;
            }
        }
    }
    
    /**
     * @return cpuTime
     */
    private int batchAlgorithm(int cpuTime, Process process){
        int completionTime = cpuTime + process.burstTime;
        scheduler.add(new ScheduleColumn(cpuTime,process.id,process.burstTime,0,
            completionTime));
        cpuTime += process.burstTime;       
        totalTurnaroutTime += completionTime;
        return cpuTime;
    }
    
    /**
     * messy code but saves 2 different function definitions
     * @return cpuTime
     */
    private int interactiveAlgorithm(int cpuTime, int timeQuantum, Process process,
            int algorithm) {
        int completionTime;
        // used so that processList isn't changed
        Process temp = new Process(process.id,process.burstTime,process.priority);
        int endBT = process.burstTime - timeQuantum;
        if (endBT <= 0) {
            endBT = 0;
            completionTime = cpuTime + process.burstTime;
        } else 
            completionTime = 0;
        // post schedule column
        scheduler.add(new ScheduleColumn(cpuTime, process.id, process.burstTime, 
            endBT, completionTime));
        // if process is finished ...add remaining burst time to cpu time
        if (endBT == 0) {
            if(algorithm == 5){
                // ok since lottery algorithm is last to be performed
                processList.remove(process);
                // reinitialize lottery ticket allocation (not efficient)
                totalPriority = 0;
                initializeLottery(processList);
            }
            cpuTime += process.burstTime;
        // if not finished ...add burst time done to cpu time 
        } else {
            if (algorithm == 5){
                process.burstTime = endBT;
            } else {
                temp.burstTime = endBT;
                processQueue.add(temp);
            }
            cpuTime += timeQuantum;
        }
        // always add a process, if possible for alg. 5
        if(algorithm == 5) addLotteryProcess(processList);
        totalTurnaroutTime += completionTime;
        return cpuTime;
    }
    
    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Michael Trinh's Scheduler Simulator");
        SchedulerSimulator ss = new SchedulerSimulator();
    }
}