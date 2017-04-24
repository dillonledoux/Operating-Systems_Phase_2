/**
 * Created by dillonledoux on 4/23/17.
 */
public class Replacer {

    SYSTEM system;
    Mem_manager mem_manager;

    public Replacer(SYSTEM sysIn, Mem_manager memIn){
        system = sysIn;
        mem_manager = memIn;
    };

    public int findVictim(int pageTableAddress){
        int victimAddress;

        outerloop:
        while(true){
            for(int i = 0; i<128; i++) {
//                todo here ^ need to figure out the max length of each pg table

                // returns the first page that is resident but also
                // unreferenced and unmodified
                if (!mem_manager.getPageTables().get(pageTableAddress)
                        .get(i).isReferenced() && !mem_manager.getPageTables()
                        .get(pageTableAddress).get(i).isModified() &&
                        mem_manager.getPageTables().get(pageTableAddress)
                        .get(i).isResident()) {
                    victimAddress = i;
                    break outerloop;
                }
            }
            // returns the first page that is modified but not referenced
            for(int i = 0; i<128; i++) {
                if (!mem_manager.getPageTables().get(pageTableAddress)
                        .get(i).isReferenced() && mem_manager.getPageTables()
                        .get(pageTableAddress).get(i).isModified()) {
                    victimAddress = i;
                    break outerloop;
                }
            }
            // returns the first page that is referenced but not modified
            for(int i = 0; i<128; i++) {
                if(mem_manager.getPageTables().get(pageTableAddress)
                        .get(i).isReferenced() && !mem_manager.getPageTables()
                        .get(pageTableAddress).get(i).isModified()) {
                    victimAddress = i;
                    break outerloop;
                }
            }
            // returns the first page that is referenced and modified
            for(int i = 0; i<128; i++) {
                if (mem_manager.getPageTables().get(pageTableAddress)
                        .get(i).isReferenced() && mem_manager.getPageTables()
                        .get(pageTableAddress).get(i).isModified()) {
                    victimAddress = i;
                    break outerloop;
                }
            }

        }
        for(int i = 0; i<128; i++) {
            mem_manager.getPageTables().get(pageTableAddress)
                    .get(i).clearReference();
        }
        return victimAddress;
    }

}
