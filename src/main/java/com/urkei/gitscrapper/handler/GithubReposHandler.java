/*
 * Copyright (c) 2022. URKEI.
 * All rights reserved.
 *
 * The copyright to the computer program(s) herein is the property of URKEI.
 * The program(s) may be used and/or copied only with the written permission
 * of the owner or in accordance with the terms and conditions stipulated in
 * the contract under which the program(s) have been supplied.
 */

package com.urkei.gitscrapper.handler;

import com.urkei.gitscrapper.client.GithubClient;
import com.urkei.gitscrapper.dto.git.Repo;
import com.urkei.gitscrapper.dto.tui.NewRepo;
import com.urkei.gitscrapper.publisher.RepoPublisher;
import com.urkei.gitscrapper.subscriber.RepoSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Handler for exposed REST API interface.
 */
@Component
public class GithubReposHandler {

    /** Client for accessing github REST API v3. */
    @Autowired
    private GithubClient githubClient;

    /**
     * Retrieve all repos with respective branches for specified user.
     *
     * @param request request contains parameter username, for which data is gathered.
     * @return complete date for user, containing repos and branches.
     */
    public Mono<ServerResponse> getReposWithBranches(ServerRequest request) {

        String username= request.pathVariable("username");

        RepoPublisher repoPublisher = new RepoPublisher();
        RepoSubscriber repoSubscriber = new RepoSubscriber(repoPublisher, githubClient);

        Flux<Repo> reposReponse = githubClient.getUserRepos(username);
        reposReponse.filter(repo -> repo.getFork() == false)
                .subscribe(repoSubscriber);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(
                        repoPublisher,
                        NewRepo.class))
                .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                        .flatMap(s -> ServerResponse.ok()
                                .contentType(MediaType.TEXT_PLAIN)
                                .bodyValue(s)));
    }

}
