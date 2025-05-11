
# Initial notes Sep 2024
- Please use Java 11 for this project
- It also uses gradle e.g.
  - cfsjava11_arm64
  - ./gradlew clean test

# Kalah Project
### By Connor Sadler (connor.sadler@gmail.com), March 2021

Here's my solution for a KALAH job assignment.  
It's a Spring boot project which implements a Web Service to play 6-stone Kalah.  

## Running the Application
You can run from your IDE by running this class:
  
    CFSKalahApplication 

By default this will start the server on localhost:8080.  
You can then make HTTP requests as detailed in the assignment to create and play a game.  
The data is stored in a H2 database, so will be lost when the server is stopped. This would use Postgres or similar in a real deployment.


## Project Details

The project is structured as follows:
- A Spring boot project
- It uses Gradle as the build system
- Uses application.yml to store configuration properties (none needed at present)

The project includes the following main parts:
- A Controller layer/class
- Service layer which includes business logic and interacts with the database using the repository layer
- Repository layer using Spring Data to talk to JPA, Hibernate, and the H2 Database
- A further layer (GameLogicService) which contains the actual move logic - this makes use of GameState which includes logic about the game pits.


There is testing included at each layer: 
- Controller testing
- Service testing
- Additional testing for GameLogicService and GameState
- a final "end to end" integration test which runs through a scenario for the entire flow
  
All the testing is written in Groovy as I'm used to that, and it works pretty well for putting together tests.  
I've also used Mockito for testing some layers.    
Groovy also allows us to use the Spock framework to test the service layer, which is pretty nice once you're used to the "interactions" syntax.


## Still left TODO

OK it's not quite finished, I ran out of time.  
Here's the list of things still left TODO:

1. Finish the end to end test so it can finish a game
2. Additional tests as marked with TODOs (or cfstodo items)
3. Some tidying may be required
4. Maybe add a GET endpoint
5. Maybe add to the makeMove response so you can see if the game is over

