package org.uva.rdewildt.mt.utils.model.report;

import java.util.List;
import java.util.Map;

/**
 * Created by roy on 5/26/16.
 */
public interface Reportable {
    Reportable getNewInstance();

    List<String> getKeys();

    Map<String, Object> getValues();

    void setValues(Map<String, Object> values) throws NoSuchFieldException;
}
