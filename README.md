# **E-Commerce Microservices Platform (Spring Boot)**

A cloud-native E-Commerce Microservices Architecture built using Spring Boot and Spring Cloud,
following best practices for scalability, security, and observability.

The system uses API Gateway as the single entry point, secured with OAuth2 authentication via Keycloak, 
and is fully containerized using Docker.

All services are containerized and deployed using Docker and orchestrated via Docker Compose.

## Architecture Overview

### Security Layer

* OAuth2 / JWT-based authentication
* Identity Provider: Keycloak
* All client requests pass through the API Gateway
* Gateway validates JWT before forwarding requests
* Role-based access control supported

### Microservices
**Service                            Responsibility**

* Config Server :-                        Centralized configuration management
* Eureka Discovery Server :-              Service registry & discovery
* Gateway Service :-                     Single entry point, routing, authentication
* User Service   :-                       User registration & management
* Product Service  :-                     Product catalog management
* Order Service   :-                     Order lifecycle management
* Payment Service  :-                     Payment processing
* Notification Service :-                 Receive ordered Product report from order service

## Service Communication
### Asynchronous Messaging
Inter-service communication is handled using:
* Apache Kafka

Event-Driven Flow Example:

Order Created → Kafka Event → Payment Service

Payment Success → Kafka Event(Fanout) → Update Order  status, Update Product quantity

This ensures:
* Loose coupling
* Scalability
* Fault tolerance
* Event-driven architecture


Payment Integration

Payments are processed via:
* Stripe

Features:
* Secure payment intent creation
* API key-based authentication
* Order-to-payment orchestration
* Payment status events via Kafka


## Observability Stack

The system includes full monitoring, tracing, and centralized logging:

Tool                                        Purpose
* Prometheus :-                             Metrics scraping
* Grafana    :-                             Metrics dashboards
* Loki       :-                             Centralized logging
* Zipkin     :-                             Distributed tracing

Observability Features:
* Micrometer metrics
* Distributed request tracing
* Centralized log aggregation
* Real-time dashboards


## Deployment Strategy

The entire platform is containerized using:
* Docker
* Docker Compose orchestration
* Environment-based configuration via .env file
Each microservice runs in its own container with internal networking.


## Project Structure

ecommerce-microservices/
│

├── config-server/

├── discovery-server/

├── gateway-service/

├── user-service/

├── product-service/

├── order-service/

├── payment-service/

├── notification-service/


├── kafka/

├── observability/

│      ├── prometheus/

│      ├── grafana/

│      ├── loki/

│      └── zipkin/


└── deploy/

├── docker-compose.yml

├── .env

└── clean-build.sh


## How to Run the Project
_IMPORTANT:_
Ensure all required environment variables are properly defined in the .env file located inside 
the deploy/ directory before starting.

#### Step 1: Clean & Build All Services
From deploy/  dir:
./clean-build.sh

#### Step 2: Start the Entire Platform
Navigate to the deploy/ directory and run:
docker compose up --build -d

#### Step 3️: Test APIs via Postman
* Open Postman
* Import:
  Microservice_Ecom(Keycloak).postman_collection.json