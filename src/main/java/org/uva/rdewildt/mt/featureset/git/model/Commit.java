package org.uva.rdewildt.mt.featureset.git.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Commit {
    private final Object id;
    private final Author author;
    private final String message;
    private final Date date;

    public Commit(Object id, Author author, String message, Date date) {
        this.id = id;
        this.author = author;
        this.message = message;
        this.date = date;
    }

    public Object getId() {
        return id;
    }

    public Author getAuthor() {
        return author;
    }

    public String getMessage() {
        return this.message;
    }

    public Date getDate() {
        return this.date;
    }

    public List<Integer> getIssueNumbers(Pattern issue){
        List<Integer> issueNumbers = new ArrayList<>();
        if(this.containsIssues(issue)) {
            Matcher issueMatcher = Pattern.compile("#\\d+").matcher(this.getMessage());

            while (issueMatcher.find()) {
                String issueNumber = issueMatcher.group();
                Integer issueNr = Integer.parseInt(issueNumber.substring(1));
                issueNumbers.add(issueNr);
            }
        }
        return issueNumbers;
    }

    public Boolean containsIssues(Pattern issue) {
        return issue.matcher(this.getMessage()).find();
    }
}
