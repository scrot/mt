package org.uva.rdewildt.mt.lims;

import java.util.List;
import java.util.Map;

/**
 * Created by roy on 5/26/16.
 */
public interface Reportable {
    List<String> getKeys();
    Map<String, Object> getValues();
}
