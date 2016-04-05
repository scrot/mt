package distribution.xloc;

public class XLoc {
    private final Integer codeLines;
    private final Integer commentLines;
    private final Integer blankLines;

    public XLoc(Integer codeLines, Integer commentLines, Integer blankLines) {
        this.codeLines = codeLines;
        this.commentLines = commentLines;
        this.blankLines = blankLines;
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

    public Integer getTotalLines() {
        return this.codeLines + this.commentLines + this.blankLines;
    }

    @Override
    public String toString() {
        return "Code lines: " + this.codeLines +
                "; Comment lines: " + this.commentLines +
                "; Blank lines: " + this.blankLines +
                "; Total lines: " + getTotalLines();
    }
}
