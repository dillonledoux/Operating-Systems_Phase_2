import java.util.ArrayList;

/**
 * Created by dillonledoux on 4/23/17.
 *
 * Description
 *
 * This method serves to find candidates for replacement among the pages
 * in the page table of a job as well as to clear reference bits once
 * a replacement occurs.
 */
public class Replacer {

    SYSTEM system;
    Mem_manager mem_manager;

    /**
     * Constructor
     */
    public Replacer(SYSTEM sysIn, Mem_manager memIn){
        system = sysIn;
        mem_manager = memIn;
    }

    /**
     * Given the address to a job's page table, this method examines the
     * page entries to find the optimal candidate for replacement then
     * returns the address of the found page.
     * @param pageTableAddress page table address of job
     * @return address of the page which should be replaced
     */
    public int findVictim(int pageTableAddress) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 128; j++) {
                if(mem_manager.getPtLib().get(pageTableAddress)[j].isResident()) {
                    if (i == 0) {
                        // Not Referenced and Not Modified
                        if (mem_manager.getPtLib().get(pageTableAddress)[j].isReferenced() == false &&
                                mem_manager.getPtLib().get(pageTableAddress)[j].isModified() == false) {
                            return j;
                        }
                    } else if (i == 1) {
                        // Not Referenced but Modified
                        if (mem_manager.getPtLib().get(pageTableAddress)[j].isReferenced() == false &&
                                mem_manager.getPtLib().get(pageTableAddress)[j].isModified() == true) {
                            return j;
                        }
                    } else if (i == 2) {
                        // Referenced but Not Modified
                        if (mem_manager.getPtLib().get(pageTableAddress)[j].isReferenced() == true &&
                                mem_manager.getPtLib().get(pageTableAddress)[j].isModified() == false) {
                            return j;
                        }
                    } else {
                        // Modified and Referenced
                        if (mem_manager.getPtLib().get(pageTableAddress)[j].isReferenced() == true &&
                                mem_manager.getPtLib().get(pageTableAddress)[j].isModified() == true) {
                            return j;
                        }
                    }
                }
            }
        }
        return -2;
    }

    /**
     * After a page replacement, this method is called to reset all the
     * reference bits ina given page table
     * @param pageTableAddress address of the page table to be operated on
     */
    public void clearReferenceBits(int pageTableAddress){
        for(int i = 0; i<128; i++) {
            mem_manager.clearReferenceBit(pageTableAddress, i);
        }
    }

}


