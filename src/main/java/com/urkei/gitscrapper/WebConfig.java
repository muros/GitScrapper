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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.urkei.gitscrapper.dto.tui.ServiceErrorMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Configuration of custom error handling.
 *
 * Error messages are mapped to custom error code and message responses.
 *
 * Todo: Requires urgent refactoring. This is spaghetti code, just as PoC. Refactor, but first prepare unit tests.
 *
 * @see ServiceErrorMessage
 */
@Configuration
@EnableWebFlux
public class WebConfig {
    @Bean
    @Order(-2)
    public WebExceptionHandler exceptionHandler() {
        Mono<Void> monoResponse = null;

        return (ServerWebExchange exchange, Throwable ex) -> {

            HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            String message = "Unknown server error.";
            String reason = "NA";
            // Error handling by requirements.
            //
            // Status code - Status text
            // 404         - Not Found
            // 404         - No matching handler
            if (ex instanceof WebClientResponseException) {
                httpStatus = ((WebClientResponseException) ex).getStatusCode();
                message = ((WebClientResponseException) ex).getStatusText();
                reason = message;
            } else if (ex instanceof ResponseStatusException) {
                httpStatus = ((ResponseStatusException) ex).getStatus();
                message = ((ResponseStatusException) ex).getMessage();
                reason = ((ResponseStatusException) ex).getReason();
            } else {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                message = ex.getMessage();
                reason = message;
            }

            ObjectMapper mapper = new ObjectMapper();
            String jsonString = "{}";
            try {
                ServiceErrorMessage serviceErrorMessage = new ServiceErrorMessage(message, httpStatus.value());
                jsonString = mapper.writeValueAsString(serviceErrorMessage);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            byte[] bytes = jsonString.getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);

            String statusText = httpStatus.getReasonPhrase();
            switch (reason) {
                case "Not Found":
                    exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
                    break;
                case "No matching handler":
                    exchange.getResponse().setStatusCode(HttpStatus.NOT_ACCEPTABLE);
                    break;
                default:
                    exchange.getResponse().setStatusCode(httpStatus);
                    break;
            }
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

            return exchange.getResponse().writeWith(Flux.just(buffer));
        };
    }

}
