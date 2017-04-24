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
	Mem_manager mem_manager;
	Replacer replacer;

//		---Constructor---    
	public CPU(SYSTEM systemIn, Scheduler schedulerIn, Mem_manager memIn,
			   Replacer replacerIn){
		system = systemIn;
		scheduler = schedulerIn;
		mem_manager = memIn;
		replacer = replacerIn;
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
		boolean noJob = true;
		while(scheduler.getRQSize()>0) {
			noJob = false;
			PCB job = scheduler.getNextPCB();
			int remainingTime = job.getQuantum();
//			todo write the page checking and faulting for each page
			while (job.isQuantumExpired() == false) {

				ReferenceStringEntry currentEntry = scheduler
						.getNextInstruction(job);
				String instrCode = currentEntry.getCode();
				int pageNumber = currentEntry.getPageNumber();

				if(mem_manager.getPageTables().get((job.getPageTableBaseAddress
						())).get(pageNumber).isResident()){

					mem_manager.getPageTables().get((job.getPageTableBaseAddress
							())).get(pageNumber).setReference(true);
				}
				else{
//					todo do the page replacement action here
					system.pageNotResidentAction(job, pageNumber);
				}

				system.incrSysClock(2);
				remainingTime-=2;
				if(instrCode.equals("p")){
					if(remainingTime<2){
						scheduler.quantumExpiredAction(job);
					}
				}
				else if(instrCode.equals("w")){
					scheduler.moveFromRtoB(job);
					mem_manager.getPageTables().get((job.getPageTableBaseAddress
							())).get(pageNumber).setModified(true);
				}
				else if(instrCode.equals("r")){
					if(remainingTime>0){
						scheduler.ioRequestBeforeQuantumExpireAction(job);
					}
					scheduler.moveFromRtoB(job);
//					If an io burst is the last action, the termination is
// handled in the checkBlockedQ method in scheduler
				}
				else{
					System.out.println("Encountered an weird character: " +
							currentEntry.getCode() + " in reference string");
					System.exit(1);
				}
			}
		}
		if(noJob){
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
			if(incrBy<0){
				incrBy = 0;
			}
			system.incrSysClock(incrBy);
			return true;
		}
		return false;
	}	
}