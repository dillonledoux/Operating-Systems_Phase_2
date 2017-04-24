/**
 * Created by Dillon LeDoux on 4/23/2017.
 */
public class ReferenceStringEntry {

    private String code = "";
    private int pageNumber;

    public ReferenceStringEntry(){

    }
    public ReferenceStringEntry(String entry){
        code = entry.substring(0, 1);
        pageNumber = Integer.parseInt(entry.substring(1));
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
}
