# NimbitsApi.TopicApi

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
```javascript
import NimbitsApi from 'nimbits_api';
let defaultClient = NimbitsApi.ApiClient.instance;

// Configure API key authorization: x-api-key
let x-api-key = defaultClient.authentications['x-api-key'];
x-api-key.apiKey = 'YOUR API KEY';
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//x-api-key.apiKeyPrefix = 'Token';

let apiInstance = new NimbitsApi.TopicApi();

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

let apiInstance = new NimbitsApi.TopicApi();

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

<a name="getDataTable"></a>
# **getDataTable**
> ChartDTO getDataTable(xApiKey, id, opts)

GetDataTable

Get A Data Table For a Topic

### Example
```javascript
import NimbitsApi from 'nimbits_api';
let defaultClient = NimbitsApi.ApiClient.instance;

// Configure API key authorization: x-api-key
let x-api-key = defaultClient.authentications['x-api-key'];
x-api-key.apiKey = 'YOUR API KEY';
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//x-api-key.apiKeyPrefix = 'Token';

let apiInstance = new NimbitsApi.TopicApi();

let xApiKey = "xApiKey_example"; // String | x-api-key

let id = "id_example"; // String | id

let opts = { 
  'start': "start_example", // String | start
  'end': "end_example", // String | end
  'count': "count_example", // String | count
  'mask': "mask_example" // String | mask
};

apiInstance.getDataTable(xApiKey, id, opts, (error, data, response) => {
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
```javascript
import NimbitsApi from 'nimbits_api';
let defaultClient = NimbitsApi.ApiClient.instance;

// Configure API key authorization: x-api-key
let x-api-key = defaultClient.authentications['x-api-key'];
x-api-key.apiKey = 'YOUR API KEY';
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//x-api-key.apiKeyPrefix = 'Token';

let apiInstance = new NimbitsApi.TopicApi();

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

let apiInstance = new NimbitsApi.TopicApi();

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

let apiInstance = new NimbitsApi.TopicApi();

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

let apiInstance = new NimbitsApi.TopicApi();

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

let apiInstance = new NimbitsApi.TopicApi();

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

