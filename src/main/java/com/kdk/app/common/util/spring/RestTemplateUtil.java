package com.kdk.app.common.util.spring;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdk.app.common.ExceptionMessage;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2024. 10. 22. 김대광	최초작성
 * 2025. 05. 22. 김대광   개선
 * </pre>
 *
 * <pre>
 * Spring 전용 Http Client
 *  - Dependency
 *    > Apache HttpClient5
 *    > Jackson
 * </pre>
 *
 * isSSL은 false로 해서 오류 나는 겨우에만 true로 사용
 *
 * @author 김대광
 */
public class RestTemplateUtil {

	private static final Logger logger = LoggerFactory.getLogger(RestTemplateUtil.class);

	private RestTemplateUtil() {
		super();
	}

	private static class RestTemplateProvider {
		private static RestTemplate secureRestTemplate;
		private static RestTemplate insecureRestTemplate;

		public static synchronized RestTemplate getRestTemplate(boolean isSsl) {
			if (isSsl) {
				if (secureRestTemplate == null) {
					HttpComponentsClientHttpRequestFactory factory = HttpRequestFactory.getRequestFactory(true);
					if (factory != null) {
						secureRestTemplate = new RestTemplate(factory);
					} else {
						secureRestTemplate = null;
					}
				}
				return secureRestTemplate;
			} else {
				if (insecureRestTemplate == null) {
					HttpComponentsClientHttpRequestFactory factory = HttpRequestFactory.getRequestFactory(false);
					if (factory != null) {
						insecureRestTemplate = new RestTemplate(factory);
					} else {
						insecureRestTemplate = null;
					}
				}
				return insecureRestTemplate;
			}
		}
	}

	private static class Config {
		private static final int TIMEOUT = 5000;

		private static final ConnectionConfig CONNECTION_CONFIG =
				ConnectionConfig.custom()
				.setConnectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
				.setSocketTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
				.build();
	}

	private static class HttpClientProvider {
		private static CloseableHttpClient httpClient;

        public static synchronized CloseableHttpClient getHttpClient(boolean isSsl) {
        	if (httpClient == null) {
        		try {
					httpClient = HttpClients.custom()
							.setConnectionManager(HttpClientConnectionManagerProvider.createHttpClientConnectionManager(isSsl))
							.setRetryStrategy(new DefaultHttpRequestRetryStrategy(1, TimeValue.ofSeconds(3)))
							.build();
				} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
					logger.error("", e);
				}
        	}
        	return httpClient;
        }
	}

	private static class HttpClientConnectionManagerProvider {
		private static HttpClientConnectionManager secureHttpClientConnectionManager;
        private static HttpClientConnectionManager insecureHttpClientConnectionManager;

        public static synchronized HttpClientConnectionManager createHttpClientConnectionManager(boolean isSsl) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        	if (isSsl) {
        		if (secureHttpClientConnectionManager == null) {
    				SSLContext sslContext = SSLContextBuilder.create()
    						.loadTrustMaterial(TrustAllStrategy.INSTANCE)
    						.build();

    				DefaultClientTlsStrategy tlsStrategy = new DefaultClientTlsStrategy(
    						sslContext, NoopHostnameVerifier.INSTANCE);

    				secureHttpClientConnectionManager = PoolingHttpClientConnectionManagerBuilder.create()
    						.setDefaultConnectionConfig(Config.CONNECTION_CONFIG)
    						.setTlsSocketStrategy(tlsStrategy)
    						.setMaxConnTotal(100)
    						.setMaxConnPerRoute(5)
    						.build();
        		}
        		return secureHttpClientConnectionManager;
        	} else {
        		if (insecureHttpClientConnectionManager == null) {
        			insecureHttpClientConnectionManager = PoolingHttpClientConnectionManagerBuilder.create()
        					.setDefaultConnectionConfig(Config.CONNECTION_CONFIG)
        					.setMaxConnTotal(100)
        					.setMaxConnPerRoute(5)
        					.build();
        		}
        		return insecureHttpClientConnectionManager;
        	}
        }
	}

	private static class HttpRequestFactory {
		private static HttpComponentsClientHttpRequestFactory getRequestFactory(boolean isSSL) {
			CloseableHttpClient httpClient = HttpClientProvider.getHttpClient(isSSL);

			if (httpClient != null) {
				return new HttpComponentsClientHttpRequestFactory(httpClient);
			} else {
				return null;
			}
		}
	}

	private static RestTemplate getRestTemplate(boolean isSsl) {
		return RestTemplateProvider.getRestTemplate(isSsl);
	}

	private static class Convert {
		@SuppressWarnings("unchecked")
		private static Map<String, Object> objectToMap(Object obj) {
			Map<String, Object> map = null;

			ObjectMapper oMapper = new ObjectMapper();
			map = oMapper.convertValue(obj, Map.class);

			return map;
		}

		private static MultiValueMap<String, String> mapToHttpHeaders(Map<String, Object> headerMap, HttpHeaders headers) {
			MultiValueMap<String, String> mMap = new LinkedMultiValueMap<>();

			MediaType mediaType = headers.getContentType();
			if ( mediaType != null ) {
				mMap.add(HttpHeaders.CONTENT_TYPE, mediaType.toString());
			}

			if ( headerMap != null ) {
				Iterator<String> it = headerMap.keySet().iterator();

				while ( it.hasNext() ) {
					String sKey = it.next();
					Object value = headerMap.get(sKey);

					mMap.add(sKey, String.valueOf(value));
				}
			}

			return mMap;
		}

		private static MultiValueMap<String, Object> hashMapToMultiValueMap(Map<String, Object> map) throws IOException {
			MultiValueMap<String, Object> mMap = new LinkedMultiValueMap<>();

			Iterator<String> it = map.keySet().iterator();
			while ( it.hasNext() ) {
				String sKey = it.next();
				Object value = map.get(sKey);

				if ( value instanceof List<?> ) {
					@SuppressWarnings("unchecked")
					List<Object> list = (List<Object>) value;
					mMap.put(sKey, list);

				} else if ( value instanceof File file ) {
					mMap.add(sKey, new FileSystemResource(file));

				} else if ( value instanceof MultipartFile mFile ) {
					mMap.add(sKey, new ByteArrayResource(mFile.getBytes()) {

						@Override
						public String getFilename() {
							return mFile.getOriginalFilename();
						}
					});

				} else {
					mMap.add(sKey, String.valueOf(value));
				}
			}

			return mMap;
		}
	}

	@SuppressWarnings("unchecked")
	public static ResponseEntity<Object> get(boolean isSSL, String url, MediaType mediaType
			, Map<String, Object> headerMap, Class<?> responseType, Object... uriVariables) {

		if ( ObjectUtils.isEmpty(url.trim()) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNull("url"));
		}

		Objects.requireNonNull(responseType, ExceptionMessage.isNull("responseType"));

		RestTemplate restTemplate = RestTemplateUtil.getRestTemplate(isSSL);
		if (restTemplate == null) {
			return null;
		}

		HttpHeaders httpHeaders = new HttpHeaders();
		if (mediaType != null) {
			httpHeaders.setContentType(mediaType);
		}

		MultiValueMap<String, String> headers = Convert.mapToHttpHeaders(headerMap, httpHeaders);

		HttpEntity<Object> request = new HttpEntity<>(headers);

		if ( uriVariables != null ) {
			return (ResponseEntity<Object>) restTemplate.exchange(url, HttpMethod.GET, request, responseType, uriVariables);
		} else {
			return (ResponseEntity<Object>) restTemplate.exchange(url, HttpMethod.GET, request, responseType);
		}
	}

	public static ResponseEntity<Object> post(boolean isSSL, String url, MediaType mediaType
			, Map<String, Object> headerMap, Object body, Class<?> responseType, Object... uriVariables) throws IOException {

		Map<String, Object> bodyMap = Convert.objectToMap(body);
		return post(isSSL, url, mediaType, headerMap, bodyMap, responseType, uriVariables);
	}

	@SuppressWarnings("unchecked")
	public static ResponseEntity<Object> post(boolean isSSL, String url, MediaType mediaType
			, Map<String, Object> headerMap, Map<String, Object> bodyMap, Class<?> responseType, Object... uriVariables) throws IOException {

		if ( ObjectUtils.isEmpty(url.trim()) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNull("url"));
		}

		Objects.requireNonNull(responseType, ExceptionMessage.isNull("responseType"));

		RestTemplate restTemplate = RestTemplateUtil.getRestTemplate(isSSL);
		if (restTemplate == null) {
			return null;
		}

		HttpHeaders httpHeaders = new HttpHeaders();
		if (mediaType != null) {
			httpHeaders.setContentType(mediaType);
		}

		MultiValueMap<String, String> headers = Convert.mapToHttpHeaders(headerMap, httpHeaders);

		HttpEntity<Object> request = null;
		MultiValueMap<String, Object> mMap = null;

		if ( bodyMap != null ) {
			if ( mediaType == null || MediaType.APPLICATION_FORM_URLENCODED.equals(mediaType) || MediaType.MULTIPART_FORM_DATA.equals(mediaType) ) {
				mMap = Convert.hashMapToMultiValueMap(bodyMap);
				request = new HttpEntity<>(mMap, headers);

			} else {
				request = new HttpEntity<>(bodyMap, headers);
			}
		} else {
			request = new HttpEntity<>(headers);
		}

		if ( uriVariables != null ) {
			return (ResponseEntity<Object>) restTemplate.postForEntity(url, request, responseType, uriVariables);
		} else {
			return (ResponseEntity<Object>) restTemplate.postForEntity(url, request, responseType);
		}
	}

}
