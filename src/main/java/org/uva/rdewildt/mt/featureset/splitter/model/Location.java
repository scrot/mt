package org.uva.rdewildt.mt.featureset.splitter.model;

import java.nio.file.Path;

public class Location implements Comparable {
    private final Position start;
    private final Position end;
    private Path source;

    public Location(Position start, Position end, Path source) {
        this.start = start;
        this.end = end;
        this.source = source;
    }

    public Position getStart() {
        return start;
    }

    public Position getEnd() {
        return end;
    }

    public Path getSource() {
        return source;
    }

    @Override
    public int compareTo(Object o) {
        Location l = (Location) o;
        if(this.start.compareTo(l.start) < 0){
            return -1;
        }
        else if(this.start.compareTo(l.start) > 0){
            return 1;
        }
        else {
            if(this.end.compareTo(l.end) < 0){
                return -1;
            }
            else if(this.end.compareTo(l.end) > 0){
                return 1;
            }
            else {
                return 0;
            }
        }
    }
}
