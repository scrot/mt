package distribution.xloc.pattern;

import java.util.regex.Pattern;

/**
 * Created by roy on 4/5/16.
 */
public class CodePattern extends XLocPattern {
    private final Pattern pattern;

    public CodePattern(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public Boolean isMatch(String line){
        return pattern.matcher(line).find();
    }
}
