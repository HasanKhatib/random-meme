package com.hasankhatib.memegenerator.api;

import com.hasankhatib.memegenerator.dto.DadJokeResponseDTO;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class RandomMemeHandler {
  final HttpHeaders headers = new HttpHeaders();

  List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
  //Add the Jackson Message converter
  MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
  StringHttpMessageConverter m = new StringHttpMessageConverter(Charset.forName("UTF-8"));
  final RestTemplate restTemplate = new RestTemplateBuilder().additionalMessageConverters(m).additionalMessageConverters(new MappingJackson2HttpMessageConverter()).build();
  @Value("${app.dad-jokes.headers.api-key}")
  String rapidApiKey;
  @Value("${app.dad-jokes.url}")
  String dadJokeURL;
  @Value("${app.dad-jokes.headers.api-host}")
  String dadJokeAPIHost;
  @Value("${app.meme-generator.url}")
  String memeGenerateURL;
  @Value("${app.meme-generator.meme-code}")
  String memeCode;
  @Value("${app.meme-generator.font-size}")
  String fontSize;
  @Value("${app.meme-generator.headers.api-host}")
  String memeApiHost;
  Tracer tracer;

  public RandomMemeHandler(Tracer tracer) {
    this.tracer = tracer;
  }

  public Mono<ServerResponse> get(ServerRequest serverRequest) {
    var span = GlobalTracer.get().activeSpan();
    //get a random dad joke
    var joke = getJoke().doFirst(() -> {
      span.log("Get a random joke!");
    });
    //generate a meme image
    return joke
      .doOnNext(dto -> {
        span.log("generating meme!");
      })
      .flatMap(this::generateMeme)
      .flatMap(image -> {
        DataBuffer buffer = new DefaultDataBufferFactory().wrap(image);
        return ServerResponse.ok().contentType(MediaType.IMAGE_JPEG).body(BodyInserters.fromDataBuffers(Flux.just(buffer)));

      })
      .switchIfEmpty(ServerResponse.notFound().build())
      .onErrorResume(e -> {
        log.error(e.getMessage(), e);
        return ServerResponse.badRequest().build();
      });

  }

  private Mono<byte[]> generateMeme(DadJokeResponseDTO dadJokeResponseDTO) {
    var topTxt = dadJokeResponseDTO.getBody().get(0).get("setup");
    var bottomTxt = dadJokeResponseDTO.getBody().get(0).get("punchline");
    var buildURL = String.format(memeGenerateURL + "&top=%s&bottom=%s&meme=%s&font_size=%s", topTxt, bottomTxt, memeCode, fontSize);
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(buildURL);
    URI uri = builder.build(false).toUri();

    headers.clear();
    headers.add("X-RapidAPI-Host", memeApiHost);
    headers.add("X-RapidAPI-Key", rapidApiKey);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
    restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());

    HttpEntity<String> entity = new HttpEntity<>(headers);
    var result = restTemplate.exchange(uri, HttpMethod.GET, entity, byte[].class).getBody();
    return Mono.justOrEmpty(result);
  }

  private Mono<DadJokeResponseDTO> getJoke() {
    headers.clear();
    headers.add("X-RapidAPI-Host", dadJokeAPIHost);
    headers.add("X-RapidAPI-Key", rapidApiKey);

    HttpEntity<String> entity = new HttpEntity<>(headers);

    var result = restTemplate.exchange(dadJokeURL, HttpMethod.GET, entity, DadJokeResponseDTO.class).getBody();
    return Mono.justOrEmpty(result);
  }
}