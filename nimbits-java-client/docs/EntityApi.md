# EntityApi

All URIs are relative to *https://api.nimbits.io*

Method | HTTP request | Description
------------- | ------------- | -------------
[**addTopic**](EntityApi.md#addTopic) | **POST** /v5_0/api/entity/topic | AddTopic
[**deleteTopic**](EntityApi.md#deleteTopic) | **DELETE** /v5_0/api/entity/topic/{id} | DeleteTopic
[**getGroup**](EntityApi.md#getGroup) | **GET** /v5_0/api/group/{id} | GetGroup
[**getTopic**](EntityApi.md#getTopic) | **GET** /v5_0/api/entity/topic/{id} | GetTopic


<a name="addTopic"></a>
# **addTopic**
> Topic addTopic(xApiKey, topic)

AddTopic

Post a new Topic

### Example
```java
// Import classes:
//import com.ApiClient;
//import com.ApiException;
//import com.Configuration;
//import com.auth.*;
//import com.nimbits.EntityApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: x-api-key
ApiKeyAuth x-api-key = (ApiKeyAuth) defaultClient.getAuthentication("x-api-key");
x-api-key.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//x-api-key.setApiKeyPrefix("Token");

EntityApi apiInstance = new EntityApi();
String xApiKey = "xApiKey_example"; // String | x-api-key
Topic topic = new Topic(); // Topic | topic
try {
    Topic result = apiInstance.addTopic(xApiKey, topic);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling EntityApi#addTopic");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xApiKey** | **String**| x-api-key |
 **topic** | [**Topic**](Topic.md)| topic |

### Return type

[**Topic**](Topic.md)

### Authorization

[x-api-key](../README.md#x-api-key)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="deleteTopic"></a>
# **deleteTopic**
> Topic deleteTopic(xApiKey, id)

DeleteTopic

Delete a Topic

### Example
```java
// Import classes:
//import com.ApiClient;
//import com.ApiException;
//import com.Configuration;
//import com.auth.*;
//import com.nimbits.EntityApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: x-api-key
ApiKeyAuth x-api-key = (ApiKeyAuth) defaultClient.getAuthentication("x-api-key");
x-api-key.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//x-api-key.setApiKeyPrefix("Token");

EntityApi apiInstance = new EntityApi();
String xApiKey = "xApiKey_example"; // String | x-api-key
String id = "id_example"; // String | id
try {
    Topic result = apiInstance.deleteTopic(xApiKey, id);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling EntityApi#deleteTopic");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xApiKey** | **String**| x-api-key |
 **id** | **String**| id |

### Return type

[**Topic**](Topic.md)

### Authorization

[x-api-key](../README.md#x-api-key)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="getGroup"></a>
# **getGroup**
> EntityGroup getGroup(xApiKey, id)

GetGroup

Get Groups With all Connected Entities

### Example
```java
// Import classes:
//import com.ApiClient;
//import com.ApiException;
//import com.Configuration;
//import com.auth.*;
//import com.nimbits.EntityApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: x-api-key
ApiKeyAuth x-api-key = (ApiKeyAuth) defaultClient.getAuthentication("x-api-key");
x-api-key.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//x-api-key.setApiKeyPrefix("Token");

EntityApi apiInstance = new EntityApi();
String xApiKey = "xApiKey_example"; // String | x-api-key
String id = "id_example"; // String | id
try {
    EntityGroup result = apiInstance.getGroup(xApiKey, id);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling EntityApi#getGroup");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xApiKey** | **String**| x-api-key |
 **id** | **String**| id |

### Return type

[**EntityGroup**](EntityGroup.md)

### Authorization

[x-api-key](../README.md#x-api-key)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="getTopic"></a>
# **getTopic**
> Topic getTopic(xApiKey, id)

GetTopic

Get a Topic

### Example
```java
// Import classes:
//import com.ApiClient;
//import com.ApiException;
//import com.Configuration;
//import com.auth.*;
//import com.nimbits.EntityApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: x-api-key
ApiKeyAuth x-api-key = (ApiKeyAuth) defaultClient.getAuthentication("x-api-key");
x-api-key.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//x-api-key.setApiKeyPrefix("Token");

EntityApi apiInstance = new EntityApi();
String xApiKey = "xApiKey_example"; // String | x-api-key
String id = "id_example"; // String | id
try {
    Topic result = apiInstance.getTopic(xApiKey, id);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling EntityApi#getTopic");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xApiKey** | **String**| x-api-key |
 **id** | **String**| id |

### Return type

[**Topic**](Topic.md)

### Authorization

[x-api-key](../README.md#x-api-key)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

