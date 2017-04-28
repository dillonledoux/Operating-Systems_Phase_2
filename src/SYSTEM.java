/**
  *Dillon LeDoux
  *CS 4323
  *Phase 2
  *April 27, 2017
  *
  *--Description--
  *This class serves as the main system and controls the
  *programs functionality from the top level and contains
  *the main routine used to initiate the program.
  *When the program is started by the user, the main calls
  *the SYSTEM constructor and all the other subprograms are
  *started followed by starting the simulation.
  *In addition to running the simulation itself, this class
  *also houses a few useful high level methods for operating
  *or accessing data.

  *--Critique--
  *The system as a whole meets the assignment specification
  *with very few deviations.  A system-wide deviation
  *from what is assigned is that the MEM_STAT file writes a
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
    public static String systemStartTime;          
    // initial free space of 512 units
    private int jobsDelivered = 0;
    // job files directory path from the user
    private static String dirLocation;
    // name of the file outlining the jobs to be done
    private static String fileName;

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
        loader = new Loader(this, mem_manager, scheduler, fileName);
        logger = new LogWriter(this, mem_manager, scheduler, loader);
        replacer = new Replacer(this, mem_manager);
        cpu = new CPU(this, scheduler, mem_manager, replacer);

        this.simulate();
     }
//		---MAIN---     
    public static void main(String args[]){
    	dirLocation = args[0];
    	fileName = args[1];
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
    	logger.intervalWriteToMemStat();
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
      * appropriate values, releasing memory frames, and by initiating a
      * write to MEM_STATS
      */
     public void jobTerminated(PCB finishedJob) {
         mem_manager.release(finishedJob.getPageTableBaseAddress());
         jobsDelivered++;
         if (jobsDelivered % 4 == 0) {
             logger.intervalWriteToMemStat();
         }
         logger.releaseWriteToMemStat(finishedJob);
     }

    /**
     * This method is called when the CPU discovers that a page it needs
     * to access is not loaded into a frame.  It first checks to see if
     * there are any more frames owed to the job requesting the page
     * and if there is, it will load a page into a frame.  Otherwise it
     * will trigger a page replacement to load the requested page.
     * In addition, it will also set and clear appropriate bits
     * @param job the job requesting a non-resident page
     * @param pageNumber the page being requested
     * @return returns false if the page requested is an invalid page
     */
    public boolean pageNotResidentAction(PCB job, int pageNumber){

        job.incrNumberOfPageFaults();
        if(mem_manager.getViBit(job.getPageTableBaseAddress(), pageNumber)==1){
            if(job.getAllocatedFrames()<job.getMaxAllocatableFrames()){

                loader.loadFrameWithPage(job.getPageTableBaseAddress(),
                        pageNumber, mem_manager.allocate());
                job.incrAllocatedFrames();
                logger.writeToTraceFileLoad(job, pageNumber);
            }
            else{
                int victim = replacer.findVictim(job.
                        getPageTableBaseAddress());
                logger.writeToTraceFileREPlace(job, pageNumber, victim);
                if(mem_manager.getModifiedBit(job.getPageTableBaseAddress(),
                        victim)){

                    job.incrDirtyPageReplacements();
                }
                else{
                    job.incrCleanPageReplacements();
                }
                loader.swapPages(job.getPageTableBaseAddress(), pageNumber,
                        victim);

                replacer.clearReferenceBits(job.getPageTableBaseAddress());
                mem_manager.setResidentBit(job.getPageTableBaseAddress(),
                        pageNumber);
            }
            mem_manager.setResidentBit(job.getPageTableBaseAddress(),
                    pageNumber);
            mem_manager.setReferenceBit(job.getPageTableBaseAddress(),
                    pageNumber);

            return true;
        }
        else{
            //If the job tries to reference and invalid page number, the job
            //is set as an abnormal termination and this is flagged back to
            //the CPU through the boolean return value
            job.setToAbnormalTermination();
            return false;
        }
    }
//		---Getters and Setters---    

    public int getClk(){
        return CLOCK;
     }
    public void setCLOCK(int time){
        CLOCK = time;
    }
    public String getDirLocation(){
        return dirLocation;
    }
}
