import java.util.ArrayList;
import java.util.LinkedList;

/**
  * The Mem_manager serves to manage the memory use of
  * the system by allocating memory to jobs if there is
  * enough available and by releasing memory of the jobs
  * who have terminated.
  */
public class Mem_manager {
	
//		---Class Variables---		
	private SYSTEM system;
	private ArrayList<Page[]> ptLib = new ArrayList<>();
	private LinkedList<Integer> fft = new LinkedList<>();
	private int framesToBeAllocated = 0;
	
//		---Constructor---		
	public Mem_manager(SYSTEM systemIn){
	    system = systemIn;
		// initialization fo the fft
	    for(int i = 0; i<128; i++){
			fft.add(i);
		}
	}
	/**
	  * When called, the next free frame is pulled from the free frame
	  * table and the number will be returned
	 * @return the number of the next free frame
	  */
	 public int allocate(){
		int newFree = fft.poll();
		return newFree;
    }

	/**
	 * Releases the memory occupied by tasks which have terminated
	 * @param pageTableAddress address of the job page table
	 */
    public void release(int pageTableAddress){
		for(int i = 0; i<128; i++) {
			if(ptLib.get(pageTableAddress)[i].getFrameNumber()!=-1){
				fft.add(ptLib.get(pageTableAddress)[i].getFrameNumber());
				framesToBeAllocated--;
			}
		}
    }

	/**
	 * When called it checks to see if a job can be accommodated in
	 * memory and if it can, it will set aside those frames for the job
	 * @param size the job size in bytes
	 * @return true if the job can be accommodated
	 */
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

	/**
	 * When a job page table is created, the number of pages that can
	 * be addressed by that job is set here
	 * @param job the job for which the bits are being set
	 */
	public void initializeViBits(PCB job){
		for(int i = 0; i<job.getJobSizeInPages(); i++) {
			ptLib.get(job.getPageTableBaseAddress())[i].setVi(1);
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
	public boolean getReferenceBit(int ptBaseAddress, int pgNumber){
		return ptLib.get(ptBaseAddress)[pgNumber].isReferenced();
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
		ptLib.get(ptBaseAddr)[pgNumber].setModified();
	}
}