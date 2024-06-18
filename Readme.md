# EventCore

EventCore is a Spring Boot based backend application designed to provide CRUD operations for local events. It serves as a comprehensive platform to view and manage events happening in your area.

## Features

- **Event Management**: Create, Read, Update, and Delete events.
- **Category Selection**: Users can select a category (e.g., concerts, family) to get a quick overview of relevant events.
- **Multiple Data Sources**: The application is fed by multiple data sources through additional parsers.
- **Location-Based Services**: Events are displayed based on the user's location.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

- Java 22 or higher
- Maven

### Installing

1. Clone the repository
```bash
git clone https://github.com/phpanhey/eventcore.git
```
2. Navigate to the project directory
```bash
cd eventcore
```
3. Build the project using Maven
```bash
mvn clean install
```
4. Run the Spring Boot application
```bash
mvn spring-boot:run
```

## Usage

Once the server is running, you can interact with the API using any HTTP client like curl or Postman.

## License

This project is licensed under the MIT License.