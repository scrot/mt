package distribution.xloc;

public class XLoc {
    private final Integer codeLines;
    private final Integer commentLines;
    private final Integer blankLines;
    private final Integer unknownLines;

    public XLoc(Integer codeLines, Integer commentLines, Integer blankLines, Integer unknownLines) {
        this.codeLines = codeLines;
        this.commentLines = commentLines;
        this.blankLines = blankLines;
        this.unknownLines = unknownLines;
    }

    public Integer getCodeLines() {
        return this.codeLines;
    }

    public Integer getCommentLines() {
        return this.commentLines;
    }

    public Integer getBlankLines() {
        return this.blankLines;
    }

    public Integer getUnknownLines() { return this.unknownLines; }

    public Integer getTotalLines() {
        return this.codeLines + this.commentLines + this.blankLines + this.unknownLines;
    }

    @Override
    public String toString() {
        return "Code lines: " + this.codeLines +
                "; Comment lines: " + this.commentLines +
                "; Blank lines: " + this.blankLines +
                "; Unknown lines: " + this.unknownLines +
                "; Total lines: " + getTotalLines();
    }

    public XLoc add(XLoc rhs){
        return new XLoc(
                this.codeLines + rhs.getCodeLines(),
                this.commentLines + rhs.getCommentLines(),
                this.blankLines + rhs.getBlankLines(),
                this.unknownLines + rhs.getUnknownLines());
    }
}
