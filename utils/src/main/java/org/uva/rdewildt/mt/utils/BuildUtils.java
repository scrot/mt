package org.uva.rdewildt.mt.utils;

import org.apache.maven.cli.MavenCli;
import org.gradle.jarjar.org.apache.commons.io.output.NullOutputStream;
import org.gradle.jarjar.org.apache.commons.lang.SystemUtils;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BuildUtils {
    public static void buildProject(Path gitRoot) {
        try {
            if (isGradlePath(gitRoot)) {
                buildGradleProjectFallback(gitRoot);
            } else if (isMavenPath(gitRoot)) {
                buildMavenProjectFallback(gitRoot);
            } else {
                throw new IOException("no maven/gradle project found in path " + gitRoot.toString());
            }
        } catch (Exception e){e.printStackTrace();}
    }

    private static void buildMavenProject(Path path) {
        System.setProperty("maven.multiModuleProjectDirectory", path.toString());
        MavenCli cli = new MavenCli();
        PrintStream devnull = new PrintStream(NullOutputStream.NULL_OUTPUT_STREAM);
        cli.doMain(new String[]{"clean", "compile"}, Paths.get(path.toString(), "pom.xml").toString(),
                devnull, devnull);
    }

    private static void buildGradleProject(Path path){
        ProjectConnection gradle = GradleConnector
                .newConnector()
                .useBuildDistribution()
                .forProjectDirectory(path.toFile())
                .connect();
        gradle.newBuild()
                .setStandardOutput(NullOutputStream.NULL_OUTPUT_STREAM)
                .setStandardError(NullOutputStream.NULL_OUTPUT_STREAM)
                .forTasks("clean", "build")
                .run();
        gradle.close();
    }

    private static void buildGradleProjectFallback(Path path) throws IOException, InterruptedException {
        File gradlew = new File(Paths.get(path.toString(), "gradlew").toUri());
        if(gradlew.isFile()) {
            Process proc = Runtime.getRuntime().exec(gradlew.toString() + "clean build -p" + path.toString());
            proc.waitFor();
        } else {
            Process proc = Runtime.getRuntime().exec("gradle clean build -p" + path.toString());
            proc.waitFor();
        }
    }

    private static void buildMavenProjectFallback(Path path) throws IOException, InterruptedException {
        Process proc = Runtime.getRuntime().exec("mvn clean compile -f" + path);
        proc.waitFor();
    }

    private static Boolean isMavenPath(Path path) {
        return Paths.get(path.toString(), "pom.xml").toFile().exists();
    }

    private static Boolean isGradlePath(Path path) {
        return Paths.get(path.toString(), "build.gradle").toFile().exists();
    }
}
