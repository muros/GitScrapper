/*
 * Copyright (c) 2022. URKEI.
 * All rights reserved.
 *
 * The copyright to the computer program(s) herein is the property of URKEI.
 * The program(s) may be used and/or copied only with the written permission
 * of the owner or in accordance with the terms and conditions stipulated in
 * the contract under which the program(s) have been supplied.
 */

package com.urkei.gitscrapper.client;

import com.urkei.gitscrapper.dto.git.Repo;
import com.urkei.gitscrapper.dto.git.Branch;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

/**
 * REST API client towards github API
 *
 * @see https://develper.github.com/v3
 */
@Component
public class GithubClient {

    /** URI for user repos. */
    public static final String USER_REPOS = "/users/{user}/repos";

    /** URL of github v3 API. */
    public static final String GITHUB_URI = "https://api.github.com";

    /** Github authorization token - don't worry it will expire */
    @Value("${github.token}")
    private String TOKEN;

    /** Clinent instance. */
    private final WebClient client;

    // Spring Boot auto-configures a `WebClient.Builder` instance with nice defaults and customizations.
    // We can use it to create a dedicated `WebClient` for our component.
    public GithubClient(WebClient.Builder builder) {
        this.client = builder.baseUrl(GITHUB_URI).build();
    }

    /**
     * Get user repos.
     *
     * @param user github user login.
     * @return List of Repo objects
     */
    public Flux<Repo> getUserRepos(String user) {

        Flux<Repo> response = this.client.get()
                .uri(USER_REPOS, user)
                .headers(h -> h.setBearerAuth(TOKEN))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Repo.class);

        return response;
    }

    /**
     * Get user branches, based on his branches URI.
     *
     * @param branchesUrl provided as part of geting user repos
     * @return return all branches with latest sha
     */
    public Flux<Branch> getBranches(String branchesUrl) {

        branchesUrl = branchesUrl.replace("{/branch}", "");
        Flux<Branch> response = this.client.get()
                .uri(branchesUrl, "")
                .headers(h -> h.setBearerAuth(TOKEN))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Branch.class);

        return response;
    }

}
