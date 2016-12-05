package io.pivotal.boot.samples.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

import io.pivotal.boot.samples.domain.Quote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	private final String SP100 = "AAPL,ABBV,ABT,ACN,AGN,AIG,ALL,AMGN,AMZN,AXP,BA,BAC,BIIB,BK,BLK,BMY,BRK.B,C,CAT,CELG,CL,CMCSA,COF,COP,COST,CSCO,CVS,CVX,DD,DHR,DIS,DOW,DUK,EMR,EXC,F,FB,FDX,FOX,FOXA,GD,GE,GILD,GM,GOOG,GOOGL,GS,HAL,HD,HON,IBM,INTC,JNJ,JPM,KHC,KMI,KO,LLY,LMT,LOW,MA,MCD,MDLZ,MDT,MET,MMM,MO,MON,MRK,MS,MSFT,NEE,NKE,ORCL,OXY,PCLN,PEP,PFE,PG,PM,PYPL,QCOM,RTN,SBUX,SLB,SO,SPG,T,TGT,TWX,TXN,UNH,UNP,UPS,USB,UTX,V,VZ,WBA,WFC,WMT,XOM";

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

//	@RequestMapping(value="/top")
//	public List<Quote> topQuotes(@RequestParam(name = "size", defaultValue = "10") int size){
//		ResponseEntity<String> response = client.getForEntity(yahooEndpoint,String.class, SP100);
//		Scanner scanner = new Scanner(response.getBody());
//		List<Quote> quotes = new ArrayList<>();
//		while(scanner.hasNextLine()){
//			Quote quote = new Quote(scanner.nextLine());
//			quotes.add(quote);
//		}
//		quotes.sort((q1,q2) -> q2.getDifference().compareTo(q1.getDifference()));
//		return quotes.subList(0,size);
//	}

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
