/*
 *- --Description---
 * The Scheduler serves to schedule tasks for execution using a multi-level
 * feedback queue as well as maintain a blockedQ for tasks awaiting 
 * I/O requests.  There are four subqueues each with different quantum
 * definitions as well as turn definitions.
 */
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Scheduler {
    
//		---Class Variables---
    SYSTEM system;
    Mem_manager mem_manager;  
    private Queue sbq1;  //subqueue 1
    private Queue sbq2;  //subqueue 2
    private Queue sbq3;  //subqueue 3
    private Queue sbq4;  //subqueue 4
    private Queue blockedQ;

//		---Constructor---  
    
    public Scheduler(SYSTEM systemIn, Mem_manager mem_managerIn){
        system = systemIn;
        mem_manager = mem_managerIn;

        sbq1 = new Queue(15);
        sbq2 = new Queue(15);
        sbq3 = new Queue(15);
        sbq4 = new Queue(15);      
        blockedQ = new Queue(15);  
    }
	    
//		---Core Functions---	
    /**
     *  Sets up a new PCB given an ArrayList with job information
     *  and adds the newly created PCB into the readyQ
     */
	public void setup(ArrayList<String> list){
	    PCB job = createPCB(list);
	    mem_manager.initializeViBits(job);
		job.setArrivalTime(system.getClk());
		addToReadyQ(job);
	}	
	/*
	/**
	 * This method returns execution time required for the current
	 * execution slice as well as determine what should be done 
	 * to the job once it has executed based on the current burst
	 * size as well as the bursts remaining.  Once the job has finished
	 * all the bursts, it terminates and and is removed from the system.
	 */
//todo need to think about the entries in the job file

	public ReferenceStringEntry getNextInstruction(PCB job){
		return job.getReferenceString().remove(0);
    }

	public boolean quantumExpiredAction(PCB job) {
		if (job.getReferenceString().isEmpty() == false) {
			updateQandT(job);
			endQuantumResch(job);
			return true;
		}
		else{

			system.jobTerminated(job);
			return false;
		}
	}
	public boolean ioRequestBeforeQuantumExpireAction(PCB job){
		if(job.getReferenceString().isEmpty() == false) {
			moveFromRtoB(job);
			job.resetTurns();
			return true;
		}
		else{
			system.jobTerminated(job);
			return false;
		}
	}
	public boolean ioRequestAndQuantumExpire(PCB job){
		if(job.getReferenceString().isEmpty() == false) {
			moveFromRtoB(job);
			updateQandT(job);
			return true;
		}
		else{
			system.jobTerminated(job);
			return false;
		}
	}
	
//		---Queue Mutators---

	//adds a given job to the ready Q into subQ-1
    public void addToReadyQ(PCB job){
    	job.assignTurns(1);
        sbq1.add(job);        
    } 

    // Moves a task form the ReadyQ to the BlockedQ because of I/O
	//

	public void pageFaultBlock(PCB job){
    	blockedQ.add(job);
		job.setTimeFinishIO(system.getClk()+10);
	}
    public void moveFromRtoB(PCB job){

		blockedQ.add(job);
        if(job.getSubQNumber()==4){
        	job.setSubQ(1);
        	job.resetTurns();
        }
        job.incrIOReq();
        job.setTimeFinishIO(system.getClk()+12);
    // todo need to worry about page faulting vs disk i/o
    }

    // Checks Blocked_Q to see if a job has finished I/O and reschedules if true
    public void checkBlockedQ(){
		boolean enoughTime = true;
    	while(enoughTime && !blockedQ.isEmpty()){
    		PCB job = blockedQ.peek();
    		if(system.getClk() < job.getTimeFinishIO()){
    			enoughTime = false;
    		}
    		else{
    			job = blockedQ.pop();
    			if(job.getReferenceString().isEmpty()){

    				system.jobTerminated(job);
				}
    			else{
    				addToSubQ(job.getSubQNumber(), job);
				}
    		}
    	}    		
    }

    //Constructs the PCB object from an ArrayList
    public PCB createPCB(ArrayList<String> list){
	    int jID = Integer.parseInt(list.remove(0));
	    int jSize = Integer.parseInt(list.remove(0));
		String refStringFileAddress = list.remove(0);
        ArrayList<ReferenceStringEntry> refString = constructReferenceString
				(refStringFileAddress);
		int programCounter = (refString.size()-1);
		PCB pcb = new PCB(jID, jSize, programCounter, refString); // adding job id
		// and size

		Page[] newEntry = new Page[128];
		int entries = 0;
		while(entries<128){
			newEntry[entries] = new Page();
			entries++;
		}
		pcb.setPageTableBaseAddress(mem_manager.addPageTable(newEntry));

		return pcb;
    }
    public ArrayList<ReferenceStringEntry> constructReferenceString(String address){
		ArrayList<ReferenceStringEntry> list = new ArrayList<>();
    	Scanner scannerJbX;
//todo change the absolute file reference to a dynamic one
    	File jbX = new File("/Users/dillonledoux/Desktop/jobs/"+address);
        try{
            scannerJbX = new Scanner(jbX);
			while(scannerJbX.hasNextLine()) {
				String line = scannerJbX.nextLine();
				list.add(new ReferenceStringEntry(line));
			}
        }
        catch(Exception e){
            System.out.println("Could not load from " +address);
            System.exit(1);
        }

        return list;
    }
        
//		---Queue Maintenance---      
	/**
	 * Decrements the number of turns the job has left in the current subQ
	 * and if the job has run out of allowed turns in the current queque,
	 * it is demoted to a higher queue and has the number of turns reset
	 * to the appropriate number based on the new queque
	 */
    public void updateQandT(PCB job){
		job.decrementTurns();
        if(job.getTurns()==0){
            demote(job);
            job.resetTurns();  
        }
	}
    //Adds the given job to the subqueue denoted by the given int
    public void addToSubQ(int number, PCB job ){
    	if(number == 1){
    		sbq1.add(job);
    	}
    	else if(number == 2){
    		sbq2.add(job);
    	}
    	else if(number == 3){
    		sbq3.add(job);
    	}
    	else{
    		sbq4.add(job);
    	}
    }
    /**
     * At the end of a quantum, a job must be rescheduled
     * back into the subqueue from which it came and this
     * method serves that purpose
     */
    public void endQuantumResch(PCB job){
        int qNumber = job.getSubQNumber();
        switch (qNumber) {
            case 1: sbq1.add(job);
                    break;
            case 2: sbq2.add(job);
                    break;
            case 3: sbq3.add(job);
                    break;
            default:sbq4.add(job);
        }
    }
    // Moves a job to a lower queue
    public void demote(PCB job){
        if(job.getSubQNumber()<4){
	        job.setSubQ(job.getSubQNumber()+1);
        }    
    }
//		---Getters---   
    // returns the number of the highest queue which has elements
    public int getHighestNonEmptySbqNumber(){
    	if(!sbq1.isEmpty()){
    		return 1;
    	}
    	else if(!sbq2.isEmpty()){
    		return 2;
    	}
    	else if(!sbq3.isEmpty()){
    		return 3;
    	}
    	else if(!sbq4.isEmpty()){
    		return 4;
    	}
    	else{
    		return 0;
    	}
    }    
    public Queue getSubQ(int number){
    	switch (number){
    		case 1: return sbq1;
    		case 2: return sbq2;
    		case 3: return sbq3;
    		default: return sbq4;
    	}
    }     
    public PCB getNextPCB(){
    	PCB job = getSubQ(getHighestNonEmptySbqNumber()).remove(0);
    	return job;
    }
    //returns the total PCBs in the system between the readyQ and blockedQ
    public int getTotalPCBs(){
    	int toReturn = (blockedQ.size() + sbq1.size() + sbq2.size()
		+ sbq3.size() + sbq4.size());
    	return toReturn;
    }
    public int getRQSize(){
        int sizeRQ = sbq1.size() + sbq2.size() + sbq3.size() + sbq4.size();
        return sizeRQ;
    }    
    public Queue getBlockedQ(){
    	return blockedQ;
    } 
}