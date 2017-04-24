import java.util.ArrayList;

/*
 * The Mem_manager serves to manage the memory use of
 * the system by allocating memory to jobs if there is
 * enough available and by releasing memory of the jobs
 * who have terminated.
 */
public class Mem_manager {
	
//		---Class Variables---		
	SYSTEM system;
	FFT fft;
	ArrayList<ArrayList<PTEntry>> pageTables = new ArrayList<>();

	
	// Defines the point at which the memory is considered full
	private static final int MEM_CUTOFF = 51;
	private static final int TOTAL_FRAMES = 128;
	
//		---Constructor---		
	public Mem_manager(SYSTEM systemIn){
	    system = systemIn;
	    fft = new FFT();
	}

//	todo add frame allocation and tracking for each job
//	todo add way to determine internal fragmentation
//	todo possibly make way to track allocated frames

//		---Memory Mutators---
 
	//allocates free space to jobs if available
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
    	return (fft.getNumberOfFreeFrames()/TOTAL_FRAMES)*100;
	}
	public double getPercentAllocatedFrames(){
    	return (fft.getNumberAllocatedFrames()/TOTAL_FRAMES)*100;
	}

	public ArrayList<ArrayList<PTEntry>> getPageTables() {
		return pageTables;
	}

	public int addPageTable(ArrayList<PTEntry> pageTable) {
		this.pageTables.add(pageTable);
		return(pageTable.size()-1);
	}
}