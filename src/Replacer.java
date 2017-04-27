import java.util.ArrayList;

/**
 * Created by dillonledoux on 4/23/17.
 */
public class Replacer {

    SYSTEM system;
    Mem_manager mem_manager;

    public Replacer(SYSTEM sysIn, Mem_manager memIn){
        system = sysIn;
        mem_manager = memIn;
    }

    public int findVictim(int pageTableAddress) {
        for (int i = 0; i < 4; i++) {
            Page[] pgTbl = mem_manager.getPtLib()
                    .get(pageTableAddress);

            for (int j = 0; j < 128; j++) {
                if (i == 0) {
                    if (pgTbl[j].isReferenced() == false &&
                            pgTbl[j].isModified() == false) {
                        return j;
                    }
                } else if (i == 1) {
                    if (pgTbl[j].isReferenced() == false &&
                            pgTbl[j].isModified() == true) {
                        return j;
                    }
                } else if (i == 2) {
                    if (pgTbl[j].isReferenced() == true &&
                            pgTbl[j].isModified() == false) {
                        return j;
                    }
                } else {
                    if (pgTbl[j].isReferenced() == true &&
                            pgTbl[j].isModified() == true) {
                        return j;
                    }
                }
            }
        }
        return -2;
    }


    public void clearReferenceBits(int pageTableAddress){
        for(int i = 0; i<128; i++) {
            mem_manager.clearReferenceBit(pageTableAddress, i);
        }
    }

}


