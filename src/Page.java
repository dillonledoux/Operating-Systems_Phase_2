/**
 * Created by dillonledoux on 4/21/17.
 *
 * Custom data structure for the a page entry to the page table
 * holding the necessary information about the page
 */
public class Page {

    private int vi = 0; // invalid if zero, valid if 1
    private boolean resident;
    private boolean reference;
    private boolean modified;
    private int frameNumber;

    /**
     * Constructor
     */
    public Page() {
        resident = false;
        reference = false;
        modified = false;
        frameNumber = -1;
    }

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
        this.reference = reference;
    }
    public boolean isModified() {
        return modified;
    }
    public void setModified() {
        modified = true;
    }
    public int getFrameNumber() {
        return frameNumber;
    }
    public void setFrameNumber(int frameNumber) {
        this.frameNumber = frameNumber;
    }

}
