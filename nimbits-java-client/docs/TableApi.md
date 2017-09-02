# TableApi

All URIs are relative to *https://api.nimbits.io*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getDataTable**](TableApi.md#getDataTable) | **GET** /v5_0/api/table/topic/{id} | GetDataTable


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
//import com.nimbits.TableApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();

// Configure API key authorization: x-api-key
ApiKeyAuth x-api-key = (ApiKeyAuth) defaultClient.getAuthentication("x-api-key");
x-api-key.setApiKey("YOUR API KEY");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//x-api-key.setApiKeyPrefix("Token");

TableApi apiInstance = new TableApi();
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
    System.err.println("Exception when calling TableApi#getDataTable");
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

