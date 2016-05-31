package org.uva.rdewildt.mt.gcrawler.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.URIish;

import java.nio.file.Path;
import java.nio.file.Paths;

public class GitClone {
    public static void gitClone(URIish gitUri, Path path){
        if(gitUri.isRemote()){
            try {
                Git.cloneRepository()
                    .setURI(gitUri.toString())
                    .setDirectory(Paths.get(path.toString(), gitUri.getHumanishName()).toFile())
                    .call()
                    .close();
                System.out.println("cloning into " + gitUri.getHumanishName() + " successful");
            } catch (GitAPIException e) {
                e.printStackTrace();
                System.out.println("cloning into " + gitUri.getHumanishName() + " failed");
            }
        }
    }
}
