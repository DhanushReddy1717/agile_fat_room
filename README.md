# E-Commerce Order Management System

This project implements order processing with inventory updates and payment handling using Java and Maven.

## Features
- Order processing workflow with status transitions
- Inventory validation and stock deduction/restoration
- Payment handling abstraction
- JUnit tests for success and failure scenarios
- CI pipeline with GitHub Actions
- CD pipeline for Docker image push and Kubernetes deployment
- Docker and Kubernetes deployment manifests

## Tech Stack
- Java 17
- Apache Maven
- JUnit 5
- GitHub Actions / Jenkins
- Docker / Kubernetes

## Project Structure
- src/main/java: application source
- src/test/java: unit tests
- .github/workflows: CI/CD workflows
- docker: Docker compose file
- k8s: Kubernetes manifests

## Build And Test
```bash
mvn clean verify
```

## Run Locally
```bash
mvn clean package
java -jar target/ecommerce-order-management-1.0.0.jar
```

## Docker
```bash
docker build -t ecommerce-order-management:latest .
docker run --rm -p 8080:8080 ecommerce-order-management:latest
```

## Kubernetes
1. Update image in k8s/deployment.yaml with your Docker Hub username.
2. Apply manifests:

```bash
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

## GitHub Setup
1. Initialize repository:

```bash
git init
git add .
git commit -m "Initial commit: e-commerce order management system"
```

2. Create GitHub repository and push:

```bash
git branch -M main
git remote add origin <your-repo-url>
git push -u origin main
```

## Required GitHub Secrets
- DOCKERHUB_TOKEN
- KUBECONFIG
