/*
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
	
//		---Class Variables---
	
	private  File jobFile;
	private Scanner scanner;	
	private boolean moreJobsInFile = true;	
	private ArrayList<ArrayList<Integer>> jobQ = new ArrayList<ArrayList<Integer>>(); 
		
	SYSTEM system;
	Mem_manager mem_manager;
	Scheduler scheduler;
		
//		---Constructor---
	public Loader(SYSTEM systemIn, Mem_manager mem_managerIn, Scheduler schedulerIn, String strIn){
	    system = systemIn;
	    mem_manager = mem_managerIn;
	    scheduler = schedulerIn;
	    jobFile = new File(strIn);
	    try{
	    	scanner = new Scanner(jobFile);
	    }
	    catch(Exception e){
	    	System.out.println("Could not load form Job File");
	    	System.exit(1);
	    }
	}
	
//		---Object Methods---     
	
    public void addToJobQ(ArrayList<Integer> job){
     	jobQ.add(job);
    }
    /**
     * Reads the job file and returns a job in the form of an ArrayList
     * A scanner scans one line at a time for new jobs, as long as there
     * are more lines.  It then creates an ArrayList holding the job info.
     * This ArrayList is then converted from that of type String to type
     * integer and it gets returned.
     *
     */
    public ArrayList<Integer> getNextJob(){
    	if(scanner.hasNextLine() == false){
    		moreJobsInFile = false;
    		ArrayList<Integer> list = new ArrayList<>();
    		list.add(0);
    		return list;
    	}
    	String line = scanner.nextLine(); 	
        ArrayList<String> stringList = new ArrayList<>(Arrays.asList(line.split("\\W+")));
        try{
        	Integer.parseInt(stringList.get(0));
        }
        catch(Exception e){
        	stringList.remove(0);
        }       
        ArrayList<Integer> jobInfo = new ArrayList<>();
        for(String s: stringList){
        	jobInfo.add(Integer.valueOf(s));
        }
        return jobInfo;   
     }
   
    /**
     * Examines the jobs residing in the jobQ and attempts to allocate 
     * memory to them and add them to the readyQ.  
     */
    public void loadFromJobQ(){
    	int index = 0;
    	boolean canAllocate; 
    	while(system.getFreeMemory()>=mem_manager.getMemCutoff()  
    		&& scheduler.getTotalPCBs()<15 && index<jobQ.size()){   		
    		canAllocate = mem_manager.allocate(jobQ.get(index).get(1));  			
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
    	ArrayList<Integer> newJob;   	
    	while(system.getFreeMemory()>=mem_manager.getMemCutoff() && scheduler.getTotalPCBs()<15
    			&& moreJobsInFile ){
    		newJob = getNextJob();
    		if(newJob.get(0) != 0){
    			canAllocate = mem_manager.allocate(newJob.get(1));
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
   
//		---Getters---
    public boolean hasMoreJobsInFile(){
        return moreJobsInFile;
     }
    public ArrayList<ArrayList<Integer>> getJobQ(){
    	return jobQ;
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