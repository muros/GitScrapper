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

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class WebConfigTest {

    @Test
    @Tag("unit")
    void exceptionHandlerWebClientResponseExceptionTest() {

        ServerWebExchange exchange = Mockito.mock(ServerWebExchange.class);
        ServerHttpResponse response = Mockito.mock(ServerHttpResponse.class);
        DataBufferFactory factory = Mockito.mock(DataBufferFactory.class);
        DataBuffer dataBuffer = Mockito.mock(DataBuffer.class);
        HttpHeaders headers = Mockito.mock(HttpHeaders.class);

        Mockito.when(response.bufferFactory()).thenReturn(factory);
        Mockito.when(factory.wrap(any(byte[].class))).thenReturn(dataBuffer);
        Mockito.when(exchange.getResponse()).thenReturn(response);
        Mockito.when(response.getHeaders()).thenReturn(headers);

        WebClientResponseException ex = new WebClientResponseException(HttpStatus.NOT_FOUND.value(), "Not Found", null, null, null);

        new WebConfig().exceptionHandler().handle(exchange, ex);

        verify(exchange, times(4)).getResponse();
    }

    @Test
    @Tag("unit")
    void exceptionHandlerResponseStatusExceptionTest() {

        ServerWebExchange exchange = Mockito.mock(ServerWebExchange.class);
        ServerHttpResponse response = Mockito.mock(ServerHttpResponse.class);
        DataBufferFactory factory = Mockito.mock(DataBufferFactory.class);
        DataBuffer dataBuffer = Mockito.mock(DataBuffer.class);
        HttpHeaders headers = Mockito.mock(HttpHeaders.class);

        Mockito.when(response.bufferFactory()).thenReturn(factory);
        Mockito.when(factory.wrap(any(byte[].class))).thenReturn(dataBuffer);
        Mockito.when(exchange.getResponse()).thenReturn(response);
        Mockito.when(response.getHeaders()).thenReturn(headers);

        ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "No matching handler");

        new WebConfig().exceptionHandler().handle(exchange, ex);

        verify(exchange, times(4)).getResponse();
    }

    @Test
    @Tag("unit")
    void exceptionHandlerUnknownExceptionTest() {

        ServerWebExchange exchange = Mockito.mock(ServerWebExchange.class);
        ServerHttpResponse response = Mockito.mock(ServerHttpResponse.class);
        DataBufferFactory factory = Mockito.mock(DataBufferFactory.class);
        DataBuffer dataBuffer = Mockito.mock(DataBuffer.class);
        HttpHeaders headers = Mockito.mock(HttpHeaders.class);

        Mockito.when(response.bufferFactory()).thenReturn(factory);
        Mockito.when(factory.wrap(any(byte[].class))).thenReturn(dataBuffer);
        Mockito.when(exchange.getResponse()).thenReturn(response);
        Mockito.when(response.getHeaders()).thenReturn(headers);

        RuntimeException ex = new RuntimeException("Server problem");

        new WebConfig().exceptionHandler().handle(exchange, ex);

        verify(exchange, times(4)).getResponse();
    }
}