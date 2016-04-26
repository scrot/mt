import distr.FaultDistribution;
import distr.Percentage;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args){
        String projectRoot = "C:\\Users\\royw\\Workspace\\junit4\\src\\main\\java";
        Path path = FileSystems.getDefault().getPath(projectRoot);
        try {
            /*
            CodeDistribution codeDistribution = new CodeDistribution(path);
            for(Double i = 0.0; i <= 100.0; i+=10){
                System.out.println(
                        "A partion of the distribution of " + i + "% results in "
                                + codeDistribution.cumulativePercentageOfPartition(new Percentage(i)).toString());
            }
            */
            FaultDistribution faultDistribution = new FaultDistribution(path, "scrot/test");
            for(Double i = 0.0; i <= 100.0; i+=10){
                System.out.println(
                        "A partion of the distribution of " + i + "% results in " +
                                faultDistribution.cumulativePercentageOfPartition(new Percentage(i)).toString());
            }

            FaultDistribution faultDistribution2 = new FaultDistribution(path, "https://gitlab.com/", "scrot","test");
            for(Double i = 0.0; i <= 100.0; i+=10){
                System.out.println(
                        "A partion of the distribution of " + i + "% results in " +
                                faultDistribution2.cumulativePercentageOfPartition(new Percentage(i)).toString());
            }


            System.out.println();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
