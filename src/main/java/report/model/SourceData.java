package report.model;

import xloc.XLoc;

public class SourceData {
    // Source code data
    private XLoc xloc;
    private Integer fileCount;
    private Integer extentionCount;

    public SourceData(XLoc xloc, Integer fileCount, Integer extentionCount) {
        this.xloc = xloc;
        this.fileCount = fileCount;
        this.extentionCount = extentionCount;
    }

    public XLoc getXloc() {
        return xloc;
    }

    public Integer getFileCount() {
        return fileCount;
    }

    public Integer getExtentionCount() {
        return extentionCount;
    }
}
