package org.uva.rdewildt.mt.fpms;

import org.apache.maven.cli.MavenCli;
import org.eclipse.jgit.util.io.NullOutputStream;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BuildUtils {
    public static void buildProject(Path gitRoot) {
        if (isGradlePath(gitRoot)) {
            ProjectConnection gradle = GradleConnector
                    .newConnector()
                    .forProjectDirectory(gitRoot.toFile())
                    .connect();
            gradle.newBuild()
                    .forTasks("clean", "build")
                    .setStandardOutput(NullOutputStream.INSTANCE)
                    .run();
            gradle.close();
        } else if (isMavenPath(gitRoot)) {
            MavenCli cli = new MavenCli();
            PrintStream devnull = new PrintStream(NullOutputStream.INSTANCE);
            cli.doMain(new String[]{"clean", "compile"}, Paths.get(gitRoot.toString(), "pom.xml").toString(),
                    devnull, devnull);
        } else {
            System.out.println("no maven/gradle project found in path " + gitRoot.toString());
        }
    }

    private static Boolean isMavenPath(Path path) {
        return Paths.get(path.toString(), "pom.xml").toFile().exists();
    }

    private static Boolean isGradlePath(Path path){
        return Paths.get(path.toString(), "build.gradle").toFile().exists();
    }
}
