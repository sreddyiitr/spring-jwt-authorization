# Use JSON Web Token for authorizing clients to web server (Session Management)


## Authoriztion strategies
In each interaction on dynamic web application, the request has authentication token. The website doesn't ask for authentication on each page (L1/P1 and L2/P2). Primarily, Web application uses 2 tokens
* [Session token](##Session-Token)
* [JWT](##JWT)

## Session token

* Server creates the session and keeps track of sessions using session id
* Client passes session id for each interaction with the server
* Server keeps track of session ids
* Common approach is to add session id to the cookie in the header

### How is the session id maintained?

* Monolothic web app can have a session id server/cache on itself
* Modern web appps are containerized or hosted on multiple servers with a Load balancer in front of them. Shared session cache is the solution (redis) so that all web servers can point to the distributed cache for session id verification on each request
* Alternate solution for redis: **Sticky Session** load balancer remembers the session (session stickiness) and forwards subsequent requests to the server that assigned the sesssion token initially

## JWT

* Server creates JSON signed token and hands off to client. Server doesn't save the token anywhere. The token is a JSON object
* Client sends it back to Server in the header as "Authorization: Bearer JWTToken"
* Server verifies the signature first and then the client autherization details
* JWT can also be saved in cookiee or browser cache

### JWT Format

There are 3 parts to JWT, separated by . (period)
* **Header**  *(Probably base 64 encoded content that has algorithm used to verify signature and token type)*
* **Payload**  (Probably base 64 encoded content that has JWT content)
* **Signature** (Only server can verify this, has the secret used to create the signature. Secret is saved on the server)

**Server computes the signature using the secret (stored somewhere) and the header,payload passed in JWT as the header in client request, compares the signature with that of JWT in client request. If mataches, successfull authorization!**

### Debug JWT
Go to debugger section on <a href="https://jwt.io">JWT</a> website to decode, verify and generate `JWT`

### Challenges
* Expiration of `JWT`
* Impersonification

## Steps involved in using JWT for authorization

* Create an API to accept userid and password and return JWT as response
* Create an API to accept JWT in the request and validate the token 
 

## Spring JPA based authentication
We'll use JPA for user authentication in this project. A user needs to be authenticated before we talk about session 
management.

* Spring out of the box doesn't provide an implementation for JPA based authentication
* Instead, we would leverage UserDetailsService implementation to have custom authentication using JPA
* UserDetailsService doesn't need to necessarily use JPA and can just read credentials from a file or have them 
hardcoded


##### Scripts to create users & authorities in PostgreSQL
```
CREATE TABLE USERS (
	USERNAME VARCHAR(36) NOT NULL,
	PASSWORD VARCHAR(36) NOT NULL,
	ENABLED BOOLEAN not null,
	constraint unique_uk_1 unique(USERNAME));

CREATE TABLE authorities (
	USERNAME VARCHAR(36) NOT NULL,
	authority VARCHAR(36) NOT NULL,
	constraint unique_uk_2 unique(USERNAME),
constraint foreign_fk_1 foreign key(USERNAME)references USERS(USERNAME));

SELECT * FROM USERS;
SELECT * FROM AUTHORITIES;

INSERT INTO USERS(USERNAME, PASSWORD, ENABLED) VALUES('user','user',true);
INSERT INTO USERS(USERNAME, PASSWORD, ENABLED) VALUES('manager','manager',true);
INSERT INTO USERS(USERNAME, PASSWORD, ENABLED) VALUES('admin','admin',true);

INSERT INTO AUTHORITIES(USERNAME, AUTHORITY) VALUES('user','ROLE_USER');
INSERT INTO AUTHORITIES(USERNAME, AUTHORITY) VALUES('manager','ROLE_MANAGER,ROLE_USER');
INSERT INTO AUTHORITIES(USERNAME, AUTHORITY) VALUES('admin','ROLE_ADMIN,ROLE_USER');

## How to validate JWT in subsequent requests to server and bypass session managment by the server?
Here are the steps involved

* Let http security know to do <a href="https://github.com/sreddyiitr/spring-jwt-authorization/blob/master/src/main/java/com/spring/security/jwt/authorization/SpringSecurityConfiguration.java#L66" target="_blank">stateless session management</a>
* Create a `filter` to intercept each request to the server and parse header to get `JWT`
* Validate `JWT` and extract `username` from the `JWT`
* Verify that `username` exists in the user repository
* Create `usernamePasswordAuthenticationToken` and push it to the spring security context because we are taking over 
the session validation
* Finally, Add this `filter` to the `filterchain` before `UsernamePasswordAuthenticationFilter` so that the token
is available using the `JWT` validation process and the token can be pushed to security context for use by 
`UsernamePasswordAuthenticationFilter`. Otherwise, it will ask for `username` and `password` again
