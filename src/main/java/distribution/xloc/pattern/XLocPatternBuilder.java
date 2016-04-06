package distribution.xloc.pattern;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roy on 4/5/16.
 */
public class XLocPatternBuilder {
    private final List<BlankPattern> blankPatterns;
    private final List<CommentPattern> commentPatterns;
    private final List<CodePattern> codePatterns;
    private final List<UnknownPattern> unknownPatterns;


    public XLocPatternBuilder() {
        this.blankPatterns = new ArrayList<>();
        this.commentPatterns = new ArrayList<>();
        this.codePatterns = new ArrayList<>();
        this.unknownPatterns = new ArrayList<>();
    }

    public void addBlankPattern(BlankPattern blankPattern){
        this.blankPatterns.add(blankPattern);
    }

    public void addCommentPattern(CommentPattern commentPattern){
        this.commentPatterns.add(commentPattern);
    }

    public void addCodePattern(CodePattern codePattern){
        this.codePatterns.add(codePattern);
    }

    public void addUnknownPattern(UnknownPattern unknownPattern) { this.unknownPatterns.add(unknownPattern);}

    public Boolean isBlankLine(String line){
        for (XLocPattern pattern : this.blankPatterns){
            if(pattern.isMatch(line)){
                return true;
            }
        }
        return false;
    }

    public Boolean isCommentLine(String line){
        for (XLocPattern pattern : this.commentPatterns){
            if(pattern.isMatch(line)){
                return true;
            }
        }
        return false;
    }

    public Boolean isCodeLine(String line){
        for (XLocPattern pattern : this.codePatterns){
            if(pattern.isMatch(line)){
                return true;
            }
        }
        return false;
    }

    public Boolean isUnknownLine(String line){
        for (XLocPattern pattern : this.unknownPatterns){
            if(pattern.isMatch(line)){
                return true;
            }
        }
        return false;
    }
}
