package distribution.language;

import distribution.Loc;
import distribution.language.structure.Java;
import distribution.language.structure.Other;
import distribution.language.visitor.Visitor;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LocCalculator implements Visitor<Loc,Path>  {
    @Override
    public Loc visit(Java lang, Path context) {
        List<String> excludes = new ArrayList<>();
        excludes.add("^\\s*\\\\.*$"); //single line comment
        excludes.add("\\\\*.*\\*\\"); //multi line comment
        excludes.add("^\\s*$"); //whitespace
        return calculateLoc(context, excludes);
    }

    @Override
    public Loc visit(Other lang, Path context) {
        return calculateLoc(context, new ArrayList<>());
    }

    private Loc calculateLoc(Path classPath, List<String> excludeRegexes){
        
        try {
            String reader = FileUtils.readFileToString(classPath.toFile());
            String[] sourceLines = filterOutMatches(reader, excludeRegexes).split("\\n");
            return new Loc(sourceLines.length);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String filterOutMatches(String string, List<String> regexStrings){
        return string.replaceAll(mergeRegexStrings(regexStrings), "");
    }

    private String mergeRegexStrings(List<String> regexStrings){
        StringBuilder regexBuilder = new StringBuilder();
        for(String regexString : regexStrings){
            if(isLastElement(regexString, regexStrings)){
                regexBuilder.append(regexString);
            }
            else{
                regexBuilder.append(regexString).append('|');
            }
        }
        return regexBuilder.toString();
    }

    private <T> Boolean isLastElement(T last, List<T> list){
        return last.equals(list.get(list.size() - 1));
    }
}
