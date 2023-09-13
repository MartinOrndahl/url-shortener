# Martin's URL Shortener

A straightforward URL shortener (though it's worth noting that SHA1 values consist of 40 hex characters, so they're not
particularly short...)

## Installation

Build the JAR with maven by running:

```bash
mvn clean package
```

Containerize and run the application with:

```bash
docker-compose up --build
```

## Usage

```bash
# create a new short link

# Returns {"linkId": "ef7efc9839c3ee036f023e9635bc3b056d6ee2db"}
curl -v -X POST 'http://localhost:8080/links?url=https://www.google.com'

# redirects the call towards 'https://www.google.com'
curl -v -X GET 'http://localhost:8080/links/ef7efc9839c3ee036f023e9635bc3b056d6ee2db'
```

> **_NOTE:_**  You can directly paste the link 'http://localhost:8080/links/ef7efc9839c3ee036f023e9635bc3b056d6ee2db' in
> your browser and it will redirect you to https://www.google.com

## About the project

Built using

- Java
- Spring Boot
- Postgres

The application employs multithreading for efficient storage and retrieval of data from a database. To improve
scalability, one can readily apply horizontal scaling by deploying additional service instances. However, it's worth
acknowledging that the database can become a bottleneck due to limitations on the number of connections it can handle.
Therefore, opting for a NoSQL database is a prudent choice, as they typically excel in both read and write speeds and at
handling a higher volume of connections.
