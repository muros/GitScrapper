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

import com.urkei.gitscrapper.client.GithubClient;
import com.urkei.gitscrapper.dto.git.Branch;
import com.urkei.gitscrapper.dto.git.Owner;
import com.urkei.gitscrapper.dto.git.Repo;
import com.urkei.gitscrapper.publisher.RepoPublisher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.reactivestreams.Subscription;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RepoSubscriberTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @Tag("unit")
    void subscriptionFor100Test() {
        RepoPublisher repoPublisher = Mockito.mock(RepoPublisher.class);
        GithubClient githubClient = Mockito.mock(GithubClient.class);
        RepoSubscriber repoSubscriber = new RepoSubscriber(repoPublisher, githubClient);

        ArgumentCaptor<Long> param = ArgumentCaptor.forClass(Long.class);

        Subscription subscription = Mockito.mock(Subscription.class);
        repoSubscriber.onSubscribe(subscription);

        verify(subscription).request(param.capture());
        assertEquals(100, param.getValue());
    }

    @Test
    @Tag("unit")
    void onNextBranchTest() {
        RepoPublisher repoPublisher = Mockito.mock(RepoPublisher.class);
        GithubClient githubClient = Mockito.mock(GithubClient.class);
        when(githubClient.getBranches(anyString())).thenReturn(
            Flux.fromIterable(
                    Arrays.asList(
                            new Branch()
                    )
            )
        );
        RepoSubscriber repoSubscriber = new RepoSubscriber(repoPublisher, githubClient);

        ArgumentCaptor<String> param = ArgumentCaptor.forClass(String.class);

        Repo repo = new Repo();
        repo.setName("my-repo");
        Owner owner = new Owner();
        owner.setLogin("muros");
        repo.setOwner(owner);
        repo.setFork(false);
        repo.setBranches_url("/users/muros/repos/my-repo{/branch}");
        repoSubscriber.onNext(repo);

        verify(githubClient, times(1)).getBranches(param.capture());
        assertEquals("/users/muros/repos/my-repo{/branch}", param.getValue());
    }

    @Test
    @Tag("unit")
    void onAnyErrorTest() {
        RepoPublisher repoPublisher = Mockito.mock(RepoPublisher.class);
        GithubClient githubClient = Mockito.mock(GithubClient.class);
        RepoSubscriber repoSubscriber = new RepoSubscriber(repoPublisher, githubClient);

        ArgumentCaptor<Throwable> param = ArgumentCaptor.forClass(Throwable.class);

        repoSubscriber.onError(new RuntimeException("AnyException"));

        verify(repoPublisher, times(1)).onOtherError(param.capture());
        assertEquals(RuntimeException.class, param.getValue().getClass());
        assertEquals("AnyException", param.getValue().getMessage());
    }

    @Test
    @Tag("unit")
    void onNotFoundTest() {
        RepoPublisher repoPublisher = Mockito.mock(RepoPublisher.class);
        GithubClient githubClient = Mockito.mock(GithubClient.class);
        RepoSubscriber repoSubscriber = new RepoSubscriber(repoPublisher, githubClient);

        ArgumentCaptor<Throwable> param = ArgumentCaptor.forClass(Throwable.class);

        repoSubscriber.onError(WebClientResponseException.create(HttpStatus.NOT_FOUND.value(), "Not found", null, null, null));

        verify(repoPublisher, times(1)).onOtherError(param.capture());
        assertEquals(RuntimeException.class, param.getValue().getClass());
        assertEquals("AnyException", param.getValue().getMessage());
    }

    @Test
    @Tag("unit")
    void onComplete() {

    }
}