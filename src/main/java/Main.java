import pareto.distribution.CodeDistribution;
import pareto.distribution.Percentage;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args){
        String projectRoot = "C:\\Users\\royw\\Workspace\\junit4\\src\\main\\java";
        Path path = FileSystems.getDefault().getPath(projectRoot);
        try {
            CodeDistribution distribution = new CodeDistribution(path);
            for(Double i = 0.0; i <= 100.0; i+=10){
                System.out.println(
                        "A partion of the distribution of " + i + "% results in " +
                        distribution.cumulativeXLocPercentageOfPartition(new Percentage(i)).toString());
            }
            System.out.println();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
