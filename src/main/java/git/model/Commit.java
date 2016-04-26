package git.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Commit {
    private final String id;
    private final String message;
    private final Date date;
    private final List<Path> files;

    private final Pattern issueClosePattern;

    public Commit(String id, String message, Date date, List<Path> files, Pattern issueClosePattern) {
        this.id = id;
        this.message = message;
        this.date = date;
        this.files = files;
        this.issueClosePattern = issueClosePattern;
    }

    public String getId() {
        return id;
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

    public List<Integer> getIssueNumbers(){
        List<Integer> issueNumbers = new ArrayList<>();
        if(this.containsIssues()) {
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

    public Boolean containsIssues() {
        return this.issueClosePattern.matcher(this.getMessage()).find();
    }
}
