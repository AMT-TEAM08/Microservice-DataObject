# Data Object Microservice
This microservice provides a set of REST APIs for managing data objects stored in Amazon Web Services (AWS). It uses the AWSDataObjectHelperImpl class to communicate with AWS and perform various actions such as listing, uploading, deleting, and getting download URLs for data objects stored in AWS.

It has been developped in the context of the AMT Lab along with :
* [The Label Detector Microservice](https://github.com/Nelson-Jnrnd/AMT-Microservice-LabelDetector)
* [The main application](https://github.com/Nelson-Jnrnd/AMT-Microservice-Main)

## Additionnal information

More general information on the project are available on this [wiki](https://github.com/Nelson-Jnrnd/AMT-Microservice-Main/wiki)

## Prerequisites
* A valid AWS account with the S3 service enabled
* Java 8 or higher
* Maven

## API Endpoints
The following API endpoints are available:

### List Objects
```
GET /objects
```
Lists all the data objects stored in AWS.

#### Response
* On success, returns an array of strings, each representing the name of a data object.
* On access denied, returns a 403 Forbidden HTTP status code.
* On data object not found, returns a 404 Not Found HTTP status code.
* On any other error, returns a 500 Internal Server Error HTTP status code.

### Upload Object
```
POST /objects
```

Uploads a new data object to AWS. The data object is provided in the request as a multipart/form-data file.

#### Response
* On success, returns an HTTP status code 200 OK.
* On invalid parameters, returns a 400 Bad Request HTTP status code with an error message in the body.
* On access denied, returns a 403 Forbidden HTTP status code.
* On data object not found, returns a 404 Not Found HTTP status code.
* On any other error, returns a 500 Internal Server Error HTTP status code.

### Get Object Download URL
```
GET /objects/{objectName}
```

Gets a download URL for a data object stored in AWS. The URL will be valid for the specified duration (in minutes).

#### Parameters
* objectName: The name of the data object.
* duration: The duration (in minutes) for which the URL will be valid.

#### Response
* On success, returns the download URL as a string in the body.
* On invalid parameters, returns a 400 Bad Request HTTP status code with an error message in the body.
* On access denied, returns a 403 Forbidden HTTP status code.
* On key not found, returns a 404 Not Found HTTP status code.
* On any other error, returns a 500 Internal Server Error HTTP status code.

### Delete Object
```
DELETE /objects/{objectName}
```

Deletes a data object from AWS.

#### Parameters
* objectName: The name of the data object.

#### Response
* On success, returns an HTTP status code 204 No Content.
* On invalid parameters, returns a 400 Bad Request HTTP status code with an error message in the body.
* On access denied, returns a 403 Forbidden HTTP status code.
* On any other error, returns a 500 Internal Server Error HTTP status code.

## Dependencies
This microservice has the following dependencies:

* Spring Framework
* Amazon Web Services SDK for Java
