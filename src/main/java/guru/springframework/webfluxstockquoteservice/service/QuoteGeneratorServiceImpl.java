package guru.springframework.webfluxstockquoteservice.service;

import guru.springframework.webfluxstockquoteservice.model.Quote;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.BiFunction;

@Slf4j
@Service
public class QuoteGeneratorServiceImpl implements QuoteGeneratorService {

	private final MathContext mathContext = new MathContext(2);

	private final Random random = new Random();

	private final List<Quote> prices = new ArrayList<>();

	public QuoteGeneratorServiceImpl() {

		prices.add(new Quote("AAPL", 160.16));
		prices.add(new Quote("MSFT", 150.16));
		prices.add(new Quote("GOOG", 156.16));
		prices.add(new Quote("IBM", 56.56));
		prices.add(new Quote("RHT", 10.00));
	}

	@Override
	public Flux<Quote> fetchQuoteStream(Duration period) {
		return Flux.generate(() -> 0,
				(BiFunction<Integer, SynchronousSink<Quote>, Integer>) (index, sink) -> {
			Quote updateQuote = updateQuote(prices.get(index));
			sink.next(updateQuote);
			return ++index % prices.size();
		}).zipWith(Flux.interval(period)).map(objects -> objects.getT1())
		.map(o -> {
			o.setInstant(Instant.now());
			return o;
		}).log("guru.springframework.webfluxstockquoteservice.service.QuoteGenerator");
	}

	private Quote updateQuote(Quote quote) {
		BigDecimal newPrice = quote.getPrice().multiply(new BigDecimal(0.05 * random.nextDouble()), mathContext);
		return new Quote(quote.getTicker(), quote.getPrice().add(newPrice));
	}
}
