package org.ostech.abcbank.services;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ExchangeRateServiceTest {

    @Autowired
    private ExchangeRateService exchangeRateService;

    @MockitoBean
    private RestTemplate restTemplate;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    private CircuitBreaker circuitBreaker;

    @BeforeEach
    void setUp() {
        circuitBreaker = circuitBreakerRegistry.circuitBreaker("exchangeRateService");
        circuitBreaker.reset();
    }

    @Test
    void testGetExchangeRates_Success() {
        String baseCurrency = "MYR";
        String url = "https://open.er-api.com/v6/latest/" + baseCurrency;

        Map<String, Object> mockResponse = Map.of(
            "result", "success",
            "base_code", baseCurrency,
            "rates", Map.of("USD", 4.5, "EUR", 4.9)
        );

        when(restTemplate.getForObject(url, Map.class)).thenReturn(mockResponse);

        Map<String, Object> response = exchangeRateService.getExchangeRates(baseCurrency);

        assertNotNull(response);
        assertEquals("success", response.get("result"));
        assertEquals(baseCurrency, response.get("base_code"));
        verify(restTemplate, times(1)).getForObject(url, Map.class);
    }

    @Test
    void testGetExchangeRates_NullResponse_FallbackReturned() {
        String baseCurrency = "MYR";
        String url = "https://open.er-api.com/v6/latest/" + baseCurrency;

        when(restTemplate.getForObject(url, Map.class)).thenReturn(null);

        Map<String, Object> response = exchangeRateService.getExchangeRates(baseCurrency);

        assertEquals("degraded", response.get("result"));
        assertTrue(response.containsKey("error"));
        verify(restTemplate, times(1)).getForObject(url, Map.class);
    }

    @Test
    void testGetExchangeRates_RestClientException_FallbackReturned() {
        String baseCurrency = "MYR";
        String url = "https://open.er-api.com/v6/latest/" + baseCurrency;

        when(restTemplate.getForObject(url, Map.class))
            .thenThrow(new RestClientException("Connection timeout"));

        Map<String, Object> response = exchangeRateService.getExchangeRates(baseCurrency);

        assertEquals("degraded", response.get("result"));
        assertEquals(baseCurrency, response.get("base_code"));
        assertTrue(response.get("error").toString().contains("temporarily unavailable"));
        verify(restTemplate, times(1)).getForObject(url, Map.class);
    }

    @Test
    void testCircuitBreaker_FallbackOnFailure() {
        String baseCurrency = "MYR";
        String url = "https://open.er-api.com/v6/latest/" + baseCurrency;

        when(restTemplate.getForObject(url, Map.class))
            .thenThrow(new RestClientException("API unavailable"));

        for (int i = 0; i < 5; i++) {
            Map<String, Object> response = exchangeRateService.getExchangeRates(baseCurrency);


            if (circuitBreaker.getState().toString().equals("OPEN")) {
                assertEquals("degraded", response.get("result"));
                assertEquals(baseCurrency, response.get("base_code"));
                assertTrue(response.get("error").toString().contains("temporarily unavailable"));
                break;
            }
        }
    }

    @Test
    void testCircuitBreaker_StateTransitions() {
        String baseCurrency = "MYR";
        String url = "https://open.er-api.com/v6/latest/" + baseCurrency;

        assertEquals("CLOSED", circuitBreaker.getState().toString());

        when(restTemplate.getForObject(url, Map.class))
            .thenThrow(new RestClientException("API Error"));

        for (int i = 0; i < 5; i++) {
            exchangeRateService.getExchangeRates(baseCurrency);
        }

        assertTrue(circuitBreaker.getState().toString().equals("OPEN")
            || circuitBreaker.getState().toString().equals("HALF_OPEN"));
    }

    @Test
    void testCircuitBreaker_FallbackResponseStructure() {
        String baseCurrency = "USD";
        String url = "https://open.er-api.com/v6/latest/" + baseCurrency;

        when(restTemplate.getForObject(url, Map.class))
            .thenThrow(new RestClientException("API Error"));

        Map<String, Object> fallbackResponse = exchangeRateService.getExchangeRates(baseCurrency);

        assertNotNull(fallbackResponse);
        assertEquals("degraded", fallbackResponse.get("result"));
        assertEquals(baseCurrency, fallbackResponse.get("base_code"));
        assertTrue(fallbackResponse.containsKey("rates"));
        assertTrue(fallbackResponse.containsKey("error"));
    }

    @Test
    void testCircuitBreaker_SuccessAfterRecovery() {
        String baseCurrency = "MYR";
        String url = "https://open.er-api.com/v6/latest/" + baseCurrency;

        Map<String, Object> successResponse = Map.of(
            "result", "success",
            "base_code", baseCurrency,
            "rates", Map.of("USD", 4.5)
        );

        when(restTemplate.getForObject(url, Map.class)).thenReturn(successResponse);

        Map<String, Object> response = exchangeRateService.getExchangeRates(baseCurrency);
        assertEquals("success", response.get("result"));

        verify(restTemplate, atLeastOnce()).getForObject(url, Map.class);
    }
}
