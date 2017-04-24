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
	private static final int MEM_CUTOFF = 51;
	private static final int TOTAL_FRAMES = 128;
	private LinkedList<Integer> fft = new LinkedList<>();
	
//		---Constructor---		
	public Mem_manager(SYSTEM systemIn){
	    system = systemIn;
		for(int i = 0; i<128; i++){
			fft.add(i);
		}
	}

//	todo add frame allocation and tracking for each job
//	todo possibly make way to track allocated frames

//		---Memory Mutators---
 
	//allocates free space to jobs if available
//	todo this must be modified for phase2 operation
	public boolean allocate(int size){
        if(system.getFreeMemory() >= size){
            system.setFreeMemory(system.getFreeMemory() - size);
            return true;
        }
		return false;
    } 
	//releases the memory occupied by tasks which have terminated
    public void release(int size){
        system.addFreeSpace(size);
    }
   
//		---Getter and Logging---
    public int getMemCutoff(){
    	return MEM_CUTOFF;
    }
    //collects the memory statistics at the time invoked
    public String memStats(){
    	String toReturn = String.format("%-5d   |   %-3d  |  %-3d   |",
    			system.getClk(), system.getFreeMemory(), 
    			(SYSTEM.TOTAL_MEMORY-system.getFreeMemory()));
    	return toReturn;
    }

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
		return 256-fft.size();
	}
}