package org.uva.rdewildt.mt.xloc.pattern;

import java.util.regex.Pattern;

public class UnknownPattern extends XLocPattern {
    private final Pattern pattern;

    public UnknownPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public Boolean isMatch(String line) {
        return pattern.matcher(line).matches();
    }
}
