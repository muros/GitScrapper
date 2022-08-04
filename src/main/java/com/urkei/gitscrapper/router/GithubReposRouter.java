/*
 * Copyright (c) 2022. URKEI.
 * All rights reserved.
 *
 * The copyright to the computer program(s) herein is the property of URKEI.
 * The program(s) may be used and/or copied only with the written permission
 * of the owner or in accordance with the terms and conditions stipulated in
 * the contract under which the program(s) have been supplied.
 */

package com.urkei.gitscrapper.router;

import com.urkei.gitscrapper.dto.tui.NewRepo;
import com.urkei.gitscrapper.dto.tui.ServiceErrorMessage;
import com.urkei.gitscrapper.handler.GithubReposHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

/**
 * Router class for routing provided REST API.
 *
 * It includes Swagger annotations.
 */
@Configuration(proxyBeanMethods = false)
public class GithubReposRouter {

    @Bean
    @RouterOperations(
            {
                    @RouterOperation(
                            path = "/repos/{username}",
                            produces = {
                                    MediaType.APPLICATION_JSON_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = GithubReposHandler.class,
                            beanMethod = "getReposWithBranches",
                            operation = @Operation(
                                    operationId = "getReposWithBranches",
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "Successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = NewRepo.class
                                                    ))
                                            ),
                                            @ApiResponse(
                                                    responseCode = "404",
                                                    description = "Not found",
                                                    content = @Content(schema = @Schema(
                                                            implementation = ServiceErrorMessage.class
                                                    ))
                                            ),
                                            @ApiResponse(
                                                    responseCode = "406",
                                                    description = "Not acceptable",
                                                    content = @Content(schema = @Schema(
                                                            implementation = ServiceErrorMessage.class
                                                    ))
                                            )
                                    },
                                    parameters = {
                                           @Parameter(in = ParameterIn.PATH, name = "username")
                                    }
                            )
                    )
            }
    )
    public RouterFunction<ServerResponse> route(GithubReposHandler githubReposHandler) {

        return (RouterFunction<ServerResponse>) RouterFunctions
                .route(GET("/repos/{username}").and(accept(MediaType.APPLICATION_JSON)), githubReposHandler::getReposWithBranches);
    }

}
