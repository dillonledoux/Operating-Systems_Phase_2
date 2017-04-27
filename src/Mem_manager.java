import java.util.ArrayList;
import java.util.LinkedList;

/*
 * The Mem_manager serves to manage the memory use of
 * the system by allocating memory to jobs if there is
 * enough available and by releasing memory of the jobs
 * who have terminated.
 */
public class Mem_manager {
	
//		---Class Variables---		
	private SYSTEM system;
	private ArrayList<Page[]> ptLib = new ArrayList<>();

	
	// Defines the point at which the memory is considered full

	private static final int TOTAL_FRAMES = 128;
	private LinkedList<Integer> fft = new LinkedList<>();
	private int framesToBeAllocated = 0;
	
//		---Constructor---		
	public Mem_manager(SYSTEM systemIn){
	    system = systemIn;
		for(int i = 0; i<128; i++){
			fft.add(i);
		}

	}

//		---Memory Mutators---
 
	//allocates free space to jobs if available

	public int allocate(){
		int newFree = fft.poll();
		return newFree;
    } 
	//releases the memory occupied by tasks which have terminated
    public void release(int pageTableAddress){

		for(int i = 0; i<128; i++) {

			if(ptLib.get(pageTableAddress)[i].getFrameNumber()!=-1){
				fft.add(ptLib.get(pageTableAddress)[i].getFrameNumber());
				framesToBeAllocated--;
			}
		}
    }

    public boolean admit(int size){
    	int framesNecessary = (int) Math.ceil((Math.ceil(size/256.0))*0.25);
    	if(128-framesToBeAllocated >=framesNecessary){
			framesToBeAllocated = framesToBeAllocated + framesNecessary;
			return true;
		}
		else{
    		return false;
		}
	}

	public void initializeViBits(PCB job){
		for(int i = 0; i<job.getJobSizeInPages(); i++) {
			ptLib.get(job.getPageTableBaseAddress())[i].setVi(1);
		}
		for(int i = 0; i<128; i++){
			ptLib.get(job.getPageTableBaseAddress())[i].clearResident();
			ptLib.get(job.getPageTableBaseAddress())[i].clearReference();
			ptLib.get(job.getPageTableBaseAddress())[i].clearModified();
		}
	}

//		---Getter and Logging---

	public ArrayList<Page[]> getPtLib() {
		return ptLib;
	}

	public int addPageTable(Page[] pageTable) {
		ptLib.add(pageTable);
		return(ptLib.size()-1);
	}

	public LinkedList<Integer> getFft() {
		return fft;
	}
	public int getNextFFNumber(){
		return fft.poll();
	}
	public int getNumberOfFreeFrames(){
		return fft.size();
	}
	public int getNumberAllocatedFrames(){
		return 128-fft.size();
	}



	public int getFramesToBeAllocated() {
		return framesToBeAllocated;
	}

	public int getFrameNumberFromPageTable(int ptBaseAddress, int pgNumber){
		return ptLib.get(ptBaseAddress)[pgNumber].getFrameNumber();
	}
	public void setFrameNumberInPageTable(int ptBaseAddress, int pgNumber,
										 int newFrameNumber){
		ptLib.get(ptBaseAddress)[pgNumber].
				setFrameNumber(newFrameNumber);
	}
	public void clearResidentBit(int ptBaseAddress, int pgNumber){
		ptLib.get(ptBaseAddress)[pgNumber].clearResident();
	}
	public void setResidentBit(int ptBaseAddress, int pgNumber){
		ptLib.get(ptBaseAddress)[pgNumber].setResident();
	}
	public Page getPage(PCB job, int pgNumber){
		return ptLib.get(job.getPageTableBaseAddress())[pgNumber];
	}
	public boolean isPageResident(int ptBaseAddr, int pgNumber){
		return ptLib.get(ptBaseAddr)[pgNumber].isResident();
	}
	public void setPageResident(int ptBaseAddr, int pgNumber){
		ptLib.get(ptBaseAddr)[pgNumber].setResident();
	}
	public void clearPageResident(int ptBaseAddr, int pgNumber){
		ptLib.get(ptBaseAddr)[pgNumber].clearResident();
	}
	public void setReferenceBit(int ptBaseAddr, int pgNumber){
		ptLib.get(ptBaseAddr)[pgNumber].setReference(true);
	}
	public void clearReferenceBit(int ptBaseAddr, int pgNumber){
		ptLib.get(ptBaseAddr)[pgNumber].setReference(false);
	}
	public int getViBit(int ptBaseAddr, int pgNumber){
		return ptLib.get(ptBaseAddr)[pgNumber].getVi();
	}
	public boolean getModifiedBit(int ptBaseAddr, int pgNumber){
		return ptLib.get(ptBaseAddr)[pgNumber].isModified();
	}
	public void setModifiedBit(int ptBaseAddr, int pgNumber){
		ptLib.get(ptBaseAddr)[pgNumber].setModified(true);
	}
	public void clearModifiedBit(int ptBaseAddr, int pgNumber){
		ptLib.get(ptBaseAddr)[pgNumber].setModified(false);
	}

	public double getPercentFreeFrames(){
		return (fft.size()/TOTAL_FRAMES)*100;
	}
	public double getPercentAllocatedFrames(){
		return ((TOTAL_FRAMES-fft.size())/TOTAL_FRAMES)*100;
	}
	//collects the memory statistics at the time invoked
}