/**
 * Created by Dillon LeDoux on 4/23/2017.
 *
 * Description
 * Custom data type which holds the values of the instructions of
 * each job.  The string "code" holds whether the instruction
 * requires a read, write, or processing and the "page" holds
 * the page number of the page being referenced
 */
public class ReferenceStringEntry {

    /**
     * Class Variables
     */
    private String code;
    private int pageNumber;

    /**
     * Constructor
     *
     * Sets up a ReferenceStringEntry object given a String.
     * The first part fo the string is the instruction code
     * while the second is the page reference
     * @param entry string entry which is deconstructed into
     *              the code and page number variables
     */
    public ReferenceStringEntry(String entry){
        code = entry.substring(0, 1);
        pageNumber = Integer.parseInt(entry.substring(1));
    }


    /**
     * Getters
     *
     * Methods below serve to access the variable held in
     * this object.
     *
     */
    public String getCode() {
        return code;
    }
    public int getPageNumber() {
        return pageNumber;
    }

}
