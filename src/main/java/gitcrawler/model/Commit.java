package gitcrawler.model;

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
    private final List<Path> files;

    public Commit(Object id, Author author, String message, Date date, List<Path> files) {
        this.id = id;
        this.author = author;
        this.message = message;
        this.date = date;
        this.files = files;
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

    public List<Path> getFiles() {
        return files;
    }

    public List<Integer> getIssueNumbers(Pattern issue){
        List<Integer> issueNumbers = new ArrayList<>();
        if(this.containsIssues(issue)) {
            Matcher issueMatcher = Pattern.compile("#\\d+").matcher(this.getMessage());

            while (issueMatcher.find()) {
                String issueNumber = issueMatcher.group();
                Integer issueNr = Integer.parseInt(issueNumber.substring(1));
                if (issueNr != null) {
                    issueNumbers.add(issueNr);
                }
            }
        }
        return issueNumbers;
    }

    public Boolean containsIssues(Pattern issue) {
        return issue.matcher(this.getMessage()).find();
    }
}
