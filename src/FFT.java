import java.util.LinkedList;

/**
 * Created by dillonledoux on 4/21/17.
 */
public class FFT {

    private LinkedList<Integer> table = new LinkedList<>();

    public FFT() {
        for(int i = 0; i<128; i++){
          table.addFirst(i);
        }
    }

    public int getNumberOfFreeFrames(){
        return table.size();
    }
    public int getNumberAllocatedFrames(){
        return 256-table.size();
    }


}
