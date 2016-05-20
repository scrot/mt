package lang;

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
        String extentionString = FilenameUtils.getExtension(classPath.toString());
        if(this.extentions.containsKey(extentionString)){
            return this.extentions.get(extentionString);
        }
        else {
            return new Other();
        }
    }
}
