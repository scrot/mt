package org.uva.rdewildt.mt.featureset.model;

public class Position implements Comparable {
    private final int line;
    private final int column;

    public Position(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public int compareTo(Object o) {
        Position p = (Position) o;
        if(this.getLine() != p.getLine()){
            return this.getLine() - p.getLine();
        }
        else{
            return this.getColumn() - p.getColumn();
        }
    }

}
