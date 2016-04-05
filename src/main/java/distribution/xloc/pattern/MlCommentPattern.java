package distribution.xloc.pattern;

import java.util.regex.Pattern;

/**
 * Created by roy on 4/5/16.
 */
public class MlCommentPattern extends CommentPattern {
    private final Pattern startOfCommentPattern;
    private final Pattern endOfCommentPattern;
    private Boolean mlActive;

    public MlCommentPattern(Pattern startOfCommentPattern, Pattern endOfCommentPattern) {
        this.startOfCommentPattern = startOfCommentPattern;
        this.endOfCommentPattern = endOfCommentPattern;
        this.mlActive = false;
    }

    @Override
    public Boolean isMatch(String line){
        if(startOfCommentPattern.matcher(line).find()){
            mlActive = true;
        }

        if(endOfCommentPattern.matcher(line).find()){
            mlActive = false;
        }

        if(mlActive){
            return true;
        }

        return false;
    }
}
