/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */
package com.nimbits.cloudplatform.server.api.openid.appengine;

import com.google.appengine.api.urlfetch.*;
import com.google.inject.Singleton;
import com.google.step2.http.FetchException;
import com.google.step2.http.FetchRequest;
import com.google.step2.http.FetchResponse;
import com.google.step2.http.HttpFetcher;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;

@Singleton
public class AppEngineHttpFetcher implements HttpFetcher {

	private static class AppEngineFetchResponse implements FetchResponse {

		private final HTTPResponse httpResponse;

		public AppEngineFetchResponse(HTTPResponse httpResponse) {
			this.httpResponse = httpResponse;
		}

		public byte[] getContentAsBytes() {
			return httpResponse.getContent();
		}

		public InputStream getContentAsStream() {
			return new ByteArrayInputStream(getContentAsBytes());
		}

		public String getFirstHeader(String name) {
			List<HTTPHeader> headers = httpResponse.getHeaders();
			for (HTTPHeader header : headers) {
				if (header.getName().equalsIgnoreCase(name)) {
					return header.getValue();
				}
			}
			return null;
		}

		public int getStatusCode() {
			return httpResponse.getResponseCode();
		}
	}

	private final URLFetchService fetchService;

	public AppEngineHttpFetcher() {
		fetchService = URLFetchServiceFactory.getURLFetchService();
	}

	public FetchResponse fetch(FetchRequest request) throws FetchException {

		HTTPMethod method;
		switch (request.getMethod()) {
		case POST:
			method = HTTPMethod.POST;
			break;
		case HEAD:
			method = HTTPMethod.HEAD;
			break;
		default:
			method = HTTPMethod.GET;
			break;
		}

		try {

			HTTPRequest httpRequest = new HTTPRequest(request.getUri().toURL(),
					method);
			HTTPResponse httpResponse = fetchService.fetch(httpRequest);
			return new AppEngineFetchResponse(httpResponse);

		} catch (MalformedURLException e) {
			throw new FetchException(e);
		} catch (IOException e) {
			throw new FetchException(e);
		}
	}
}
