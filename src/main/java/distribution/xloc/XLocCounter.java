package distribution.xloc;

public class XLocCounter {
    private Integer codeLines;
    private Integer commentLines;
    private Integer blankLines;
    private Integer unknownLines;

    public XLocCounter() {
        this.codeLines = 0;
        this.commentLines = 0;
        this.blankLines = 0;
        this.unknownLines = 0;
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

    public void incrementUnknownLines() { this.unknownLines++; }

    public XLoc getXLoc(){
        return new XLoc(this.codeLines, this.commentLines, this.blankLines, this.unknownLines);
    }
}
