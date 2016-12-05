package io.pivotal.boot.samples.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.stereotype.Component;

/**
 * @author Vinicius Carvalho
 */
@Component
public class YahooHealthIndicator implements HealthIndicator {

	private QuotesService service;



	@Autowired
	public YahooHealthIndicator(QuotesService service) {
		this.service = service;
	}

	@Override
	public Health health() {
		return Health.up().withDetail("responseTime",service.averageResponseTime()).build();

	}
}
