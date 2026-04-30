# Welcome to My Spring Portfolio
***

## Task
Build a web application to help an investor keep track of their active investments. The application must provide a home view summarizing all investments alongside a fund calculator, starting from an initial pool of $10,000,000 USD. The challenge lies in combining real-time fund tracking, full CRUD operations, client-side sorting and filtering, and a live push notification system — all within a cohesive full-stack Java application deployed to the cloud.

## Description
The application is built with Spring Boot (MVC) and Thymeleaf for server-side rendering, backed by a PostgreSQL database and deployed on AWS EC2 via Docker Compose.

Key features implemented:
- **Home view** — lists all active investments with dollar amounts and displays the remaining fund balance (initial $10M minus total invested)
- **Add to Fund** — allows users to increase the available investment pool at any time
- **Detailed view** — clicking an investment opens a page showing its full details
- **Update** — each investment's name can be edited inline
- **Sort & Filter** — investments can be sorted by dollar amount (ascending/descending) or alphabetically, executed client-side in JavaScript without a page reload
- **2-minute notification (Bonus)** — after a new investment is created, a Server-Sent Events (SSE) push notification is delivered to the browser exactly 2 minutes later using a Spring scheduled task

## Installation

**Prerequisites:** Docker and Docker Compose installed on your machine.

1. Clone the repository:
```
git clone https://github.com/devbossma/My-Spring-Portfolio.git
cd My-Spring-Portfolio
```

2. Create a `.env` file from the example and set your database password:
```
cp .env.example .env
```

3. Build and start all services (app + PostgreSQL):
```
docker compose up --build
```

The application will be available at `http://localhost`.

> To run locally without Docker, ensure Java 21+ and PostgreSQL are installed, configure `src/main/resources/application.properties` with your DB credentials, then run `./mvnw spring-boot:run`.

## Usage

**Live demo:** http://ec2-18-234-109-220.compute-1.amazonaws.com

```
http://localhost          # Home — fund summary + investment list
http://localhost/investments/new       # Add a new investment
http://localhost/investments/{id}      # Detailed view of a single investment
http://localhost/fund/add              # Add money to the fund
```

- On the home page, use the sort controls to order investments by amount or name
- Click any investment row to open its detailed view
- Use the edit button on the detail page to update the investment name
- After creating a new investment, a browser notification will appear 2 minutes later confirming it was recorded

### The Core Team


<span><i>Made at <a href='https://qwasar.io'>Qwasar SV -- Software Engineering School</a></i></span>
<span><img alt='Qwasar SV -- Software Engineering School's Logo' src='https://storage.googleapis.com/qwasar-public/qwasar-logo_50x50.png' width='20px' /></span>