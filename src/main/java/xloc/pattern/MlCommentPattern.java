package xloc.pattern;

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
        line = emptyInlineStrings(line);

        if(startOfCommentPattern.matcher(line).find()){
            mlActive = true;
        }

        Boolean match = mlActive;

        if(endOfCommentPattern.matcher(line).find()){
            mlActive = false;
        }

        Boolean codeBeforeComment =Pattern.compile("\\S.*" + startOfCommentPattern.pattern()).matcher(line).find();
        Boolean codeAfterComment = Pattern.compile(endOfCommentPattern.pattern() + ".*\\S").matcher(line).find();
        return match && !codeBeforeComment && !codeAfterComment;
    }

    private String emptyInlineStrings(String line){
        Pattern enclosure = Pattern.compile("\".*\"");
        return enclosure.matcher(line).replaceAll("");
    }
}
