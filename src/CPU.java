/*
 * ---Description---
 * This class serves as the CPU and executes tasks
 * supplied to it.  The CPU's main function in this
 * implementation is to increment the clock time 
 * representing a job being executed.
 */
public class CPU{
	SYSTEM system;
	Scheduler scheduler;

//		---Constructor---    
	public CPU(SYSTEM systemIn, Scheduler schedulerIn){
		system = systemIn;
		scheduler = schedulerIn;
	}
	
//		---Object Functions---	
	/**
	 * This method handles the program execution actions.
	 * 1) It checks to make sure there is something that can
	 * be executed in the readyQ, 2) the next job is called 
	 * from the scheduler, 3) the execution time is acquired
	 * and the system clock is incremented appropriately.  If
	 * there is no job that can be executed and there is no option 
	 * but to wait for the object in the blockedQ to complete, the
	 * clock will be incremented appropriately. 
	 */
	public void execute(){	
		if(scheduler.getRQSize()>0){
			PCB job = scheduler.getNextPCB();
			int executeTime = scheduler.getNextTask(job);
			system.incrSysClock(executeTime);
		}
		else{
			noJobWait();			
		}	
	}
	/**
	 * When no job can execute and the blockedQ must be waited on,
	 * the time the blockedQ job will complete is acquired and the clock
	 * is incremented by this value
	 */
	public boolean noJobWait(){
		if(scheduler.getBlockedQ().size()>0){		
			int incrBy = scheduler.getBlockedQ().peek().getTimeFinishIO()-system.getClk();
			system.incrSysClock(incrBy);
			return true;
		}
		return false;
	}	
}