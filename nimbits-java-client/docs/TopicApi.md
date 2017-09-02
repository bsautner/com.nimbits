# TopicApi

All URIs are relative to *https://api.nimbits.io*

Method | HTTP request | Description
------------- | ------------- | -------------
[**addTopic**](TopicApi.md#addTopic) | **POST** /v5_0/api/entity/topic | AddTopic
[**deleteTopic**](TopicApi.md#deleteTopic) | **DELETE** /v5_0/api/entity/topic/{id} | DeleteTopic
[**getDataTable**](TopicApi.md#getDataTable) | **GET** /v5_0/api/table/topic/{id} | GetDataTable
[**getGroup**](TopicApi.md#getGroup) | **GET** /v5_0/api/group/{id} | GetGroup
[**getGroups**](TopicApi.md#getGroups) | **GET** /v5_0/api/group | GetGroups
[**getSnapshot**](TopicApi.md#getSnapshot) | **GET** /v5_0/api/snapshot/{id} | GetSnapshot
[**getTopic**](TopicApi.md#getTopic) | **GET** /v5_0/api/entity/topic/{id} | GetTopic
[**postSnapshot**](TopicApi.md#postSnapshot) | **POST** /v5_0/api/snapshot/{id} | PostSnapshot


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
//import com.nimbits.TopicApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: x-api-key
ApiKeyAuth x-api-key = (ApiKeyAuth) defaultClient.getAuthentication("x-api-key");
x-api-key.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//x-api-key.setApiKeyPrefix("Token");

TopicApi apiInstance = new TopicApi();
String xApiKey = "xApiKey_example"; // String | x-api-key
Topic topic = new Topic(); // Topic | topic
try {
    Topic result = apiInstance.addTopic(xApiKey, topic);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling TopicApi#addTopic");
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
//import com.nimbits.TopicApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: x-api-key
ApiKeyAuth x-api-key = (ApiKeyAuth) defaultClient.getAuthentication("x-api-key");
x-api-key.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//x-api-key.setApiKeyPrefix("Token");

TopicApi apiInstance = new TopicApi();
String xApiKey = "xApiKey_example"; // String | x-api-key
String id = "id_example"; // String | id
try {
    Topic result = apiInstance.deleteTopic(xApiKey, id);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling TopicApi#deleteTopic");
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

<a name="getDataTable"></a>
# **getDataTable**
> ChartDTO getDataTable(xApiKey, id, start, end, count, mask)

GetDataTable

Get A Data Table For a Topic

### Example
```java
// Import classes:
//import com.ApiClient;
//import com.ApiException;
//import com.Configuration;
//import com.auth.*;
//import com.nimbits.TopicApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: x-api-key
ApiKeyAuth x-api-key = (ApiKeyAuth) defaultClient.getAuthentication("x-api-key");
x-api-key.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//x-api-key.setApiKeyPrefix("Token");

TopicApi apiInstance = new TopicApi();
String xApiKey = "xApiKey_example"; // String | x-api-key
String id = "id_example"; // String | id
String start = "start_example"; // String | start
String end = "end_example"; // String | end
String count = "count_example"; // String | count
String mask = "mask_example"; // String | mask
try {
    ChartDTO result = apiInstance.getDataTable(xApiKey, id, start, end, count, mask);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling TopicApi#getDataTable");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xApiKey** | **String**| x-api-key |
 **id** | **String**| id |
 **start** | **String**| start | [optional]
 **end** | **String**| end | [optional]
 **count** | **String**| count | [optional]
 **mask** | **String**| mask | [optional]

### Return type

[**ChartDTO**](ChartDTO.md)

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
//import com.nimbits.TopicApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: x-api-key
ApiKeyAuth x-api-key = (ApiKeyAuth) defaultClient.getAuthentication("x-api-key");
x-api-key.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//x-api-key.setApiKeyPrefix("Token");

TopicApi apiInstance = new TopicApi();
String xApiKey = "xApiKey_example"; // String | x-api-key
String id = "id_example"; // String | id
try {
    EntityGroup result = apiInstance.getGroup(xApiKey, id);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling TopicApi#getGroup");
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

<a name="getGroups"></a>
# **getGroups**
> List&lt;EntityGroup&gt; getGroups(xApiKey)

GetGroups

Get All Groups For an Account

### Example
```java
// Import classes:
//import com.ApiClient;
//import com.ApiException;
//import com.Configuration;
//import com.auth.*;
//import com.nimbits.TopicApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: x-api-key
ApiKeyAuth x-api-key = (ApiKeyAuth) defaultClient.getAuthentication("x-api-key");
x-api-key.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//x-api-key.setApiKeyPrefix("Token");

TopicApi apiInstance = new TopicApi();
String xApiKey = "xApiKey_example"; // String | x-api-key
try {
    List<EntityGroup> result = apiInstance.getGroups(xApiKey);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling TopicApi#getGroups");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xApiKey** | **String**| x-api-key |

### Return type

[**List&lt;EntityGroup&gt;**](EntityGroup.md)

### Authorization

[x-api-key](../README.md#x-api-key)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="getSnapshot"></a>
# **getSnapshot**
> Snapshot getSnapshot(xApiKey, id)

GetSnapshot

Get Snapshot for a Topic

### Example
```java
// Import classes:
//import com.ApiClient;
//import com.ApiException;
//import com.Configuration;
//import com.auth.*;
//import com.nimbits.TopicApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: x-api-key
ApiKeyAuth x-api-key = (ApiKeyAuth) defaultClient.getAuthentication("x-api-key");
x-api-key.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//x-api-key.setApiKeyPrefix("Token");

TopicApi apiInstance = new TopicApi();
String xApiKey = "xApiKey_example"; // String | x-api-key
String id = "id_example"; // String | id
try {
    Snapshot result = apiInstance.getSnapshot(xApiKey, id);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling TopicApi#getSnapshot");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xApiKey** | **String**| x-api-key |
 **id** | **String**| id |

### Return type

[**Snapshot**](Snapshot.md)

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
//import com.nimbits.TopicApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: x-api-key
ApiKeyAuth x-api-key = (ApiKeyAuth) defaultClient.getAuthentication("x-api-key");
x-api-key.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//x-api-key.setApiKeyPrefix("Token");

TopicApi apiInstance = new TopicApi();
String xApiKey = "xApiKey_example"; // String | x-api-key
String id = "id_example"; // String | id
try {
    Topic result = apiInstance.getTopic(xApiKey, id);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling TopicApi#getTopic");
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

<a name="postSnapshot"></a>
# **postSnapshot**
> Snapshot postSnapshot(xApiKey, value, id)

PostSnapshot

Post a Snapshot to a Topic

### Example
```java
// Import classes:
//import com.ApiClient;
//import com.ApiException;
//import com.Configuration;
//import com.auth.*;
//import com.nimbits.TopicApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: x-api-key
ApiKeyAuth x-api-key = (ApiKeyAuth) defaultClient.getAuthentication("x-api-key");
x-api-key.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//x-api-key.setApiKeyPrefix("Token");

TopicApi apiInstance = new TopicApi();
String xApiKey = "xApiKey_example"; // String | x-api-key
Snapshot value = new Snapshot(); // Snapshot | value
String id = "id_example"; // String | id
try {
    Snapshot result = apiInstance.postSnapshot(xApiKey, value, id);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling TopicApi#postSnapshot");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xApiKey** | **String**| x-api-key |
 **value** | [**Snapshot**](Snapshot.md)| value |
 **id** | **String**| id |

### Return type

[**Snapshot**](Snapshot.md)

### Authorization

[x-api-key](../README.md#x-api-key)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

