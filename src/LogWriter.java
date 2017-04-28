/**
 * ---Description---
 * The LogWriter serves to construct and write the log files
 * of the system in a neatly formatted manner. 
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogWriter {
	
//		---Class Variables---	

	private static final String MEM_STAT_PATH = "MEM_STAT";
	private static final String TRACE_PATH = "TRACE";

	private SYSTEM sys;
	private Mem_manager mem;
	private Scheduler sch;
	private Loader ld;

//		---Constructor---	
	
	public LogWriter(SYSTEM sysIn, Mem_manager memIn,
					 Scheduler schIn, Loader ldIn){
		sys = sysIn;
		mem = memIn;
		sch = schIn;
		ld = ldIn;
		setupMemStatFile();
		setupTraceFile();
	}

	/**
	 * Sets up the MEM_STAT file. The header is constructed from a
	 * String and then the method creates a file on the file system and
 	 * writes the constructed header to it.  Called during the object
	 * instantiation.
 	 */
	private void setupMemStatFile(){
		String header = "Mem_Stat file starting at CSX time of "
				+ SYSTEM.systemStartTime+
				" \n\nNormal | Job |  Job  |Alloca|Int. |Length| Page " +
				"|Clean|Dirty|     Stats     |"
				+  "\n Term. | ID  | Size  |Frames|Frag |" +
				"String|Faults| Rep | Rep |   FF  |   AF  |"
				+
				"\n*************************************************" +
				"****************************\n";
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

	/** Writes to the mem_stat file after every fourth job termination
	 * including the frame allocation statistics
	 */
	public void intervalWriteToMemStat(){
		String toWrite = String.format
				("=========================================" +
						"===================| %-4.3f | %-4.3f " +
						"|",	mem.getNumberOfFreeFrames()/128.0,
				mem.getNumberAllocatedFrames()/128.0);

		String dottedLine =  "\n-------------------------------------" +
				"----------------------------------------\n";
		File file = new File(MEM_STAT_PATH);
		try{
			FileWriter append = new FileWriter(file, true);
			append.write(toWrite);
			append.write(dottedLine);
			append.close();
		}
		catch(IOException e){
			System.out.println("Error writing to MEM_STAT" +
					" file during interval");
		}
	}

	/**
	 * Each time a job is terminated and the memory is released,
	 * this method is called and writes the job statics to the
	 * MEM_stat file
	 *
	 * @param job	 the job which just terminated
	 */
	public void releaseWriteToMemStat(PCB job){
		String toWrite = String.format(" %-5s | %-3d | %-5d |  %-3d | " +
						"%-3d | %-5d|  %-4d| %-4d|  %-3d|   -   |   -   |",
				Boolean.toString(job.isNormalTermination()), job.getJobID(),
				job.getJobSize(), job.getAllocatedFrames(), job
						.getInternalFragmentation(),
				job.getOriginalReferenceStringSize(),
				job.getNumberOfPageFaults(),
				job.getCleanPageReplacements(),
				job.getDirtyPageReplacements() );

		String dottedLine =  "\n---------------------------------------" +
				"--------------------------------------\n";

		File file = new File(MEM_STAT_PATH);
		try{
			FileWriter append = new FileWriter(file, true);
			append.write(toWrite);
			append.write(dottedLine);
			append.close();
		}
		catch(IOException e){
			System.out.println("Error writing to MEM_STAT " +
					"file during release");
		}
	}

	/**
	 * Sets up the TRACE file by constructing a string header
	 * then creates a file on the filesystem and writes the
	 * header to it.  Called during the object instantiation.
	 */
	public void setupTraceFile(){
		String header = "Trace file starting at CSX time of "
				+ SYSTEM.systemStartTime+
				" \n\nJob ID| Placed | Replaced | Frame |       Page Status "+
				"    |" +
				"\nNumber|      Pages        |Numbers| Referenced | Modified"+
				" |" +
				"\n*****************************" +
				"******************************\n";
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


	/**
	 * Called each time there is a page fault which results in loading
	 * a new page.  This prints the page statistics
	 * @param job	the job to which the page belongs
	 * @param pgNumber	the specific page entry in page table
	 */
	public void writeToTraceFileLoad(PCB job, int pgNumber){
		Page entry = mem.getPage(job, pgNumber);
		String toWrite = String.format("  %-3d |  %-3d  |     -     |  %-4d " +
						"|   %5s    |  %5s   |", job
						.getJobID(),
				pgNumber, entry.getFrameNumber(),
				Boolean.toString(mem.getReferenceBit(job
						.getPageTableBaseAddress(), pgNumber)), Boolean.toString
						(mem.getModifiedBit(job.getPageTableBaseAddress(),
								pgNumber)));

		String dottedLine =  "\n--------------------------------------" +
				"---------------------\n";
		File file = new File(TRACE_PATH);
		try{
			FileWriter append = new FileWriter(file, true);
			append.write(toWrite);
			append.write(dottedLine);
			append.close();
		}
		catch(IOException e){
			System.out.println("Error writing to TRACE file during release");
		}


	}

	/**
	 * Called each time there is a page fault which results in doing a
	 * page replacement on a frame.  Takes in the job, the page number
	 * of the current resident page which will be replaced, and the
	 * page which will be loaded and writes the statistics.
 	 * @param job	job to which the pages belong
	 * @param pgNumberToPlace	the page which will be loaded into
	 *                          the frame
	 * @param pgNumberVictim	the page which will be replaced by the
	 *                          new page being loaded
	 */
	public void writeToTraceFileREPlace(PCB job, int pgNumberToPlace, int
			pgNumberVictim){

		String toWrite = String.format("  %-3d |  %-3d  |     %-3d   |  %-4d " +
						"|   %5s    |  %5s   |", job
						.getJobID(),
				pgNumberToPlace, pgNumberVictim, mem
						.getFrameNumberFromPageTable(job
								.getPageTableBaseAddress(), pgNumberVictim),
				Boolean.toString(mem.getReferenceBit(job
						.getPageTableBaseAddress(), pgNumberVictim)),
				Boolean.toString(mem.getModifiedBit(job.getPageTableBaseAddress(),
								pgNumberVictim)));

		String dottedLine =  "\n--------------------------------------" +
				"---------------------\n";
		File file = new File(TRACE_PATH);
		try{
			FileWriter append = new FileWriter(file, true);
			append.write(toWrite);
			append.write(dottedLine);
			append.close();
		}
		catch(IOException e){
			System.out.println("Error writing to TRACE file during release");
		}


	}

}