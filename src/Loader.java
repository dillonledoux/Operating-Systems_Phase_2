/**
  * ---Description---
  * The Loader serves to load jobs from the job file and place them
  * in either the readyQ if there is enough free memory space to
  * accommodate the respective job or add the job to the JobQ until
  * enough space becomes available.
  */

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Loader{

	/**
	 * Class Variables
	 */
    private File jbX;
	private Scanner scannerArrivals;
	private boolean moreJobsInFile = true;
	private ArrayList<ArrayList<String>> jobQ = new ArrayList<ArrayList<String>>();

	SYSTEM system;
	Mem_manager mem_manager;
	Scheduler scheduler;

	/**
	 * Constructor
	 *
	 * Constructs a loader object with the other system objects as
	 * well as the filename of where the job files is located
	 * @param strIn		file path of the arrivals file
	 */
	public Loader(SYSTEM systemIn, Mem_manager mem_managerIn,
				  Scheduler schedulerIn, String strIn){
	    system = systemIn;
	    mem_manager = mem_managerIn;
	    scheduler = schedulerIn;
        File arrivals = new File(system.getDirLocation()+""+strIn);
	    try{
	    	scannerArrivals = new Scanner(arrivals);
	    }
	    catch(Exception e){
	    	System.out.println("Could not load from Arrivals File");
	    	System.exit(1);
	    }
	}
	
//		---Object Methods---

    /**
     * Reads the job file and returns a job in the form of an ArrayList
     * A scanner scans one line at a time for new jobs, as long as there
     * are more lines.  It then creates an ArrayList holding the job info.
     * This ArrayList is then converted from that of type String to type
     * integer and it gets returned.
     *
	 * @return an ArrayList containing parsed job information
     */
    public ArrayList<String> getNextJob(){
    	if(scannerArrivals.hasNextLine() == false){
			moreJobsInFile = false;
    		ArrayList<String> list = new ArrayList<>();
    		list.add("0");
    		return list;
    	}
    	String line = scannerArrivals.nextLine();
        ArrayList<String> stringList = new ArrayList<>(Arrays.asList
				(line.split("\\W+")));
        try{
        	Integer.parseInt(stringList.get(0));
        }
        catch(Exception e){
        	stringList.remove(0);
        }
        return stringList;
     }
   
    /**
     * Examines the jobs residing in the jobQ and attempts to allocate 
     * memory to them and add them to the readyQ.  
     */
    public void loadFromJobQ(){
    	int index = 0;
    	boolean canAllocate;
		while((mem_manager.getFramesToBeAllocated())<=128
    		&& scheduler.getTotalPCBs()<15 && index<jobQ.size()){

			canAllocate = mem_manager.admit(Integer.parseInt(jobQ.get(index).get
					(1)));
			if(canAllocate){
    			scheduler.setup(jobQ.remove(index));
    		}
    		index++;
    	}
    }
   /**
    * Calls to load jobs from the jobQ then attempts to get new jobs from
    * the file and insert them in the system if there is enough memory.  If
    * sufficient memory cannot be found, the job is added to the JobQ until
    * enough memory becomes available.
    */
    public void loadTasks(){
		loadFromJobQ();
    	boolean canAllocate;
    	ArrayList<String> newJob;

		while((mem_manager.getFramesToBeAllocated())<= 128
				&& scheduler.getTotalPCBs()<15 && moreJobsInFile ){
			newJob = getNextJob();
    		if((Integer.parseInt(newJob.get(0))!=0)){
    			canAllocate = mem_manager.admit(Integer.parseInt(newJob.get
						(1)));

				if(canAllocate){
    				scheduler.setup(newJob);
    			}
    			else{
    				jobQ.add(newJob);   				
    			}
    		}
    		else{
    			break;
    		}
    	}
    }

	/**
	 * Given a page table and two page addresses, this method
	 * changes the frame number from page address A to B page
	 * B and sets the frame number in page A to -1 representing
	 * no frame allocated.
	 * @param pgTableAddress	address of the page table
	 * @param pgNumberToLoad	page number of the new page to load
	 * @param pgNumberOfToBeReplaced	page number of the current page
	 *                                  which will be replaced
	 */
    public void swapPages(int pgTableAddress, int pgNumberToLoad,
						  int pgNumberOfToBeReplaced){

		int frameNumber = mem_manager.getFrameNumberFromPageTable
				(pgTableAddress, pgNumberOfToBeReplaced);
		mem_manager.setFrameNumberInPageTable(pgTableAddress,
				pgNumberOfToBeReplaced, -1);
		mem_manager.clearResidentBit(pgTableAddress, pgNumberOfToBeReplaced);
		mem_manager.setFrameNumberInPageTable(pgTableAddress,
				pgNumberToLoad, frameNumber);
		mem_manager.setResidentBit(pgTableAddress, pgNumberToLoad);
    }

	/**
	 * Sets the frame number of a page to be a specified value.  In a
	 * physical system, this would populate a frame with a specified page.
	 * @param pgTableAddress	address in the system page table
	 * @param pgNumber	page number in the job's page table
	 * @param frame	frame number to assign to the page
	 */
	public void loadFrameWithPage(int pgTableAddress, int pgNumber, int frame){

    	mem_manager.setFrameNumberInPageTable(pgTableAddress, pgNumber, frame);

	}


// --- Getters ---
	/**
	 * Class getters which return values
	 * @return	values of class variables
	 */
    public boolean hasMoreJobsInFile(){
        return moreJobsInFile;
     }

    public int getJobQSize(){
    	return jobQ.size();
    }

    public boolean tasksExist(){
    	if(getJobQSize()+scheduler.getTotalPCBs()==0){
    		return false;
    	}
    	return true;
    }
}