/*
 * ---Description---
 * The LogWriter serves to construct and write the log files
 * of the system in a neatly formatted manner. 
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogWriter {
	
//		---Class Variables---	
	
	private static final String SYS_LOG_PATH = "SYS_LOG";  
	private static final String JOB_LOG_PATH = "JOB_LOG";
	private static final String MEM_STAT_PATH = "MEM_STAT";
	private static final String TRACE_PATH = "TRACE";

	
	SYSTEM sys;
	Mem_manager mem;
	Scheduler sch;
	Loader ld;

//		---Constructor---	
	
	public LogWriter(SYSTEM sysIn, Mem_manager memIn, Scheduler schIn, Loader ldIn){
		sys = sysIn;
		mem = memIn;
		sch = schIn;
		ld = ldIn;
		
		setupSysLog();
		setupJobLog();
	}
	
//		---SYS_LOG Setup and Write---	
	/**
	 * Creates a new file titles SYS_LOG if its not already created
	 * and writes the header of the file containing some organization
	 * information.
	 */
	public static void setupSysLog(){
    	String header1 = "SYS_LOG file starting at CSX time of "+ SYSTEM.systemStartTime;
		String header2 = "\n\nVirtual |  Free  |  Used  |  Job  | Blocked | Ready |   Jobs    |"
						+  "\n Clock  | Memory | Memory | Queue |  Queue  | Queue | Delivered |"
						+  "\n*****************************************************************\n";						
    	File file = new File(SYS_LOG_PATH);	    	
		try{
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
			writer.write(header1);
			writer.write(header2);
			writer.close();
		}
		catch(IOException e){
			System.out.println("Cannot Create SYS_LOG.txt");
		}		
    }
    /**
     * This method writes the system's current statistics when invoked
     */
	public void writeToSysLog(){
        String toWriteMem = mem.memStats();
        String toWriteJobs = String.format("  %-2d   |    %-2d   |  %-2d   |    %-3d    |", 
        						ld.getJobQ().size(),sch.getBlockedQ().size(),sch.getRQSize(),
        						sys.getJobsDelivered());
        String dottedLine = "\n-----------------------------------------------------------------\n";		     		
        File file = new File(SYS_LOG_PATH);
    	try{
    		FileWriter append = new FileWriter(file, true);
        	append.write(toWriteMem);
        	append.write(toWriteJobs);
        	append.write(dottedLine);
        	append.close();
    	}
    	catch(IOException e){
    		System.out.println("Error while writing to SYS_LOG");
    		e.printStackTrace();
    	}                                 
     }
        
//		---JOB_LOG Setup and Write---  	
	/**
	 * Creates a new file titles JOB_LOG if its not already created
	 * and writes the header of the file containing some organization
	 * information.
	 */
    public static void setupJobLog(){
        String header = "JOB_LOG file starting at CSX time of "+ SYSTEM.systemStartTime+
        			 " \n\nJob ID | Time of |   Time of   | Execution |  CPU  |"
        			 +  "\nNumber | Arrival | Termination |    Time   | Shots |"
        			 +  "\n****************************************************\n";
        File file = new File(JOB_LOG_PATH);
        try{    
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
			writer.write(header);
			writer.close();		    
		}
		catch (IOException e){
		    System.out.println("Error while initializing JOB_LOG");    	
		}
    }
    /**
     * Given a job, this method writes the job's statistics when invoked
     */
    public void writeToJobLog(PCB job){
        String toWrite = job.jobStats();
        String dottedLine ="\n----------------------------------------------------\n";
        File file = new File(JOB_LOG_PATH);
    	try{
    		FileWriter append = new FileWriter(file, true);
    		append.write(toWrite);
    		append.write(dottedLine);
        	append.close();
    	}
    	catch(IOException e){
    		System.out.println("Error writing to JOB_LOG");
    	}        
    }             


    public void setupMemStatFile(){
		String header = "Mem_Stat file starting at CSX time of "+ SYSTEM.systemStartTime+
				" \n\nNormal|Job ID|Job Size|Allocated|Internal|   Length  | Page |Clean Pg|Dirty Pg|Utilization Stats|"
				+  "\n Term.|Number|(Bytes) |  Frames |  Frag. |Ref. String|Faults|  Repl. |  Repl. |Free Fr.|Allo Fr.|"
				+  "\n*************************************************************************************************\n";
		File file = new File(MEM_STAT_PATH);
		try{
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
			writer.write(header);
			writer.close();
		}
		catch (IOException e){
			System.out.println("Error while initializing MEM_STAT file");
		}
	}

	// writes to the mem_stat file after every fourth job termination
	public void intervalWriteToMemStat(){
    	String toWrite = String.format("   -   |  -   |   -    |    -    |" +
				"   -    |     -     |  -   |   -    |   -    |%-5d   |%-5d   |", mem.getPercentFreeFrames(),
				mem.getPercentAllocatedFrames());

		String dottedLine =  "\n----------------------------------" +
				"---------------------------------------------------------------\n";
		File file = new File(MEM_STAT_PATH);
		try{
			FileWriter append = new FileWriter(file, true);
			append.write(toWrite);
			append.write(dottedLine);
			append.close();
		}
		catch(IOException e){
			System.out.println("Error writing to MEM_STAT file during interval");
		}
	}

	public void releaseWriteToMemStat(PCB job){
		String toWrite = String.format("%-5d |%-5d  |%-5d  |%-5d  | " +
				"%-5d | %-5d | %-5d | %-5d | %-5d |   -   |   -   |", job.isNormalTermination(), job.getJobID(),
				job.getJobSize(), /*Allocated Frames*/, /*Internal Frag*/,
				job.getReferenceString().length() /*Not Sure*/, job.getNumberOfPageFaults(),
				job.getCleanPageReplacements(), job.getDirtyPageReplacements() );

		String dottedLine =  "\n----------------------------------" +
				"---------------------------------------------------------------\n";

		File file = new File(MEM_STAT_PATH);
		try{
			FileWriter append = new FileWriter(file, true);
			append.write(toWrite);
			append.write(dottedLine);
			append.close();
		}
		catch(IOException e){
			System.out.println("Error writing to MEM_STAT file during release");
		}
	}

	public void setupTraceFile(){
		String header = "Trace file starting at CSX time of "+ SYSTEM.systemStartTime+
				" \n\nJob ID|Placed/Replaced| Frame |       Page Status     |" +
				     "Number|     Page      |Numbers| Referenced | Modified |" +
				"    ********************************************************";
		File file = new File(TRACE_PATH);
		try{
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
			writer.write(header);
			writer.close();
		}
		catch (IOException e){
			System.out.println("Error while initializing TRACE file");
		}
	}

	public void writeToTraceFile(PCB job){
		String toWrite = String.format("%-5d | %-5d | %-5d | %-5d | %-5d", job.getJobID(),
				/* placed/repl pages*/,job.getPageTable().getFrameNumber(),
				job.getPageTable().isReferenced(), job.getPageTable().isModified());
	}

}