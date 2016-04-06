package xloc.pattern;

import java.util.regex.Pattern;

/**
 * Created by roy on 4/5/16.
 */
public class BlankPattern extends XLocPattern {
    private final Pattern pattern;

    public BlankPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public Boolean isMatch(String line){
        return pattern.matcher(line).matches();
    }
}
