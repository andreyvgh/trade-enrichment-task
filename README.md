# Trade Data Enrichment Service

This service provides an API to enrich trade data with product names from a static data file. The service translates `product_id` into `product_name`, validates the trade data, and handles large sets of trades efficiently.

## How to Run the Service

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/very-good-bank/trade-enrichment-task.git
2. **Build the Project:**
*ensure you have Maven installed. Run the following command to build the project:*
   ```bash
   mvn clean install
3. **Run the Service:**
   ```bash
   mvn spring-boot:run
   ```
    *Alternatively, you can run the generated JAR file:*
   ```bash
    java -jar target/trade-enrichment-service-0.0.1-SNAPSHOT.jar
    ```
4. **Run the Service:**
   The service will be available at http://localhost:8080.

## How to Use the API
Enrich Trade Data
Endpoint:
`POST /api/v1/enrich`


## Description:
This endpoint accepts a CSV 
file containing trade data 
and returns the enriched 
trade data with product names.


## Request:

Content-Type: `text/csv`

Example trade.csv:
```csv
date,product_id,currency,price
20160101,1,EUR,10.0
20160101,2,EUR,20.1
20160101,3,EUR,30.34
20160101,11,EUR,35
```


## Sample HTTP Request
This sample command may be modified depending on the implementation, but must be documented.
```curl
curl --data @src/test/resources/trade.csv --header 'Content-Type: text/csv' http://localhost:8080/api/v1/enrich
```

## Sample HTTP Response
```csv
date,product_name,currency,price
20160101,Treasury Bills Domestic,EUR,10
20160101,Corporate Bonds Domestic,EUR,20.1
20160101,REPO Domestic,EUR,30.34
20160101,Missing Product Name,EUR,35.34
```

## Limitations of the Code
- The service reads and processes the entire CSV file in memory,
which might not be optimal for extremely large files.
This is mitigated by pagination, but further optimizations
could be necessary.
- The product data is loaded into a cache,
which has a fixed size. If the number of products exceeds
the cache size, older entries will be evicted,
potentially causing performance issues if frequently
accessed entries are evicted.
- Error handling could be improved to provide more detailed
feedback to the user.


## Discussion/Comment on the Design
- The service uses Spring Boot for ease of setup and dependency management.
- The trade data validation is performed in a separate validator class to keep the code modular and maintainable.
- A simple LRU cache is implemented to handle the product data efficiently.
- The service is designed to be thread-safe by using read-write locks around the cache access.


## Ideas for Improvement

If more time were available, the following improvements could be made:

- Asynchronous Processing: Implement asynchronous processing for handling large files to
improve performance and responsiveness.
- Improved Caching Strategy: Use a more sophisticated caching mechanism,
potentially integrating with a distributed cache like Redis to handle larger datasets.
- Enhanced Validation: Add more comprehensive validation rules and error messages.
- API Documentation: Use Swagger or similar tools to provide interactive API documentation.
- Testing: Increase test coverage, including more integration and performance tests.
- Configuration: Externalize more configurations to application properties for greater flexibility.