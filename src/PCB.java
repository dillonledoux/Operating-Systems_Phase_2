/**
  * --Description--
  * This class serves as a data structure for holding
  * all the necessary information about a job in the
  * system and allows other classes to access this info
  * when requested.
  */
import java.util.ArrayList;

public class PCB {

	// Global variables
	private int jobID;
	private int jobSize; // memory required for job (in bytes)
	private int timeArrival;	// time job entered the system
	private int timeFinishIO;	// time the job will finish an IO request
	private int subQ = 1;       //current subqueue, initially in subQ1
	private int subQTurns = 3;  //turns spent in given subqueue;

	// -- Phase 2 Addition --
	private int programCounter;		// address for the reference string file
	private int jobSizeInPages;
	private int maxAllocatableFrames;// maximum frames which the job can fill
	private int allocatedFrames = 0;
	private int pageTableBaseAddress;//address to the job's page table
	private int numberOfPageFaults;
	private int numberOfReplacements;
	private int internalFragmentation;
	private int originalReferenceStringSize;
	private boolean normalTermination = true;
	private int cleanPageReplacements;
	private int dirtyPageReplacements;
	private boolean quantumExpired = false;
	private  ArrayList<ReferenceStringEntry>
			referenceString = new ArrayList<ReferenceStringEntry>();

	/**
	 * Constructor
	 * Sets up the PCB object
	 * @param id job id
	 * @param size job size in bytes
	 * @param counter program counter
	 * @param refString list of job instructions
	 */
	public PCB(int id, int size, int counter, ArrayList<ReferenceStringEntry>
			refString){
		jobID = id;
		jobSize = size;	
		programCounter = counter;
		referenceString = refString;

		// converts the job size from bytes to the number of pages
		// necessary to hold it
		jobSizeInPages = (int) Math.ceil((jobSize/256.0));

		//the job size in pages divided by 4 then rounded up
		maxAllocatableFrames = (int) Math.ceil(jobSizeInPages*0.25);

		// the amount of free space left in the last allocated page
		internalFragmentation = (jobSizeInPages*256)-jobSize;
		originalReferenceStringSize = refString.size();
	}

//		---Object Functions---
	/**
	 * Assigns the appropriate number of turns based
	 * on the number supplied which represents the
	 * subQ number the job resides in.
	 * @param n subqueue number
	 */
    public void assignTurns(int n){
        switch (n){
            case 1: subQTurns = 3;
                    break;
            case 2: subQTurns = 5;
                    break;
            case 3: subQTurns = 6;
                    break;
            default: subQTurns = 2147483647;
                    break;
        }
    }

	/**
	 * resets teh number of turns a job has based on the subquue
	 * in which it resides
	 */
	public void resetTurns(){
		switch (subQ){
			case 1: subQTurns = 3;
				break;
			case 2: subQTurns = 5;
				break;
			case 3: subQTurns = 6;
				break;
			default: subQTurns = 2147483647;
				break;
		}
	}

	/**
	 * Getters, Setters, Mutators
	 *
	 * Getters, Setters, and Mutators for each necessary
	 * variable
	 */
	public void setTimeFinishIO(int clkIn){
		timeFinishIO = clkIn;
	}

	public void setSubQ(int number){
	    subQ = number;
	}

	public int getTimeFinishIO(){
		return timeFinishIO;
	}

	public int getJobID(){
		return jobID;
	}

	public int getJobSize(){
		return jobSize;
	}

	/**
	 * Returns the appropriate time quantum derived from
	 * the subQ which the job currently resides
	 */
	public int getQuantum(){
	    if(subQ==1){
	        return 10;
	    }
	    else if(subQ==2){
	        return 16;
	    }
	    else if(subQ==3){
	        return 24;
	    }
	    else{
	        return 40;
	    }
	}

	public int getTurns(){
	    return subQTurns;
	}

	public int getSubQNumber(){
        return subQ;
    }

	public void decrementTurns(){
	    subQTurns--;
	}

	public int getJobSizeInPages() {
		return jobSizeInPages;
	}

	public int getPageTableBaseAddress() {
		return pageTableBaseAddress;
	}

	public int getNumberOfPageFaults() {
		return numberOfPageFaults;
	}

	public void setPageTableBaseAddress(int pageTableBaseAddress) {
		this.pageTableBaseAddress = pageTableBaseAddress;
	}

	public void incrNumberOfPageFaults() {
		this.numberOfPageFaults++;
	}

	public boolean isNormalTermination() {
		return normalTermination;
	}

	public void setToAbnormalTermination() {
		normalTermination = false;
	}

	public int getCleanPageReplacements() {
		return cleanPageReplacements;
	}

	public void incrCleanPageReplacements() {
		cleanPageReplacements++;
	}

	public int getDirtyPageReplacements() {
		return dirtyPageReplacements;
	}

	public void incrDirtyPageReplacements() {
		dirtyPageReplacements++;
	}

	public boolean isQuantumExpired() {
		return quantumExpired;
	}

	public ArrayList<ReferenceStringEntry> getReferenceString() {
		return referenceString;
	}

	public int getAllocatedFrames() {
		return allocatedFrames;
	}

	public void incrAllocatedFrames() {
		allocatedFrames++;
	}

	public int getMaxAllocatableFrames() {
		return maxAllocatableFrames;
	}

	public int getInternalFragmentation() {
		return internalFragmentation;
	}

	public int getOriginalReferenceStringSize() {
		return originalReferenceStringSize;
	}

	public void clearReferenceString(){
		referenceString.clear();
	}
}