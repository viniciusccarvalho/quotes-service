package io.pivotal.boot.samples.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import io.pivotal.boot.samples.domain.Quote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author Vinicius Carvalho
 */
@RestController
public class QuotesService {

	private RestTemplate client;

	private CounterService counterService;
	private GaugeService gaugeService;
	private AtomicLong counter = new AtomicLong(0);
	private AtomicLong cumulativeTime = new AtomicLong(0);

	@Value("${quotes.fetch.size}")
	Integer fetchSize;

	@Value("${yahoo.endpoint}")
	private String yahooEndpoint;

	@Autowired
	public QuotesService(RestTemplate client, CounterService counterService, GaugeService gaugeService) {
		this.client = client;
		this.counterService = counterService;
		this.gaugeService = gaugeService;
	}

	@RequestMapping(value = "/quotes/{symbol}")
	public Quote fetch(@PathVariable("symbol") String symbol){
		Long start = System.currentTimeMillis();
		ResponseEntity<String> response = client.getForEntity(yahooEndpoint,String.class, symbol);
		Long end = System.currentTimeMillis();
		cumulativeTime.addAndGet(end-start);
		counter.incrementAndGet();
		counterService.increment("yahoo.quotes");
		Quote quote = new Quote(response.getBody());
		gaugeService.submit("yahoo.response",cumulativeTime.get()/counter.get());
		return quote;
	}

	@RequestMapping(value = "/quotes/config")
	public String config(){
		return String.valueOf(fetchSize);
	}


	public Long averageResponseTime(){
		if(counter.get() == 0)
			return 0L;
		return (Long)cumulativeTime.get()/counter.get();
	}

}
