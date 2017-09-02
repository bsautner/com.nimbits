# NimbitsApi.GroupApi

All URIs are relative to *https://api.nimbits.io*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getGroup**](GroupApi.md#getGroup) | **GET** /v5_0/api/group/{id} | GetGroup
[**getGroups**](GroupApi.md#getGroups) | **GET** /v5_0/api/group | GetGroups


<a name="getGroup"></a>
# **getGroup**
> EntityGroup getGroup(xApiKey, id)

GetGroup

Get Groups With all Connected Entities

### Example
```javascript
import NimbitsApi from 'nimbits_api';
let defaultClient = NimbitsApi.ApiClient.instance;

// Configure API key authorization: x-api-key
let x-api-key = defaultClient.authentications['x-api-key'];
x-api-key.apiKey = 'YOUR API KEY';
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//x-api-key.apiKeyPrefix = 'Token';

let apiInstance = new NimbitsApi.GroupApi();

let xApiKey = "xApiKey_example"; // String | x-api-key

let id = "id_example"; // String | id


apiInstance.getGroup(xApiKey, id, (error, data, response) => {
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

[**EntityGroup**](EntityGroup.md)

### Authorization

[x-api-key](../README.md#x-api-key)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="getGroups"></a>
# **getGroups**
> [EntityGroup] getGroups(xApiKey)

GetGroups

Get All Groups For an Account

### Example
```javascript
import NimbitsApi from 'nimbits_api';
let defaultClient = NimbitsApi.ApiClient.instance;

// Configure API key authorization: x-api-key
let x-api-key = defaultClient.authentications['x-api-key'];
x-api-key.apiKey = 'YOUR API KEY';
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//x-api-key.apiKeyPrefix = 'Token';

let apiInstance = new NimbitsApi.GroupApi();

let xApiKey = "xApiKey_example"; // String | x-api-key


apiInstance.getGroups(xApiKey, (error, data, response) => {
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

### Return type

[**[EntityGroup]**](EntityGroup.md)

### Authorization

[x-api-key](../README.md#x-api-key)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

