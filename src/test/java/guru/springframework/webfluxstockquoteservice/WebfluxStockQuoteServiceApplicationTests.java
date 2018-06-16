package guru.springframework.webfluxstockquoteservice;

import guru.springframework.webfluxstockquoteservice.model.Quote;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebfluxStockQuoteServiceApplicationTests {

	@Autowired
	private WebTestClient client;

	@Test
	public void contextLoads() {
	}

	@Test public void fetchQuotesTest() throws Exception {
		client.get().uri("/quotes?size=20").accept(APPLICATION_JSON).exchange().expectStatus().isOk().expectHeader()
				.contentType(APPLICATION_JSON).expectBodyList(Quote.class).hasSize(20)
				.consumeWith(listEntityExchangeResult -> {
					assertThat(listEntityExchangeResult.getResponseBody())
							.allSatisfy(quote -> assertThat(quote.getPrice()).isPositive());
					assertThat(listEntityExchangeResult.getResponseBody()).hasSize(20);
				});


	}

	@Test public void streamMonoTest() throws Exception {
		CountDownLatch countDownLatch = new CountDownLatch(1);
		client.get().uri("/quotesStream").accept(APPLICATION_STREAM_JSON).exchange().returnResult(Quote.class)
				.getResponseBody().take(10).subscribe(quote -> {
			assertThat(quote.getPrice()).isPositive();
			countDownLatch.countDown();
		});
		countDownLatch.await();
		System.out.println("Stream test done!");
	}
}
