# com.nimbits.io

## Requirements

Building the API client library requires [Maven](https://maven.apache.org/) to be installed.

## Installation

To install the API client library to your local Maven repository, simply execute:

```shell
mvn install
```

To deploy it to a remote Maven repository instead, configure the settings of the repository and execute:

```shell
mvn deploy
```

Refer to the [official documentation](https://maven.apache.org/plugins/maven-deploy-plugin/usage.html) for more information.

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
    <groupId>com.nimbits</groupId>
    <artifactId>com.nimbits.io</artifactId>
    <version>5.0.0</version>
    <scope>compile</scope>
</dependency>
```

### Gradle users

Add this dependency to your project's build file:

```groovy
compile "com.nimbits:com.nimbits.io:5.0.0"
```

### Others

At first generate the JAR by executing:

    mvn package

Then manually install the following JARs:

* target/com.nimbits.io-5.0.0.jar
* target/lib/*.jar

## Getting Started

Please follow the [installation](#installation) instruction and execute the following Java code:

```java

import com.*;
import com.auth.*;
import com.nimbits.io.*;
import com.nimbits.EntityApi;

import java.io.File;
import java.util.*;

public class EntityApiExample {

    public static void main(String[] args) {
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
    }
}

```

## Documentation for API Endpoints

All URIs are relative to *https://api.nimbits.io*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*EntityApi* | [**addTopic**](docs/EntityApi.md#addTopic) | **POST** /v5_0/api/entity/topic | AddTopic
*EntityApi* | [**deleteTopic**](docs/EntityApi.md#deleteTopic) | **DELETE** /v5_0/api/entity/topic/{id} | DeleteTopic
*EntityApi* | [**getGroup**](docs/EntityApi.md#getGroup) | **GET** /v5_0/api/group/{id} | GetGroup
*EntityApi* | [**getTopic**](docs/EntityApi.md#getTopic) | **GET** /v5_0/api/entity/topic/{id} | GetTopic
*GroupApi* | [**getGroup**](docs/GroupApi.md#getGroup) | **GET** /v5_0/api/group/{id} | GetGroup
*GroupApi* | [**getGroups**](docs/GroupApi.md#getGroups) | **GET** /v5_0/api/group | GetGroups
*SnapshotApi* | [**getSnapshot**](docs/SnapshotApi.md#getSnapshot) | **GET** /v5_0/api/snapshot/{id} | GetSnapshot
*SnapshotApi* | [**postSnapshot**](docs/SnapshotApi.md#postSnapshot) | **POST** /v5_0/api/snapshot/{id} | PostSnapshot
*TableApi* | [**getDataTable**](docs/TableApi.md#getDataTable) | **GET** /v5_0/api/table/topic/{id} | GetDataTable
*TopicApi* | [**addTopic**](docs/TopicApi.md#addTopic) | **POST** /v5_0/api/entity/topic | AddTopic
*TopicApi* | [**deleteTopic**](docs/TopicApi.md#deleteTopic) | **DELETE** /v5_0/api/entity/topic/{id} | DeleteTopic
*TopicApi* | [**getDataTable**](docs/TopicApi.md#getDataTable) | **GET** /v5_0/api/table/topic/{id} | GetDataTable
*TopicApi* | [**getGroup**](docs/TopicApi.md#getGroup) | **GET** /v5_0/api/group/{id} | GetGroup
*TopicApi* | [**getGroups**](docs/TopicApi.md#getGroups) | **GET** /v5_0/api/group | GetGroups
*TopicApi* | [**getSnapshot**](docs/TopicApi.md#getSnapshot) | **GET** /v5_0/api/snapshot/{id} | GetSnapshot
*TopicApi* | [**getTopic**](docs/TopicApi.md#getTopic) | **GET** /v5_0/api/entity/topic/{id} | GetTopic
*TopicApi* | [**postSnapshot**](docs/TopicApi.md#postSnapshot) | **POST** /v5_0/api/snapshot/{id} | PostSnapshot


## Documentation for Models

 - [ChartColumnDefinition](docs/ChartColumnDefinition.md)
 - [ChartDTO](docs/ChartDTO.md)
 - [ChartDataColumn](docs/ChartDataColumn.md)
 - [EntityGroup](docs/EntityGroup.md)
 - [ResponseEntity](docs/ResponseEntity.md)
 - [Row](docs/Row.md)
 - [Snapshot](docs/Snapshot.md)
 - [Topic](docs/Topic.md)


## Documentation for Authorization

Authentication schemes defined for the API:
### x-api-key

- **Type**: API key
- **API key parameter name**: x-api-key
- **Location**: HTTP header


## Recommendation

It's recommended to create an instance of `ApiClient` per thread in a multithreaded environment to avoid any potential issues.

## Author

support@nimbits.com

