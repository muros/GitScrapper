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

import com.urkei.gitscrapper.publisher.RepoPublisher;
import com.urkei.gitscrapper.client.GithubClient;
import com.urkei.gitscrapper.dto.git.Branch;
import com.urkei.gitscrapper.dto.git.Repo;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

/**
 * Subscriber for Repos when next repo is read, it starts reading
 * its branches. This joins data and makes cartesian product of
 * repo x branch. This is a single return value for API caller.
 *
 * This requires sub-calls to github REST API.
 */
public class RepoSubscriber implements Subscriber<Repo> {

    Logger logger = LoggerFactory.getLogger(RepoSubscriber.class);

    /** Publisher for repos. */
    private RepoPublisher repoPublisher;

    /** Github REST API client. */
    private GithubClient githubClient;
    public RepoSubscriber(RepoPublisher repoPublisher, GithubClient githubClient) {
        this.repoPublisher = repoPublisher;
        this.githubClient = githubClient;
    }

    @Override
    public void onSubscribe(Subscription s) {
        logger.debug(">> onSubscribe Repo");
        // Todo: shall we handle paging on github REST API?
        s.request(100);
    }

    @Override
    public void onNext(Repo repo) {
        logger.debug(">> onNext Repo " + repo.getName());
        repoPublisher.onNextRepo(repo.getName(), repo.getOwner().getLogin());
        repoPublisher.increaseRepoCnt();

        BranchSubscriber branchSubscriber = new BranchSubscriber(repo.getName(), repoPublisher);

        String branchesUrl = repo.getBranches_url();
        Flux<Branch> branchesResponse = githubClient.getBranches(branchesUrl);
        branchesResponse.subscribe(branchSubscriber);
    }

    @Override
    public void onError(Throwable t) {
        logger.debug(">> onError Repo " + t.getClass());
        if (t instanceof WebClientResponseException.NotFound) {
            repoPublisher.onNotFound(t);
        }
    }

    @Override
    public void onComplete() {
        logger.debug(">> onComplete Repo");
    }

}
