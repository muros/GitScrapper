/*
 * Copyright (c) 2022. URKEI.
 * All rights reserved.
 *
 * The copyright to the computer program(s) herein is the property of URKEI.
 * The program(s) may be used and/or copied only with the written permission
 * of the owner or in accordance with the terms and conditions stipulated in
 * the contract under which the program(s) have been supplied.
 */

package com.urkei.gitscrapper.publisher;

import com.urkei.gitscrapper.dto.git.Branch;
import com.urkei.gitscrapper.dto.tui.NewBranch;
import com.urkei.gitscrapper.dto.tui.NewRepo;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Publisher of complete data gathered form github REST API.
 *
 * Publisher is used to provide multiple calls to github and collecting
 * all data, before returning to caller.
 */
public class RepoPublisher implements Publisher<NewRepo> {

    Logger logger = LoggerFactory.getLogger(RepoPublisher.class);

    /** Subscriber to provided data - only one for now. */
    private Subscriber<? super NewRepo> subscriber;

    /** Map of all repos with key repo name and value containg all repo data. */
    private Map<String, NewRepo> repos = new HashMap<>();

    /** Counting of retrieved repos, to know when to stop. */
    private int repoCnt;

    /** Total number of repos. */
    private int branchRepoCnt;

    /** Subscriber to repo data. */
    private Subscription subscription;

    /** When subscriber subscribes. */
    @Override
    public void subscribe(Subscriber<? super NewRepo> s) {
        logger.debug(">>> RepoPublisher.subscribe() " + s);
        this.subscriber = s;

        this.subscription = new Subscription() {
            @Override
            public void request(long n) {
                logger.debug("SUBSCRIPTION " + n);
            }

            @Override
            public void cancel() {
                logger.debug("CANCEL subscription");
            }
        };
        this.subscriber.onSubscribe(subscription);
    }

    /**
     * When next repo is read over REST API from github.
     *
     * @param repoName name of repo
     * @param repoOwner login name of github user
     */
    public void onNextRepo(String repoName, String repoOwner) {

        NewRepo repo = new NewRepo(repoName, repoOwner);
        repos.put(repoName, repo);
    }

    /**
     * When next branch is read over REST API from github.
     *
     * @param repoName branch repo
     * @param branch branch name
     */
    public void onNextBranch(String repoName, Branch branch) {

        this.repos.get(repoName).getBranches().add(new NewBranch(branch.getName(), branch.getCommit().getSha()));
    }

    /**
     * When read of branches for repo is finished.
     *
     * @param repoName name of repo
     */
    public void onCompleteBranch(String repoName) {
        NewRepo repo = this.repos.get(repoName);
        if (this.subscriber != null) {
            this.branchRepoCnt++;
            this.subscriber.onNext(repo);
        }
    }

    /**
     * When last repo is read.
     * @param repoName name of repo
     */
    public void onCompleteRepo(String repoName) {
        if (this.repoCnt == this.branchRepoCnt) {
            this.subscriber.onComplete();
        }
    }

    public void increaseRepoCnt() {
        this.repoCnt++;
    }

    /**
     * When repo is not found.
     *
     * @param t not found exception.
     */
    public void onNotFound(Throwable t) {
        this.subscriber.onError(t);
    }

}
