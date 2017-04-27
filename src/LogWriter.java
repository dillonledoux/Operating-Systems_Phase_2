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
				" \n\nNormal|Job| Job |Alloca|Int. |Length| Page " +
				"|Clean|Dirty|     Stats     |"
				+  "\n Term.|ID |Size |Frames|Frag |" +
				"String|Faults| Rep | Rep |   FF  |   AF  |"
				+
				"\n************************************************************************\n";
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
		String toWrite = String.format("=====================================" +
						"==================|  %-4.1f |  %-4.1f " +
						"|",	mem.getPercentFreeFrames(),
				mem.getPercentAllocatedFrames());

		String dottedLine =  "\n----------------------------------" +
				"--------------------------------------\n";
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
		String toWrite = String.format(" %s | %-2d|%-5d|  %-3d | " +
						"%-3d | %-5d|  %-4d| %-4d|  %-3d|   -   |   -   |",
				Boolean.toString(job.isNormalTermination()), job.getJobID(),
				job.getJobSize(), job.getAllocatedFrames(), job
						.getInternalFragmentation(),
				job.getOriginalReferenceStringSize(), job.getNumberOfPageFaults(),
				job.getCleanPageReplacements(), job.getDirtyPageReplacements() );

		String dottedLine =  "\n----------------------------------" +
				"--------------------------------------\n";

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
				"\nNumber|     Page      |Numbers| Referenced | Modified |" +
				"\n*******************************************************\n";
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

	public void writeToTraceFile(PCB job, int pgNumber){
		Page entry = mem.getPage(job, pgNumber);
		String toWrite = String.format("  %-3d |      %-5d    |  %-4d " +
						"|   %s    |  %s   |", job
						.getJobID(),
				pgNumber, entry.getFrameNumber(),
				Boolean.toString(entry.isReferenced()), Boolean.toString(entry
						.isModified()));
		String dottedLine =  "\n----------------------------------" +
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