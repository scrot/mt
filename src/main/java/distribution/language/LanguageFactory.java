package distribution.language;

import distribution.language.structure.Java;
import distribution.language.structure.Language;
import distribution.language.structure.Other;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class LanguageFactory {
    private final Map<String, Language> extentions;

    public LanguageFactory() {
        this.extentions = new HashMap<>();
        this.extentions.put("java", new Java());
    }

    public Language classPathToLanguage(Path classPath){
        String extetionString = FilenameUtils.getExtension(classPath.toString());
        if(this.extentions.containsKey(extetionString)){
            return this.extentions.get(extetionString);
        }
        else {
            return new Other();
        }
    }
}
