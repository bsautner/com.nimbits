# NimbitsApi.SnapshotApi

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
```javascript
import NimbitsApi from 'nimbits_api';
let defaultClient = NimbitsApi.ApiClient.instance;

// Configure API key authorization: x-api-key
let x-api-key = defaultClient.authentications['x-api-key'];
x-api-key.apiKey = 'YOUR API KEY';
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//x-api-key.apiKeyPrefix = 'Token';

let apiInstance = new NimbitsApi.SnapshotApi();

let xApiKey = "xApiKey_example"; // String | x-api-key

let id = "id_example"; // String | id


apiInstance.getSnapshot(xApiKey, id, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
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
```javascript
import NimbitsApi from 'nimbits_api';
let defaultClient = NimbitsApi.ApiClient.instance;

// Configure API key authorization: x-api-key
let x-api-key = defaultClient.authentications['x-api-key'];
x-api-key.apiKey = 'YOUR API KEY';
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//x-api-key.apiKeyPrefix = 'Token';

let apiInstance = new NimbitsApi.SnapshotApi();

let xApiKey = "xApiKey_example"; // String | x-api-key

let value = new NimbitsApi.Snapshot(); // Snapshot | value

let id = "id_example"; // String | id


apiInstance.postSnapshot(xApiKey, value, id, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
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

