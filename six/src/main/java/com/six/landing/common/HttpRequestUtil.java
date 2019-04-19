package com.six.landing.common;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Project: ly-fortune
 * <p>
 * Date: 2019-03-03
 * <p>
 * TIME: 10:20
 * <p>
 * Description:
 * <p>
 * Version:1.0
 */

public class HttpRequestUtil {


/**
	 * POST 请求
	 */

	public static String doPost(String url, Map<String, String> params, int readTimeoutMs, int connectTimeoutMs)
			throws IOException {
		HttpClient httpClient = getHttpClient();
		HttpPost httpPost = postForm(url, params);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(readTimeoutMs)
				.setConnectTimeout(connectTimeoutMs).build();
		httpPost.setConfig(requestConfig);
		httpPost.addHeader("User-Agent", "  ");
		HttpResponse httpResponse = httpClient.execute(httpPost);
		HttpEntity httpEntity = httpResponse.getEntity();
		return EntityUtils.toString(httpEntity, "UTF-8");
	}


/**
	 * POST请求
	 * 
	 * @param url
	 *            URL
	 * @param parameterMap
	 *            请求参数
	 * @return 返回结果
	 */

	public static String post(String url, Map<String, Object> parameterMap) {
		String result = null;
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<>();
			if (parameterMap != null) {
				for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
					String name = entry.getKey();
					String value = ConvertUtils.convert(entry.getValue());
					if (StringUtils.isNotEmpty(name)) {
						nameValuePairs.add(new BasicNameValuePair(name, value));
					}
				}
			}
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpClient httpClient = getHttpClient();
			HttpResponse response = httpClient.execute(httpPost);
			try {
				HttpEntity httpEntity = response.getEntity();
				if (httpEntity != null) {
					result = EntityUtils.toString(httpEntity);
					EntityUtils.consume(httpEntity);
				}
			} finally {

			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (ClientProtocolException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return result;
	}




	@SuppressWarnings("unchecked")
	private static Map<String, Object> setBackData(String content) {
		if(!StringUtil.isEmpty(content)) {
			try {
				return (Map<String, Object>) JSONObject.parse(content);
			}catch (Exception e){
				return new HashMap<>();
			}
		}
		return new HashMap<>();
	}

	public static String doPostWithJson(String jsonObj, String url) {
		HttpPost post = null;
		try {
			post = new HttpPost(url);
			// 构造消息头
			post.setHeader("Content-type", "application/json; charset=utf-8");
			post.setHeader("Connection", "Close");

			// 构建消息实体
			StringEntity entity = new StringEntity(jsonObj, Charset.forName("UTF-8"));
			entity.setContentEncoding("UTF-8");
			// 发送Json格式的数据请求
			entity.setContentType("application/json");
			post.setEntity(entity);

			HttpClient httpClient = getHttpClient();
			HttpResponse response = httpClient.execute(post);

			// 检验返回码
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				return "";
			}

			HttpEntity httpEntity = response.getEntity();
			String result = null;
			if (httpEntity != null) {
				result = EntityUtils.toString(httpEntity, "utf-8");
			}
			EntityUtils.consume(httpEntity);

			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (post != null) {
				try {
					post.releaseConnection();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return "";
	}


/**
	 *
	 * @param url
	 * @param params
	 * @param readTimeoutMs
	 * @param connectTimeoutMs
	 * @return
	 * @throws IOException
	 */

	public static String doGet(String url, Map<String, String> params, int readTimeoutMs, int connectTimeoutMs)
			throws IOException {
		HttpClient httpClient = getHttpClient();
		HttpGet httpGet = getForm(url, params);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(readTimeoutMs)
				.setConnectTimeout(connectTimeoutMs).build();
		httpGet.setConfig(requestConfig);
		httpGet.addHeader("User-Agent", "  ");
		HttpResponse httpResponse = httpClient.execute(httpGet);
		HttpEntity httpEntity = httpResponse.getEntity();
		return EntityUtils.toString(httpEntity, "UTF-8");
	}


/**
	 * 异步POST提交
	 */

	public static void doAsyncPost(String url, Map<String, String> params, int readTimeoutMs, int connectTimeoutMs,
			final String orderNo) throws UnsupportedEncodingException {
		// 设置请求
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(readTimeoutMs)
				.setConnectTimeout(connectTimeoutMs).build();

		final CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom().setDefaultRequestConfig(requestConfig)
				.build();

		HttpPost httpPost = new HttpPost(url);

		if (!CollectionUtils.isEmpty(params)) {
			List<NameValuePair> nameValuePairs = new ArrayList<>(params.size());
			for (Map.Entry<String, String> entry : params.entrySet()) {
				nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
		}

		httpclient.start();

		httpclient.execute(httpPost, new FutureCallback<HttpResponse>() {
			@Override
			public void completed(HttpResponse httpResponse) {
				handleRequestComplete(httpResponse, orderNo);
				close(httpclient);
			}

			@Override
			public void failed(Exception e) {
				close(httpclient);
			}

			@Override
			public void cancelled() {
				close(httpclient);
			}
		});
	}

	private static String getDate() {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(d);
	}

	private static void handleRequestComplete(HttpResponse httpResponse, String orderNo) {
		String body;
		// 这里使用EntityUtils.toString()方式时会大概率报错，原因：未接受完毕，链接已关
		try {
			System.out.println("异步请求状态码:" + httpResponse.getStatusLine().getStatusCode());
			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				final InputStream inputStream = entity.getContent();
				try {
					final StringBuilder sb = new StringBuilder();
					final char[] tmp = new char[1024];
					final Reader reader = new InputStreamReader(inputStream, "utf-8");
					int l;
					while ((l = reader.read(tmp)) != -1) {
						sb.append(tmp, 0, l);
					}
					body = sb.toString();
					// System.out.println("日期：" + getDate() + " 订单：" + orderNo + " 异步请求返回内容:" +
					// body);
				} finally {
					inputStream.close();
					EntityUtils.consume(entity);
				}
			}
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
	}

	private static void close(CloseableHttpAsyncClient client) {
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static HttpPost postForm(String url, Map<String, String> params) {

		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		Set<String> keySet = params.keySet();
		for (String key : keySet) {
			nvps.add(new BasicNameValuePair(key, params.get(key)));
		}

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return httpPost;
	}

	private static HttpGet getForm(String url, Map<String, String> params) {
		HttpGet httpGet = null;
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		if (params != null) {
			Set<String> keySet = params.keySet();
			for (String key : keySet) {
				nvps.add(new BasicNameValuePair(key, params.get(key)));
			}
		}
		String str = "";
		try {
			if (nvps != null && nvps.size() > 0)
				str = EntityUtils.toString(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (str.length() > 0) {
			httpGet = new HttpGet(url + "?" + str);
		} else {
			httpGet = new HttpGet(url);
		}

		return httpGet;
	}

	public static String formatDate(String time) {
		String result = StringUtils.EMPTY;
		if (StringUtils.isNotEmpty(time) && time.length() == 14) {
			String resultFormat = "{0}-{1}-{2} {3}:{4}:{5}";
			String year = StringUtils.substring(time, 0, 4);
			String month = StringUtils.substring(time, 4, 6);
			String day = StringUtils.substring(time, 6, 8);
			String hour = StringUtils.substring(time, 8, 10);
			String minute = StringUtils.substring(time, 10, 12);
			String second = StringUtils.substring(time, 12, 14);
			result = MessageFormat.format(resultFormat, year, month, day, hour, minute, second);
		}
		return result;
	}

	private static HttpClient getHttpClient() {
		BasicHttpClientConnectionManager connManager;
		connManager = new BasicHttpClientConnectionManager(RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.getSocketFactory())
				.register("https", SSLConnectionSocketFactory.getSocketFactory()).build(), null, null, null);
		HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(connManager).build();
		return httpClient;
	}

}

