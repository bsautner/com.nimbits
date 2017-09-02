# SnapshotApi

All URIs are relative to *https://api.nimbits.io*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getSnapshot**](SnapshotApi.md#getSnapshot) | **GET** /v5_0/api/snapshot/{id} | GetSnapshot
[**postSnapshot**](SnapshotApi.md#postSnapshot) | **POST** /v5_0/api/snapshot/{id} | PostSnapshot


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
//import com.nimbits.SnapshotApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: x-api-key
ApiKeyAuth x-api-key = (ApiKeyAuth) defaultClient.getAuthentication("x-api-key");
x-api-key.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//x-api-key.setApiKeyPrefix("Token");

SnapshotApi apiInstance = new SnapshotApi();
String xApiKey = "xApiKey_example"; // String | x-api-key
String id = "id_example"; // String | id
try {
    Snapshot result = apiInstance.getSnapshot(xApiKey, id);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling SnapshotApi#getSnapshot");
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
//import com.nimbits.SnapshotApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: x-api-key
ApiKeyAuth x-api-key = (ApiKeyAuth) defaultClient.getAuthentication("x-api-key");
x-api-key.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//x-api-key.setApiKeyPrefix("Token");

SnapshotApi apiInstance = new SnapshotApi();
String xApiKey = "xApiKey_example"; // String | x-api-key
Snapshot value = new Snapshot(); // Snapshot | value
String id = "id_example"; // String | id
try {
    Snapshot result = apiInstance.postSnapshot(xApiKey, value, id);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling SnapshotApi#postSnapshot");
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

