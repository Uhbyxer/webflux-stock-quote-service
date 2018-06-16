package guru.springframework.webfluxstockquoteservice.web;

import guru.springframework.webfluxstockquoteservice.model.Quote;
import guru.springframework.webfluxstockquoteservice.service.QuoteGeneratorService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.time.Duration.ofMillis;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component public class QuoteHandler {
	private final QuoteGeneratorService quoteGeneratorService;

	public QuoteHandler(QuoteGeneratorService quoteGeneratorService) {
		this.quoteGeneratorService = quoteGeneratorService;
	}

	public Mono<ServerResponse> fetchQuotes(ServerRequest request) {
		int size = Integer.parseInt(request.queryParam("size").orElse("10"));

		return ok().contentType(APPLICATION_JSON)
				.body(quoteGeneratorService.fetchQuoteStream(ofMillis(100)).take(size), Quote.class);
	}

	public Mono<ServerResponse> streamQuotes(ServerRequest request) {
		return ok().contentType(APPLICATION_STREAM_JSON)
				.body(quoteGeneratorService.fetchQuoteStream(ofMillis(200)), Quote.class);
	}
}
