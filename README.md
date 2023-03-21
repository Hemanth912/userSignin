# userSignin
This is a sprong boot application explaining how you would go about setting up a

User Registration API, where each user is assigned a Role.
User Authentication, where valid users are retuned a JWT Token.
Role-based access to specific API targets by means of providing a valid JWT Token.

JSON Web Token (JWT)
JSON Web Token (JWT) is an open standard that defines a compact and self-contained way for securely transmitting information between parties as a JSON object. This information can be verified and trusted because it is digitally signed. JWTs can be signed using a secret or a public/private key pair using RSA or ECDSA.

We will generate a JWT Token on the server as soon as the user is able to verify their credentials. When a user wants to hit an API, we will append the token to the request’s header. This will not only inform the server of who is trying to make the request but also the role that will help the API server determine if that person has access to the API or not.

For each API, we will assign which roles are able to access them. If the user’s role matches the roles allowed by that API, the request goes through. Else, we return a 403 Forbidden Request.

Authorization
To set up Authorization, we again need to provide the configuration by overriding the configure method, where we are passed a reference to the default HttpSecurity configuration.

Here we are configuring such that we will require authentication for all requests, with the exception of /users/register & /users/authenticate (We require those two endpoints to be available to all users to sign-up or login).

For the graceful handling of Unauthorized requests, we pass along a class that implements AuthenticationEntryPoint. We will return a 401 Unauthorized when we encounter an exception.

Because we are using JWT to store roles, we need to translate that into something that Spring Security can understand. The JWT Token needs to be parsed to fetch roles that the SpringSecurityContext needs to become aware of before it goes on to check if the API’s permissions will allow it. Hence we pass along the JwtAuthenticationFilter (Which we will come to in a later step).

Service Layer
Model
Let us first define what a User is. For the purposes of this article, I am defining a user to have

Username (Unique)
Password
Name
Business Title
Roles
A single user can have multiple roles. We define Roles to have

Name
Description
So let's create 2 classes under the model subdirectory, User, and Role. As mentioned earlier, we will be storing all this information in a MySQL Database and so User & Role classes can be written as Entity classes. What that really means is that we will be annotating the member variables as appropriate.

There exists a Many-to-Many relationship between User and Roles, meaning that each user can assume multiple roles and each role can be assumed by many users. So we will annotate that accordingly.

Repository
To perform read and writes on our database, we will create the UserDao and RoleDao repositories (annotated with @). These are interfaces that extend CrudRepository<?,?> where the 1st parameter represents the object model, in this case, User and Role. In each case, the 2nd parameter should be datatype of the unique id of each user or role object.

Service
We can now get to the step of writing the services. The services will ultimately use the service methods in our controller, so ultimately, this is where our core business logic might live.

Controller
Finally! The last steps!

The first thing the user needs to do is to register. The bare minimum that we need to provide is a username and password. simply calling the service method to save the user does the trick.

In order to access the APIs, we need to pass along a server-generated JWT Token. We have done all the groundwork for that in our TokenProvider. We use the generateTokenMethod and pass along the response.

We first need to create the tables in our DB populate them where needed.

Create another file called query.sql file alongside our .properties file and configure the data source to point to our “springsecurity” database.

The first step is to create the tables. Add the following statements. Note, you can only have DDL execute here.

output form using Postman(POSTMAN QUERIES)
Register as User:
"request": {
						"method": "POST",
						"header": [],
						"body": {
							"mode": "raw",
							"raw": "{\n    \"username\": \"user1\",\n    \"email\": \"user1@random.edu\",\n    \"password\": \"abc123\",\n    \"name\": \"John Doe\",\n    \"phone\": \"1234567789\",\n    \"businessTitle\": \"Student\" \n}",
							"options": {
								"raw": {
									"language": "json"
								}
							}
						},
						"url": {
							"raw": "http://{{host}}:{{port}}/users/register",
							"protocol": "http",
							"host": [
								"{{host}}"
							],
							"port": "{{port}}",
							"path": [
								"users",
								"register"
							]
						}
					},
					"response": []
				}
        
        
       
       Authenticate as User (Fetch Token)
      
      "request": {
						"method": "POST",
						"header": [],
						"body": {
							"mode": "raw",
							"raw": "{\n\t\"username\": \"user1\",\n\t\"password\": \"abc123\"\n}",
							"options": {
								"raw": {
									"language": "json"
								}
							}
						},
						"url": {
							"raw": "http://{{host}}:{{port}}/users/authenticate",
							"protocol": "http",
							"host": [
								"{{host}}"
							],
							"port": "{{port}}",
							"path": [
								"users",
								"authenticate"
							]
						}
					},
					"response": []
				}
        
        User Ping as User:
        "request": {
						"auth": {
							"type": "noauth"
						},
						"method": "GET",
						"header": [
							{
								"key": "Authorization",
								"value": "Bearer {{token}}",
								"type": "text"
							}
						],
						"url": {
							"raw": "http://{{host}}:{{port}}/users/userping",
							"protocol": "http",
							"host": [
								"{{host}}"
							],
							"port": "{{port}}",
							"path": [
								"users",
								"userping"
							]
						}
					},
					"response": []
				}
        
        Admin Ping as User:
        "request": {
						"auth": {
							"type": "noauth"
						},
						"method": "GET",
						"header": [
							{
								"key": "Authorization",
								"value": "Bearer {{userToken}}",
								"type": "text"
							}
						],
						"url": {
							"raw": "http://{{host}}:{{port}}/users/adminping",
							"protocol": "http",
							"host": [
								"{{host}}"
							],
							"port": "{{port}}",
							"path": [
								"users",
								"adminping"
							]
						}
					},
					"response": []
				}
        
        Register as Admin:
        "request": {
						"method": "POST",
						"header": [],
						"body": {
							"mode": "raw",
							"raw": "{\n    \"username\": \"user2\",\n    \"email\": \"user2@admin.edu\",\n    \"password\": \"abc123\",\n    \"name\": \"Jane Doe\",\n    \"phone\": \"9876543321\",\n    \"businessTitle\": \"Admin\" \n}",
							"options": {
								"raw": {
									"language": "json"
								}
							}
						},
						"url": {
							"raw": "http://{{host}}:{{port}}/users/register",
							"protocol": "http",
							"host": [
								"{{host}}"
							],
							"port": "{{port}}",
							"path": [
								"users",
								"register"
							]
						}
					},
					"response": []
				}
        
        Authenticate as Admin (Fetch Token):
        "request": {
						"method": "POST",
						"header": [],
						"body": {
							"mode": "raw",
							"raw": "{\n\t\"username\": \"user2\",\n\t\"password\": \"abc123\"\n}",
							"options": {
								"raw": {
									"language": "json"
								}
							}
						},
						"url": {
							"raw": "http://{{host}}:{{port}}/users/authenticate",
							"protocol": "http",
							"host": [
								"{{host}}"
							],
							"port": "{{port}}",
							"path": [
								"users",
								"authenticate"
							]
						}
					},
					"response": []
				}
        
        User Ping as Admin:
					"name": "User Ping as Admin",
					"request": {
						"auth": {
							"type": "noauth"
						},
						"method": "GET",
						"header": [
							{
								"key": "Authorization",
								"value": "Bearer {{adminToken}}",
								"type": "text"
							}
						],
						"url": {
							"raw": "http://{{host}}:{{port}}/users/userping",
							"protocol": "http",
							"host": [
								"{{host}}"
							],
							"port": "{{port}}",
							"path": [
								"users",
								"userping"
							]
						}
					},
					"response": []
				},
        
        Admin Ping as Admin:
				{
					"name": "Admin Ping as Admin",
					"request": {
						"auth": {
							"type": "noauth"
						},
						"method": "GET",
						"header": [
							{
								"key": "Authorization",
								"value": "Bearer {{adminToken}}",
								"type": "text"
							}
						],
						"url": {
							"raw": "http://{{host}}:{{port}}/users/adminping",
							"protocol": "http",
							"host": [
								"{{host}}"
							],
							"port": "{{port}}",
							"path": [
								"users",
								"adminping"
							]
						}
					},
					"response": []
				}
			]
        
