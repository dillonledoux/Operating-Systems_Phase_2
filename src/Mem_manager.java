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
	SYSTEM system;
	ArrayList<ArrayList<PTEntry>> pageTables = new ArrayList<>();

	
	// Defines the point at which the memory is considered full

	private  final int FRAME_CUTOFF = 2;
	private static final int TOTAL_FRAMES = 128;
	private LinkedList<Integer> fft = new LinkedList<>();
	private int framesNeeded = 0;
	
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
        return fft.poll();
    } 
	//releases the memory occupied by tasks which have terminated
    public void release(int pageTableAddress){
        for(int i = 0; i<pageTables.get(pageTableAddress).size(); i++) {
			if (pageTables.get(pageTableAddress).get(i).getFrameNumber() != -1) {
				fft.add(pageTables.get(pageTableAddress).get(i).getFrameNumber());
				pageTables.get(pageTableAddress).get(i).setFrameNumber(-1);
				framesNeeded--;
			}
		}
    }

    public boolean admit(int size){
    	int framesNecessary = (int) Math.ceil(Math.ceil(size/256.0)*0.25);
    	if(framesNeeded>=framesNecessary){
			framesNeeded += framesNecessary;
    		return true;
		}
		else{
    		return false;
		}
	}

	public void initializeViBits(PCB job){
		for(int i = 0; i<job.getJobSizeInPages(); i++) {
			pageTables.get(job
					.getPageTableBaseAddress()).get(i).setVi(1);
		}
		for(int i = 0; i<128; i++){
			pageTables.get(job.getPageTableBaseAddress())
					.get(i).clearResident();
			pageTables.get(job.getPageTableBaseAddress())
					.get(i).clearReference();
			pageTables.get(job.getPageTableBaseAddress())
					.get(i).clearModified();
		}
	}

//		---Getter and Logging---


    public double getPercentFreeFrames(){
    	return (fft.size()/TOTAL_FRAMES)*100;
	}
	public double getPercentAllocatedFrames(){
    	return ((TOTAL_FRAMES-fft.size())/TOTAL_FRAMES)*100;
	}

	public ArrayList<ArrayList<PTEntry>> getPageTables() {
		return pageTables;
	}

	public int addPageTable(ArrayList<PTEntry> pageTable) {
		this.pageTables.add(pageTable);
		return(pageTable.size()-1);
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


	public int getFrameCutoff() {
		return FRAME_CUTOFF;
	}

	public int getFramesNeeded() {
		return framesNeeded;
	}
	//collects the memory statistics at the time invoked
}