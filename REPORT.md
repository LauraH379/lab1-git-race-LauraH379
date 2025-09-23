# Lab 1 Git Race -- Project Report

## Description of Changes
- Personalized greeting based on the time of day:
    HelloController and HelloApiController were modified to generate a dynamic greeting based on the current time:
        "Buenos días" (Good morning) for 6–13 h
        "Buenas tardes" (Good afternoon) for 13–21 h
        "Buenas noches" (Good night) at any other time
    This logic was centralized in the getGreeting service function to ensure reuse across all controllers.

- Persistence of greetings in the database:
    An H2 persistent database was implemented using JPA, with the GreetingHistory entity storing:
        Username
        Greeting message
        Timestamp of creation
    The GreetingHistoryRepository was created with standard methods (save, findAll) and custom queries (findByUsername). Greetings are automatically saved when the user provides a name, both from the web interface and the API.

- Visualization of greeting statistics:
    A REST endpoint /api/stats and a web interface were implemented to display aggregated greeting statistics per user:
        Total number of greetings per user
        Count of each type of greeting ("Buenos días", "Buenas tardes", "Buenas noches")
    A button was added in the interface that, when clicked, fetches the data and updates an HTML table dynamically with the information.

- Unit, MVC, and integration tests:

    For the dynamic greeting functionality: tests verify correct messages based on the time and inclusion of the username in both web and API responses.

    For persistence: JPA unit tests verify creation, retrieval, and queries by username, and integration tests with MockMvc simulate the database to ensure endpoints return correct data.

    For statistics: tests verify that counts per user and greeting type are correct, including scenarios with multiple users and empty lists.

## Technical Decisions

Centralization of greeting logic in getGreeting for reuse and easier testing.

Use of LocalDateTime.now().hour to determine the time range and generate the appropriate message.

H2 file-based database (jdbc:h2:file:./data/testdb) for persistence between executions and enabling the web admin console (/h2-console).

JPA configuration with ddl-auto=update to automatically create and update tables.

REST endpoint /api/stats that groups records by user and counts each type of greeting.

Use of MockMvc, DataJpaTest, and repository mocks to ensure full test coverage without depending on a real database.

Web interface with dynamic table and button that fetches statistics using fetch to improve user experience.

## Learning Outcomes

Deep understanding of integrating time-dependent business logic with Spring Boot and Kotlin.

Experience creating and managing JPA entities, repositories, and custom queries for data persistence.

Development of REST endpoints that aggregate data and integrate with dynamic web interfaces using JavaScript.

Comprehensive unit, MVC, and integration testing combining business logic, persistence, and visualization.

Understanding the full workflow: user → controller → service → repository → database → API → web interface.

Improved skills in designing features that combine dynamic data and aggregated information for analysis.

## AI Disclosure
### AI Tools Used

- ChatGPT for code review, compilation error correction, adding comments, and initial guidance on implementing the new functionalities.

### AI-Assisted Work

- AI was used to research different implementation possibilities and receive suggestions on how to structure the functionality, as well as guidance on how to start the code.

- Approximately ~50% of the work was AI-assisted.

- All code generated or suggested by AI was personally adapted, modified, and completed to fit the project and ensure correct functionality.

### Original Work

Implementation of the getGreeting function and modification of web and API controllers to use it.

Integration of H2 database, creation of the GreetingHistory entity and GreetingHistoryRepository.

Development of endpoints and logic for greeting statistics (/api/stats) and their visualization in the web interface.

Development of unit and integration tests for business logic, persistence, and statistics.

Through this project, I learned a lot about how to build and test a full web application using Spring Boot, Kotlin, and JPA. Working on an existing project helped me understand how all the layers connect: the interface, business logic, and database.

Adding the time-based greeting functionality showed me how to handle dynamic data in the backend and how to centralize the logic in a service (getGreeting) so it could be used in both the web interface and the API.

With H2 persistence and JPA, I learned to create entities, save data with automatic timestamps, and write custom queries, seeing how records can be efficiently stored and retrieved.

The statistics feature gave me practice in grouping and displaying dynamic data in the interface using a REST endpoint and JavaScript to update the table.

Unit and integration tests helped me ensure everything worked correctly and gave me a better understanding of how to test different scenarios.

I also used AI to research ideas, explore different ways to implement features, and get guidance on how to start coding, but I personally adapted and wrote all the code so it worked properly in the project.

Overall, this project gave me solid practice with backend, frontend, databases, and testing, and helped me see how a complete web application flows from the user to the database and back.