## To successfully run the BookSwap Spring Boot application on your local machine, please ensure you have the following prerequisites and configurations in place:

## 1. Software Prerequisites
- Java Development Kit (JDK) 21 or newer: Ensure you have a compatible JDK installed.
- Maven 3.9.+: The project is built with Maven, so you'll need it to compile and run.
- PostgreSQL: A running instance of a PostgreSQL database server.

## 2. Database Setup
   - Create a PostgreSQL database

## 3. Project Configuration
- Create a .env file in the project's root directory 
  - SPRING_R2DBC_URL= 
  - SPRING_R2DBC_USERNAME=
  - SPRING_R2DBC_PASSWORD=

## 4. Running the Application
- Load Environment Variables:
  - If you're on Windows, use the provided PowerShell script from your project's root directory: 
    - `./load-end.ps1`
  - For other operating systems or IDEs, ensure your environment variables are correctly loaded before starting the application.
  
## 5. Start the server
- From your project's root directory in the terminal, run: 
  - `mvn springboot:run`

With these items in place, your BookSwap application should launch successfully on port 8081, with the dev profile active.