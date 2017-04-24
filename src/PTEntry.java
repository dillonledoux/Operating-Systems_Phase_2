/**
 * Created by dillonledoux on 4/21/17.
 */
public class PTEntry {

    private int vi = 0;
    private boolean resident = false; // false is 0, true is 1
    private boolean reference = false;
    private boolean modified = false;
    private int frameNumber = -1;

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

    public void setResident(boolean resident) {
        this.resident = resident;
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

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public void setFrameNumber(int frameNumber) {
        this.frameNumber = frameNumber;
    }
}
