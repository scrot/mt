package distribution.language;

import distribution.language.structure.Java;
import distribution.language.structure.Language;
import distribution.language.visitor.Visitor;

public class ClassLanguage implements Visitor<Language, String> {

    @Override
    public Language visit(Java lang, String extention) {
        return null;
    }
}
