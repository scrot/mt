package org.uva.rdewildt.mt.utils.lang;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class LanguageFactory {
    private final Map<String, Language> extentions;

    public LanguageFactory() {
        this.extentions = new HashMap<>();
        this.extentions.put("java", new Java());
        this.extentions.put("class", new Class());
    }

    public Language stringToLanguage(String string){
        if(this.extentions.containsKey(string.toLowerCase())){
            return this.extentions.get(string.toLowerCase());
        }
        else {
            return new Other();
        }
    }

    public Language classPathToLanguage(Path classPath){
        String extentionString = getExtension(classPath.getFileName().toString());
        if(this.extentions.containsKey(extentionString)){
            return this.extentions.get(extentionString);
        }
        else {
            return new Other();
        }
    }

    private String getExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i+1);
        }

        return extension;
    }


}
