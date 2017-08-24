package com.bigdata.wechat.service;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bigdata.wechat.util.JSONUtil;

/**
 * 请求Rest相关信息
 * @author xinchun.wang
 *
 */
@Service("restClientService")
@SuppressWarnings("unchecked")
public class RESTClientService<T> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RESTClientService.class);
	
	private static JSONUtil jsonUtil = JSONUtil.getInstance();

	protected String excuteGet(String url, boolean useURI) throws Exception {
		CloseableHttpClient httpClient = getHttpClient();
		HttpUriRequest request = getGetRequest(url, useURI);
		
		HttpResponse httpResponse = httpClient.execute(request);

		String response = parseResponse(url, httpResponse);
		return response;
	}
	
	protected String excutePost(String url, List<NameValuePair> nvps) throws Exception {
		HttpClient httpClient = getHttpClient();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		HttpResponse httpResponse = httpClient.execute(httpPost);
		String response = parseResponse(url, httpResponse);
		return response;
	}

	private String parseResponse(String url, HttpResponse httpResponse)
			throws Exception, IOException {
		int status = httpResponse.getStatusLine().getStatusCode();
		if(status != 200){
			String errorMsg = "Error occurs in calling service: " + url + ", with status:" + status;
			throw new Exception(errorMsg);
		}
		HttpEntity entry = httpResponse.getEntity();
		String response = EntityUtils.toString(entry, "UTF-8");
		return response;
	}

	private CloseableHttpClient getHttpClient() {
		HttpClientBuilder builder = HttpClientBuilder.create();
		RequestConfig.Builder requestBuilder = RequestConfig.custom()
				.setConnectTimeout(3000)
				.setConnectionRequestTimeout(3000)
				.setSocketTimeout(3000);
		builder.setDefaultRequestConfig(requestBuilder.build());
		CloseableHttpClient closeableHttpClient = builder.build();
		return closeableHttpClient;
	}

	private HttpUriRequest getGetRequest(String url, boolean useURI) throws Exception {
		HttpUriRequest request;
		if(useURI){
			URL requestURL = new URL(url);
			URI uri = new URI(
				requestURL.getProtocol(),
				null,
				requestURL.getHost(), 
				requestURL.getPort(),
				requestURL.getPath(), 
				requestURL.getQuery(), 
				null);
			request = new HttpGet(uri);
		}
		else{
			request = new HttpGet(url);
		}
		return request;
	}
	
	protected T parseResultMap(String response, String url) throws Exception{
		Map<?, ?> result = jsonUtil.formatJSON2Map(response);
		if(result == null){
			return null;
		}
		Object code = null;
		if(result.containsKey("code"))
			code = result.get("code");
		
		//不含code的消息
		if(code == null){
			return (T)result;
		}
		if(code.equals(200)){
			T msg;
			if(result.containsKey("msg"))
				msg = (T)result.get("msg");
			else
				msg = (T)result.get("Msg");	
			return msg;
		}
		else{
			String errorMsg = "code is not 200, response code is = " + code + ", response = " + response + ", url =" + url;
			throw new Exception(errorMsg);
		}
	}

	protected List<T> parseResultList(String response){
		List<T> result = (List<T>)jsonUtil.formatJSON2List(response);
		if(result == null){
			return null;
		}
		return result;
	}
	
	/**
	 * 根据URL得到返回值
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public T get(String url) throws Exception{
		String response = excuteGet(url, false);
		if(response == null){
			LOGGER.error("call uri error, response is null, uri = " + url);
			return null;
		}
		T result = parseResultMap(response, url);
		return result;
	}
	
	public T getByURI(String url) throws Exception{
		String response = excuteGet(url, true);
		if(response == null){
			LOGGER.error("call uri error, response is null, uri = " + url);
			return null;
		}
		T result = parseResultMap(response, url);
		return result;
	}
	
	public List<T> getByURI4List(String url) throws Exception{
		String response = excuteGet(url, true);
		if(response == null){
			LOGGER.error("call uri error, response is null, uri = " + url);
			return null;
		}
		List<T> result = parseResultList(response);
		return result;
	}
	
	
	/**
	 * 发起Post请求
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public T post(String url, List<NameValuePair> nvps) throws Exception{
		String response = excutePost(url, nvps);
		if(response == null){
			LOGGER.error("post uri error, response is null, uri = " + url);
			return null;
		}
		T result = parseResultMap(response, url);
		return result;
	}

	public static void main(String[] args) throws Exception{
		RESTClientService<Map<String, ?>> rest = new RESTClientService<Map<String, ?>>();

		Map<String, ?> result = rest.get("http://fanyi.youdao.com/openapi.do?"
				+ "keyfrom=MedicalEnglishHere&key=226698417&type=data&doctype=json&version=1.1&q=Analgesia");
		List<String> translation = (List<String>)result.get("translation");
		result.size();
		
	}
	
}
