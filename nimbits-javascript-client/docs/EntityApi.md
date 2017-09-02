# NimbitsApi.EntityApi

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
```javascript
import NimbitsApi from 'nimbits_api';
let defaultClient = NimbitsApi.ApiClient.instance;

// Configure API key authorization: x-api-key
let x-api-key = defaultClient.authentications['x-api-key'];
x-api-key.apiKey = 'YOUR API KEY';
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//x-api-key.apiKeyPrefix = 'Token';

let apiInstance = new NimbitsApi.EntityApi();

let xApiKey = "xApiKey_example"; // String | x-api-key

let topic = new NimbitsApi.Topic(); // Topic | topic


apiInstance.addTopic(xApiKey, topic, (error, data, response) => {
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
```javascript
import NimbitsApi from 'nimbits_api';
let defaultClient = NimbitsApi.ApiClient.instance;

// Configure API key authorization: x-api-key
let x-api-key = defaultClient.authentications['x-api-key'];
x-api-key.apiKey = 'YOUR API KEY';
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//x-api-key.apiKeyPrefix = 'Token';

let apiInstance = new NimbitsApi.EntityApi();

let xApiKey = "xApiKey_example"; // String | x-api-key

let id = "id_example"; // String | id


apiInstance.deleteTopic(xApiKey, id, (error, data, response) => {
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
```javascript
import NimbitsApi from 'nimbits_api';
let defaultClient = NimbitsApi.ApiClient.instance;

// Configure API key authorization: x-api-key
let x-api-key = defaultClient.authentications['x-api-key'];
x-api-key.apiKey = 'YOUR API KEY';
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//x-api-key.apiKeyPrefix = 'Token';

let apiInstance = new NimbitsApi.EntityApi();

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

<a name="getTopic"></a>
# **getTopic**
> Topic getTopic(xApiKey, id)

GetTopic

Get a Topic

### Example
```javascript
import NimbitsApi from 'nimbits_api';
let defaultClient = NimbitsApi.ApiClient.instance;

// Configure API key authorization: x-api-key
let x-api-key = defaultClient.authentications['x-api-key'];
x-api-key.apiKey = 'YOUR API KEY';
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//x-api-key.apiKeyPrefix = 'Token';

let apiInstance = new NimbitsApi.EntityApi();

let xApiKey = "xApiKey_example"; // String | x-api-key

let id = "id_example"; // String | id


apiInstance.getTopic(xApiKey, id, (error, data, response) => {
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

[**Topic**](Topic.md)

### Authorization

[x-api-key](../README.md#x-api-key)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

