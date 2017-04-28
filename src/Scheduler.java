/**
  * --Description---
  * The Scheduler serves to schedule tasks for execution using a multi-level
  * feedback queue as well as maintain a blockedQ for tasks awaiting
  * I/O requests.  There are four subqueues each with different quantum
  * definitions as well as turn definitions.
  */
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Scheduler {

	/**
	 * Class Variables
	 */
	SYSTEM system;
    Mem_manager mem_manager;  
    private Queue sbq1;  //subqueue 1
    private Queue sbq2;  //subqueue 2
    private Queue sbq3;  //subqueue 3
    private Queue sbq4;  //subqueue 4
    private Queue blockedQ;

	/**
	 * Constructor
	 *
	 * Constructs the scheduler object and instantiates the
	 * required queues
	 * @param systemIn system object
	 * @param mem_managerIn memory manager object
	 */
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
		addToReadyQ(job);
	}

	/**
	 * This method returns the next instruction in the reference string as
	 * well as determine what should be done
	 * to the job once it has executed based on the quantum remaining
	 * as well as the actions taken.  Once the job has finished
	 * all instructions it terminates and is removed from the system.
	 */
	public ReferenceStringEntry getNextInstruction(PCB job){
		return job.getReferenceString().remove(0);
    }

	/**
	 * When a quantum expires and the last instrution was a processing
	 * instruction, this serves to reschedule the job based on
	 * subqueue
	 * @param job currently executing job
	 * @return	true if the job still has more instructions
	 */
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

	/**
	 * If there is an I/O request and there is still time left on the
	 * job's quantum, this method is called to properly reschule the job
	 * @param job currently executing job
	 * @return true if the job still has more instructions
	 */
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

	/**
	 * If there is an I/O request and the quantum is expired as well,
	 * this method is called to reschedule the job appropriately
	 * @param job currently executing job
	 * @return true if there are more instructions
	 */
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

	/**
	 * When a page fault occurs, this method is called ot add
	 * the job to the blockedQ to wait
	 * @param job the job currently faulting
	 */
	public void pageFaultBlock(PCB job){
    	blockedQ.add(job);
		job.setTimeFinishIO(system.getClk()+10);
	}

	/**
	 * When a job requests I/O, this method is called to move the
	 * job to the blockedQ.
	 * @param job
	 */
	public void moveFromRtoB(PCB job){
		blockedQ.add(job);
        if(job.getSubQNumber()==4){
        	job.setSubQ(1);
        	job.resetTurns();
        }
        job.setTimeFinishIO(system.getClk()+12);
    }

	/**
	 * Checks the blocked queue to see if any of the jobs have finished
	 * their actions, if so, then the jobs are rescheduled to the readyQ
	 */
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

	/**
	 * Constructs the PCB object from an ArrayList
	 * @param list arraylist of job information
	 * @return constructed PCB of the job
	 */
	public PCB createPCB(ArrayList<String> list){
	    int jID = Integer.parseInt(list.remove(0));
	    int jSize = Integer.parseInt(list.remove(0));
		String refStringFileAddress = list.remove(0);
        ArrayList<ReferenceStringEntry> refString = constructReferenceString
				(refStringFileAddress);
		int programCounter = (refString.size()-1);
		PCB pcb = new PCB(jID, jSize, programCounter, refString);

		// constructs a new page table
		Page[] newEntry = new Page[128];
		int entries = 0;
		while(entries<128){
			newEntry[entries] = new Page();
			entries++;
		}
		pcb.setPageTableBaseAddress(mem_manager.addPageTable(newEntry));

		return pcb;
    }

	/**
	 * Given an address to the reference string file, this method constructs
	 * an arraylist of ReferenceStringEntry objects holding the appropriate
	 * values
	 * @param address points the the reference string file fo the job
	 * @return returns an Arraylist of the reference string entries
	 */
	public ArrayList<ReferenceStringEntry> constructReferenceString(String address){
		ArrayList<ReferenceStringEntry> list = new ArrayList<>();
    	Scanner scannerJbX;
    	File jbX = new File(system.getDirLocation()+""+address);
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

	/**
	 * Adds the given job to the subqueue denoted by the given int
	 * @param number the subqueue number to which it will be added
	 * @param job	the job to be addedd
	 */
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

	/**
	 * Moves a job from one queue to a lower queue
	 * @param job job to be moved
	 */
	public void demote(PCB job){
        if(job.getSubQNumber()<4){
	        job.setSubQ(job.getSubQNumber()+1);
        }    
    }
//		---Getters---   

	/**
	 * Gets the number of the highest priority subqueue which is not empty
	 * @return the number the highest sQ
	 */
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

	/**
	 * Gets the next job from the ready queue in the order of priority
	 *
	 * @return the job which is next in line
	 */
	public PCB getNextPCB(){
    	PCB job = getSubQ(getHighestNonEmptySbqNumber()).remove(0);
    	return job;
    }

	/**
	 * returns the total PCBs in the system between the readyQ and blockedQ
	 */
    public int getTotalPCBs(){
    	int toReturn = (blockedQ.size() + sbq1.size() + sbq2.size()
		+ sbq3.size() + sbq4.size());
    	return toReturn;
    }

	/**
	 * @return total size of the readyQ
	 */
	public int getRQSize(){
        int sizeRQ = sbq1.size() + sbq2.size() + sbq3.size() + sbq4.size();
        return sizeRQ;
    }    
    public Queue getBlockedQ(){
    	return blockedQ;
    } 
}