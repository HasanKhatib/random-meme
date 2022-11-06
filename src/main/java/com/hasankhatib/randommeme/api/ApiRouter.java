package com.hasankhatib.randommeme.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ApiRouter {
  @Bean
  public RouterFunction<ServerResponse> route(
    RandomMemeHandler randomMemeHandler) {
    return RouterFunctions.route(
        RequestPredicates.GET("/").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
        randomMemeHandler::get);
  }
}
