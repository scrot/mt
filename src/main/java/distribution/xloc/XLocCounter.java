package distribution.xloc;

public class XLocCounter {
    private Integer codeLines;
    private Integer commentLines;
    private Integer blankLines;

    public XLocCounter() {
        this.codeLines = 0;
        this.commentLines = 0;
        this.blankLines = 0;
    }

    public void incrementCodeLines() {
        this.codeLines++;
    }

    public void incrementCommentLines() {
        this.commentLines++;
    }

    public void incrementBlankLines() {
        this.blankLines++;
    }

    public XLoc getXLoc(){
        return new XLoc(this.codeLines, this.commentLines, this.blankLines);
    }
}
