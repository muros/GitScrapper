/*
 * Copyright (c) 2022. URKEI.
 * All rights reserved.
 *
 * The copyright to the computer program(s) herein is the property of URKEI.
 * The program(s) may be used and/or copied only with the written permission
 * of the owner or in accordance with the terms and conditions stipulated in
 * the contract under which the program(s) have been supplied.
 */

package com.urkei.gitscrapper.subscriber;

import com.urkei.gitscrapper.dto.git.Branch;
import com.urkei.gitscrapper.publisher.RepoPublisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Subscriber to listen for Branches read from github REST API.
 */
public class BranchSubscriber implements Subscriber<Branch> {

    Logger logger = LoggerFactory.getLogger(BranchSubscriber.class);

    /** Repository name. */
    private String repoName;

    /** Publisher where read data is published. */
    private RepoPublisher repoPublisher;

    public BranchSubscriber(String repoName, RepoPublisher repoPublisher) {
        this.repoName = repoName;
        this.repoPublisher = repoPublisher;
    }

    @Override
    public void onSubscribe(Subscription s) {
        logger.debug(">> onSubscribe Branch for repo " + this.repoName);
        s.request(100);
    }

    @Override
    public void onNext(Branch branch) {
        logger.debug(">> onNext Branch " + this.repoName + "/" +branch.getName());
        repoPublisher.onNextBranch(this.repoName, branch);
    }

    @Override
    public void onError(Throwable t) {
        logger.debug(">> onError Branch");
    }

    @Override
    public void onComplete() {
        logger.debug(">> onComplete Branch " + this.repoName);
        repoPublisher.onCompleteBranch(this.repoName);
        repoPublisher.onCompleteRepo(this.repoName);
    }
}
