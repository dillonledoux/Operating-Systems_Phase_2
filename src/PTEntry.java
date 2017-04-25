/**
 * Created by dillonledoux on 4/21/17.
 */
public class PTEntry {

    private int vi = 0; // invalid if zero, valid if 1
    private boolean resident = false; // false is 0, true is 1
    private boolean reference = false;
    private boolean modified = false;
    private int frameNumber = -1;
    private static final int MAX_CAPACITY = 256;

  //  private int occupiedSpace = 0;

    public PTEntry() {}

    public int getVi() {
        return vi;
    }

    public void setVi(int vi) {
        this.vi = vi;
    }

    public boolean isResident() {
        return resident;
    }

    public void setResident() {
        resident = true;
    }

    public void clearResident(){
        resident = false;
    }

    public boolean isReferenced() {
        return reference;
    }

    public void setReference(boolean reference) {
        reference = reference;
    }

    public void clearReference(){
        reference = false;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        modified = true;
    }

    public void clearModified(){
        modified = false;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public void setFrameNumber(int frameNumber) {
        this.frameNumber = frameNumber;
    }
  /*
    public int getOccupiedSpace() {
        return occupiedSpace;
    }

    public void setOccupiedSpace(int occupiedSpace) {
        this.occupiedSpace = occupiedSpace;
    }
    */
}
