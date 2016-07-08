package org.uva.rdewildt.mt.utils.model.git;

public class Author {
    private final String name;

    public Author(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Author) {
            Author author = (Author) obj;
            return author.getName().equalsIgnoreCase(this.getName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.getName().toLowerCase().hashCode();
    }
}
