package org.uva.rdewildt.mt.gcrawler.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.URIish;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GitClone {
    public static void gitClone(URIish gitUri, Path path){
        File projectroot= Paths.get(path.toString(), gitUri.getPath().replace('/','-').substring(1)).toFile();
        File gitroot = Paths.get(projectroot.toString(), ".git").toFile();

        if(gitUri.isRemote() && !projectroot.exists()){
            try {
                Git.cloneRepository()
                        .setURI(gitUri.toString())
                        .setDirectory(projectroot)
                        .call();
                System.out.println("cloning into " + gitUri.getHumanishName() + " successful");
            } catch (Exception e) {
                System.out.println("cloning into " + gitUri.getHumanishName() + " failed");
            }
        }
        else if (gitroot.exists()){
            try {
                Git git = Git.open(gitroot);

                StoredConfig config = git.getRepository().getConfig();
                config.setBoolean( "http", null, "sslVerify", false );
                config.save();

                git.reset().setMode(ResetCommand.ResetType.HARD).call();
                git.pull().call();
                git.close();

                System.out.println("pulling " + gitUri.getHumanishName() + " successful");
            } catch (Exception e) {
                System.out.println("pulling " + gitUri.getHumanishName() + " failed");
            }
        }
    }
}
