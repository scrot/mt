package org.uva.rdewildt.mt.fpms;

import org.apache.maven.cli.MavenCli;
import org.eclipse.jgit.util.io.NullOutputStream;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BuildUtils {
    public static void buildProject(Path gitRoot) {
        try {
            if (isGradlePath(gitRoot)) {
                ProjectConnection gradle = GradleConnector
                        .newConnector()
                        .useBuildDistribution()
                        .forProjectDirectory(gitRoot.toFile())
                        .connect();
                gradle.newBuild()
                        .setStandardOutput(NullOutputStream.INSTANCE)
                        .setStandardError(NullOutputStream.INSTANCE)
                        .forTasks("clean", "build")
                        .run();
                gradle.close();
            } else if (isMavenPath(gitRoot)) {
                MavenCli cli = new MavenCli();
                PrintStream devnull = new PrintStream(NullOutputStream.INSTANCE);
                cli.doMain(new String[]{"clean", "compile"}, Paths.get(gitRoot.toString(), "pom.xml").toString(),
                        devnull, devnull);
            } else {
                throw new IOException("no maven/gradle project found in path " + gitRoot.toString());
            }
        } catch (Exception ignore){}
    }

    private static Boolean isMavenPath(Path path) {
        return Paths.get(path.toString(), "pom.xml").toFile().exists();
    }

    private static Boolean isGradlePath(Path path) {
        return Paths.get(path.toString(), "build.gradle").toFile().exists();
    }
}