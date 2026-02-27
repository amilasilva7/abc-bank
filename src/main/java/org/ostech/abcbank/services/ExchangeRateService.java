package org.ostech.abcbank.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ExchangeRateService {

    private static final Logger log = LoggerFactory.getLogger(ExchangeRateService.class);

    private final RestTemplate restTemplate;

    @Value("${external.exchange-rate.url}")
    private String exchangeRateUrl;

    public ExchangeRateService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> getExchangeRates(String baseCurrency) {
        String url = exchangeRateUrl + "/" + baseCurrency.toUpperCase();
        log.info("Fetching exchange rates from: {}", url);
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null) {
                throw new IllegalStateException("Empty response from exchange rate API");
            }
            return response;
        } catch (RestClientException ex) {
            log.error("Failed to fetch exchange rates: {}", ex.getMessage());
            throw new IllegalStateException("Unable to fetch exchange rates: " + ex.getMessage());
        }
    }
}

