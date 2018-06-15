package guru.springframework.webfluxstockquoteservice.service;

import guru.springframework.webfluxstockquoteservice.model.Quote;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class QuoteGeneratorServiceImplTest {

	QuoteGeneratorService quoteGeneratorService = new QuoteGeneratorServiceImpl();

	@Test public void fetchStream() throws Exception {
		Flux<Quote> quoteFlux = quoteGeneratorService.fetchQuoteStream(Duration.ofMillis(100L));
		quoteFlux.take(10).subscribe();

		Thread.sleep(1000);
	}

	@Test public void fetchStreamCountDown() throws Exception {
		Flux<Quote> quoteFlux = quoteGeneratorService.fetchQuoteStream(Duration.ofMillis(100));

		CountDownLatch countDownLatch = new CountDownLatch(1);
		quoteFlux.take(10).subscribe(System.out::println, throwable -> throwable.printStackTrace(), () -> countDownLatch.countDown());

		countDownLatch.await();
	}
}