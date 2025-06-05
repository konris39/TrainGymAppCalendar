# Welcome to TrainGymAppCalendar 🫡

TrainGymAppCalendar helps you keep track of your workouts, progress, and communicate with your trainer.  
Create and edit training sessions, see your stats, and stay motivated!

---

## Screenshots

**Login page:**  
Simple login form to access your account.  
![Login](https://github.com/user-attachments/assets/19e1ffff-fcc1-453c-ba71-5d67b8da2bcc)

**Main page:**  
Dashboard to access all core features.  
![MainPage](https://github.com/user-attachments/assets/0b42b2eb-fcd6-4dd5-9f80-dd0908f05eed)

**Add a new training:**  
Easily log new workout sessions.  
![AddEvent](https://github.com/user-attachments/assets/137e45a3-7173-4fbb-a942-0e683b0c9641)

**Training calendar:**  
Visual overview of your upcoming and past trainings.  
![calendar](https://github.com/user-attachments/assets/dab99431-0fa6-46c8-a7f3-67115dd8721a)

**Your workouts:**  
List of your past training sessions.  
![yourWorkouts](https://github.com/user-attachments/assets/86c0f16d-75fd-4bcb-884b-69d8b2d041cd)

**1RM Calculator:**  
Estimate your one-rep max for different exercises.  
![1rmcalc](https://github.com/user-attachments/assets/76f44a27-6fbd-4029-a189-d2e80f0c3a2b)

**Profile page:**  
Add info about yourself and see your powerlifting score and BMI.  
![profilPage](https://github.com/user-attachments/assets/03aad058-43dd-4de4-ad93-5ac3033bf2b1)

**Edit training:**  
Update your previous sessions.  
![editEvent](https://github.com/user-attachments/assets/e79d61e2-e914-48e1-9a38-4b7d68abbc28)

---

## Architecture Overview

- **Frontend:** React 18+, TypeScript, Material UI  
- **Backend:** Spring Boot 3 (Java 21+), REST API, WebSocket support  
- **Database:** PostgreSQL  
- **Docker:** Ready to run the full stack in containers

---
## First clone the repo:

```bash
git clone https://github.com/konris39/TrainGymAppCalendar.git
```

## Frontend Setup

Requirements:  
- Node.js 18+  
- npm or pnpm

```bash
cd train-gym-app-calendar-frontend

npm install
npm start
```

Frontend will be available at http://localhost:3000.

## Backend Setup

Requirements:  
- Java 21+  
- Maven  
- PostgreSQL

1. **Create a PostgreSQL database:**  
   For example, `traingymapp`.

2. **Set up database credentials** in `application.yml`:
    ```yaml
    spring:
      datasource:
        url: jdbc:postgresql://localhost:5432/traingymapp
        username: your_db_user
        password: your_db_password
    ```

3. **Build and run backend:**
    ```bash
    cd TrainGymAppCalendarBackend

    mvn clean install
    mvn spring-boot:run
    ```

Backend will be running at [http://localhost:8080](http://localhost:8080).

---

## Run With Docker 🐳 (Optional and recommended for simplicity)

You can run the whole app stack with Docker:

If running for the first time:
```bash
docker compose up --build
```
if not first time then:
```bash
docker compose up -d
```

## ERD Diagram

Below is the ERD Diagram illustrating the application's database structure:

![ERD](https://github.com/user-attachments/assets/0ad9a6e3-063e-475a-8e17-393cca6bcfd3)

**Description:**

- **users** – Stores user data such as email, username, password, admin status, and trainer status.
- **training** – Contains information about trainings (date, name, description, acceptance and completion status, user associations).
- **data_user** – Stores users' physical data (age, height, weight, strength results, BMI, totals).
- **recommended_trainings** – Catalog of recommended trainings with description and type.
- **user_recommended_trainings** – Connects users with recommended trainings (many-to-many relationship).
- **user_group** – Represents group membership managed by trainers (many-to-many relationship via trainer_id and user_id).

The relationships are defined via foreign keys and join tables where many-to-many relations are needed.

---

## Tests

The backend is covered by a comprehensive set of unit and integration tests. 
Tests are written using **JUnit 5** and **Spring Boot Test**, covering business logic and various cases.

To run all tests locally, use:

```bash
./mvnw test
```

or

```bash
mvn clean test
```

## Technologies Used

- **React 18 + TypeScript** – Modern framework, great ecosystem.
- **Material UI** – Responsive, ready to use React UI components.
- **Spring Boot 3 (Java 21+)** – Fast, secure backend with REST API, security, WebSocket & Kafka integration.
- **PostgreSQL** – Relational, transactional database, ideal for structured data.
- **Docker** – Simplifies running the entire app.
- **WebSocket & Kafka** – Real time notifications.

