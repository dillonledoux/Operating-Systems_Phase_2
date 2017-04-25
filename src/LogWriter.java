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

	private static final String MEM_STAT_PATH = "MEM_STAT";
	private static final String TRACE_PATH = "TRACE";

	
	@SuppressWarnings("FieldCanBeLocal")
	private SYSTEM sys;
	private Mem_manager mem;
	@SuppressWarnings("FieldCanBeLocal")
	private Scheduler sch;
	@SuppressWarnings("FieldCanBeLocal")
	private Loader ld;

//		---Constructor---	
	
	public LogWriter(SYSTEM sysIn, Mem_manager memIn, Scheduler schIn, Loader ldIn){
		sys = sysIn;
		mem = memIn;
		sch = schIn;
		ld = ldIn;

		setupMemStatFile();
		setupTraceFile();
	}


	private void setupMemStatFile(){
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
						"   -    |     -     |  -   |   -    |   -    |%-5f   |%-5f   |", mem.getPercentFreeFrames(),
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
						"%-5d | %-5d | %-5d | %-5d | %-5d |   -   |   -   |",
				job.isNormalTermination(), job.getJobID(),
				job.getJobSize(), job.getAllocatedFrames(), job
						.getInternalFragmentation(),
				job.getOriginalReferenceStringSize(), job.getNumberOfPageFaults(),
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

	private void setupTraceFile(){
		String header = "Trace file starting at CSX time of "+ SYSTEM.systemStartTime+
				" \n\nJob ID|Placed/Replaced| Frame |       Page Status     |" +
				"\nNumber|     Page      |Numbers| Referenced | Modified |" +
				"\n*******************************************************";
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

	public void writeToTraceFile(PCB job, PTEntry entry, int pgNumber){
		String toWrite = String.format("%-5d | %-5d | %-5d | %-5d | %-5d", job.getJobID(),
				pgNumber, entry.getFrameNumber(),
				entry.isReferenced(), entry.isModified());
//		todo Do we need to deal with the dirty bit or write a special message?
	}

}