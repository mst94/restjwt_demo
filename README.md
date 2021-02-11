# Sample REST Webservice with JWT Authentication

## Purpose of this projekt
This is a sample REST webservice implemented with Spring Boot. It enables authentication via Jason-Webtoken (JWT). By having a valid JWT in your Basic-Authentication-Header, you can perform manipulation on the users and their roles.

## Run service
Before starting this service, be sure you have a running MySQL database with the corresponding "user" and "role" tables in the "jwtdemo"-database. 
With ``mvn clean install`` you can start the service. 

## Usage
### Authenticate via credentials
Call http://localhost:8084/login to authenticate. 
The body of your request should look like following:

``{"username": "yourusername",
"password": "yourpassword"}``

The result will be your JWT-Token which has to be used for the next steps. Copy your token. The token will expire after a predefined time (default 60min).

### CRUD operations 
Add your generated JWT token in your Authentication header like this way: Basic \<yourjwtoken>.

#### Example user object

    {"id":1,"login": "yourusername","password": "yourpw","fname": "testfname","lname": "testlname","email": "test@test.com","roles": ["ROLE_PURCHASE_WRITE","ROLE_ADMIN"]}

The user id will be created automatically by the database and **must not be stated** when creating user!

| Aim | Method | Path | Requ. Body | Answer Body | HttpStatus (success)
|--|--|--|--|--|--|
| CREATE user | POST | /user | user json object |  | 201
| READ user | GET | /user/{id} |  | user json object |200
| UPDATE user | PUT | /user/{id} | user json object |  | 200
| DELETE user | DELETE | /user/{id} |  |  | 204
