# NimbitsApi.TableApi

All URIs are relative to *https://api.nimbits.io*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getDataTable**](TableApi.md#getDataTable) | **GET** /v5_0/api/table/topic/{id} | GetDataTable


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

let apiInstance = new NimbitsApi.TableApi();

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

