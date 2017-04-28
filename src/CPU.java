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
	private int timeInCurrentQuantum;

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
		boolean isJobInRQ = false;
		if(scheduler.getRQSize()>0) {
			isJobInRQ = true;
		}
		while(scheduler.getRQSize()>0) {
			noJob = false;
			PCB job = scheduler.getNextPCB();


			int quantum;
			while (job.isQuantumExpired() == false && job.getReferenceString
					().size() != 0) {
				quantum = job.getQuantum();
				ReferenceStringEntry currentEntry = scheduler
						.getNextInstruction(job);


				if(mem_manager.isPageResident(job.getPageTableBaseAddress(),
						currentEntry.getPageNumber()) == false){
					// if the job is found to be not valid, then it breaks
					// this iteration of the loop.
					boolean result = system.pageNotResidentAction(job,
							currentEntry.getPageNumber());
					if(result==true){
						job.getReferenceString().add(0, currentEntry);
						scheduler.pageFaultBlock(job);
						// todo changes ok?
					}
					else {
						system.jobTerminated(job);
						job.clearReferenceString();
					}
					return;
				}

				mem_manager.setReferenceBit(job.getPageTableBaseAddress(),
						currentEntry.getPageNumber());
				system.incrSysClock(2);
				timeInCurrentQuantum+=2;
				if(currentEntry.getCode().equals("p")){
					if(timeInCurrentQuantum==quantum){
						timeInCurrentQuantum = 0;
						scheduler.quantumExpiredAction(job);
						return;
					}
					if(job.getReferenceString().isEmpty()){
						system.jobTerminated(job);
						return;
					}
				}
				else if(currentEntry.getCode().equals("w")) {

					mem_manager.setModifiedBit(job.getPageTableBaseAddress(),
                            currentEntry.getPageNumber());
					timeInCurrentQuantum = 0;
					if (timeInCurrentQuantum < quantum) {
						scheduler.ioRequestBeforeQuantumExpireAction(job);

					}
					else {
						scheduler.ioRequestAndQuantumExpire(job);
					}

					return;
				}
				else if(currentEntry.getCode().equals("r")) {
					timeInCurrentQuantum = 0;
					if (timeInCurrentQuantum < quantum) {
						scheduler.ioRequestBeforeQuantumExpireAction(job);

					}
					else {
						scheduler.ioRequestAndQuantumExpire(job);
						}

					return;

				}
				else{
					System.out.println("Encountered an weird character: " +
							currentEntry.getCode() + " in reference string");
					System.exit(1);
				}

			}
		}
		if(isJobInRQ==false){
			noJobWait();
			return;
		}
	}
	/**
	 * When no job can execute and the blockedQ must be waited on,
	 * the time the blockedQ job will complete is acquired and the clock
	 * is incremented by this value
	 */
	public boolean noJobWait(){
		if(scheduler.getBlockedQ().size()>0){		
			int newTime = scheduler.getBlockedQ().peek().
					getTimeFinishIO();

			system.setCLOCK(newTime);
			return true;
		}
		return false;
	}	
}