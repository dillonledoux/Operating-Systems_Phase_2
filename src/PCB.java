/*
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
	private int timeDelivered;  // time job terminated
	private int timeUsed;		// cumulative CPU time used by the job
	private int timeFinishIO;	// time the job will finish an IO request
	private int IoReq;          // number of I/O requests
	private int subQ = 1;       //current subqueue, initially in subQ1
	private int subQTurns = 3;  //turns spent in given subqueue;

// -- Phase 2 Addition --
	private int programCounter;
	private int jobSizeInPages;
	private int maxAllocatableFrames;
	private int allocatedFrames = 0;
	private int pageTableBaseAddress;
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

// todo be able to track the max amount of pages that can be allocated to a job

//	todo trck the number of frames actually allocated to a specific job
	//  --- Constructor ---
	public PCB(int id, int size, int counter, ArrayList<ReferenceStringEntry>
			refString){
		jobID = id;
		jobSize = size;	
		programCounter = counter;
		referenceString = refString;

		jobSizeInPages = (int) Math.ceil((jobSize/256.0));
		maxAllocatableFrames = (int) Math.ceil(jobSizeInPages*0.25);

		internalFragmentation = (jobSizeInPages*256)-jobSize;
		originalReferenceStringSize = refString.size();

		// populates the page table with empty entries





	}

//		---Object Functions---
	/**
	 * Assigns the appropriate number of turns based
	 * on the number supplied which represents the
	 * subQ number the job resides in.
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
    	
//		---Setters and Getters---
	public void setTimeFinishIO(int clkIn){
		timeFinishIO = clkIn+12;
	}

	public void setArrivalTime(int time){
		timeArrival = time;
	}

	public void setTimeDelivered(int time){
		timeDelivered = time;
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

	public int getTimeArrival(){
		return timeArrival;
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

//		---Mutators--- 
	public void incrIoRequests(){
	    IoReq++;
	}

    public void incrementTurns(){
	    subQTurns++;
	}

	public void decrementTurns(){
	    subQTurns--;
	}

	public void incrTimeUsed(){
	    timeUsed++;
	}

	public void incrTimeUsed(int value){
	    timeUsed += value;
	}

	public void incrIOReq(){
	    IoReq++;
	}

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

	public int getProgramCounter() {
		return programCounter;
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

	public int getNumberOfReplacements() {
		return numberOfReplacements;
	}

	public void setProgramCounter(int programCounter) {
		this.programCounter = programCounter;
	}

	public void setJobSizeInPages(int jobSizeInPages) {
		this.jobSizeInPages = jobSizeInPages;
	}

	public void setPageTableBaseAddress(int pageTableBaseAddress) {
		this.pageTableBaseAddress = pageTableBaseAddress;
	}

	public void incrNumberOfPageFaults() {
		this.numberOfPageFaults++;
	}

	public void incrNumberOfReplacements() {
		this.numberOfReplacements++;
	}

	public boolean isNormalTermination() {
		return normalTermination;
	}

	public void setNormalTermination(boolean normalTermination) {
		this.normalTermination = normalTermination;
	}

	public int getCleanPageReplacements() {
		return cleanPageReplacements;
	}

	public void setCleanPageReplacements(int cleanPageReplacements) {
		this.cleanPageReplacements = cleanPageReplacements;
	}

	public int getDirtyPageReplacements() {
		return dirtyPageReplacements;
	}

	public void setDirtyPageReplacements(int dirtyPageReplacements) {
		this.dirtyPageReplacements = dirtyPageReplacements;
	}



	public boolean isQuantumExpired() {
		return quantumExpired;
	}

	public void setQuantumExpired(boolean quantumExpired) {
		this.quantumExpired = quantumExpired;
	}

	public ArrayList<ReferenceStringEntry>
	getReferenceStringEntries() {
		return referenceString;
	}

	public void setReferenceStringEntry(ArrayList<ReferenceStringEntry> list){
		referenceString = list;
	}

	public void addToReferenceStringEntries
			(ReferenceStringEntry
					 referenceStringEntry) {
		referenceString.add(referenceStringEntry);
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
	public void decrAllocatedFrames(){
		allocatedFrames--;
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

}


