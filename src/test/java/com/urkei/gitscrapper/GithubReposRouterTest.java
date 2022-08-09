/*
 * Copyright (c) 2022. URKEI.
 * All rights reserved.
 *
 * The copyright to the computer program(s) herein is the property of URKEI.
 * The program(s) may be used and/or copied only with the written permission
 * of the owner or in accordance with the terms and conditions stipulated in
 * the contract under which the program(s) have been supplied.
 */

package com.urkei.gitscrapper;

import com.urkei.gitscrapper.dto.tui.NewRepo;
import com.urkei.gitscrapper.dto.tui.ServiceErrorMessage;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
//  We create a `@SpringBootTest`, starting an actual server on a `RANDOM_PORT`
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GithubReposRouterTest {

    // Spring Boot will create a `WebTestClient` for you,
    // already configure and ready to issue requests against "localhost:RANDOM_PORT"
    @Autowired
    private WebTestClient webTestClient;

    @Test
    @Tag("integration")
    public void testUserAndReposExist() {
        webTestClient
                .get().uri("/repos/muros")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(NewRepo.class).value(repos -> {
                    assertThat(repos.size()).isGreaterThan(0);
                });
    }

    @Test
    @Tag("integration")
    public void testNonexistentUser() {
        webTestClient
                .get().uri("/repos/muros-cttt")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody(ServiceErrorMessage.class).value(msg -> {
                    assertThat(msg.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
                });
    }

    @Test
    @Tag("integration")
    public void testXmlMediaTypeNotSupported() {
        webTestClient
                .get().uri("/repos/muros")
                .accept(MediaType.APPLICATION_XML)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_ACCEPTABLE)
                .expectBody(ServiceErrorMessage.class).value(msg -> {
                    assertThat(msg.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
                });
    }

}
