/*
*Dillon LeDoux
*CS 4323
*Phase 1
*March 23, 2017
*
*--Description--
*This class serves as the main system and controls the
*programs functionality from the top level and containing
*the main routine used to initiate the program.  
*When the program is started by the user, the main calls
*the SYSTEM constructor and all the other subprograms are
*started followed by starting the simulation.
*In addition to running the simulation itself, this class
*also houses a few useful high level methods for operating
*or accessing data.

*--Critique--
*The system as a whole meets the assignment specification
*with very few deviations.  The only system-wide deviation
*from what is assigned is that the SYS_LOG file writes a 
*final entry after all jobs have completed for thoroughness.
*/

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SYSTEM {
     
// 		---Class Variables---
	
	// virtual clock initialization
	private static int CLOCK = 0; 
	// time between each write to SYS_LOG
    private static final int WRITE_EVERY = 200;
    // Memory setup initial used: 0 units    
    public static final int TOTAL_MEMORY = 32768;
    // stores the CSX system time when SYSTEM initialized 
    public static String systemStartTime;          
    // initial free space of 512 units
    private static int freeMemory = 512;     				
    // variable for tracking number of jobs delivered   
    private int jobsDelivered = 0;
    // job file location from user
    private static String fileLocation;

    private static final int FRAME_SIZE = 256;


//		---Object Variables---
     Mem_manager mem_manager;
     Scheduler scheduler;
     Loader loader;
     CPU cpu;
     LogWriter logger;
     Replacer replacer;
         
//		---Constructor---a
     public SYSTEM(){
        initSysTime();
        mem_manager = new Mem_manager(this);
        scheduler = new Scheduler(this, mem_manager);
        loader = new Loader(this, mem_manager, scheduler, fileLocation);       
        logger = new LogWriter(this, mem_manager, scheduler, loader);
        replacer = new Replacer(this, mem_manager);
        cpu = new CPU(this, scheduler, mem_manager, replacer);

      
        this.simulate();
     }

//		---MAIN---     
    public static void main(String args[]){
    	fileLocation = args[0];
    	SYSTEM sys = new SYSTEM();
		
    }
       
//		---SYSTEM Object Methods---
    /**
    * Handles the simulation of the system by continuing as long more
    * jobs are coming into the system from the file and more tasks exist
    * in the system.  1) Calls the scheduler to check the blockedQ and 
    * does the appropriate  action. 2) Calls the loader to load jobs 
    * from the jobQ into the readyQ if there is available memory and 
    * load jobs from the file if there are any available. 3) Calls the
    * cpu to begin job execution.
    */
     public void simulate(){
    	 while(loader.hasMoreJobsInFile() || loader.tasksExist()){
    		scheduler.checkBlockedQ();
    		loader.loadTasks();
    		cpu.execute();
    	}
    	System.out.println("Simulation complete");
     }     
     /** 
      * Sets up the systemStartTime variable.
      */
     public void initSysTime(){
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        systemStartTime = df.format(cal.getTime());     
     }     
     /**
      * Getter for getting the system start time.
      */     
     public String getSystemStartTime(){
        return systemStartTime;
     }     
     /**
      * Increments the system clock by a given value while
      * calling the logger to write to the system log at 
      * the appropriate times.
      */
     public void incrSysClock(int value){
     	for(int i = 1; i<=value; i++){
     		CLOCK++;
     	}
     }

     /**
      * Does the proper actions when a job completes by setting
      * appropriate values, releasing memory, and by initiating a
      * write to JOB_LOG
      */
     public void jobTerminated(PCB finishedJob){
     	finishedJob.setTimeDelivered(CLOCK);    	
         mem_manager.release(finishedJob.getJobSize());
         jobsDelivered++;
         logger.releaseWriteToMemStat(finishedJob);
     }
//     todo needs to be adjusted ^

    public void replacePage(){
//         todo write the method body for the system controlled page replacement
    }

//		---Getters and Setters---    
    public int getFreeMemory(){
        return freeMemory;
    }
    public int getClk(){
        return CLOCK;
     }
    public int getJobsDelivered(){
    	return jobsDelivered;
    }
    public void setFreeMemory(int value){
        freeMemory = value;
    }
    public void addFreeSpace(int value){
        freeMemory += value;
    }



}
