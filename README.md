# spring-jwt-authorization
Use JSON Web Token for authorizing clients to web server (Session Management)

Authoriztion strategies
In each interaction on dynamic web application, the request has authentication token
Website doesn't ask for authentication on each page (L1/P1 and L2/P2). 

Web application uses 2 tokens
Session token
JWT

## Session token

Server creates the session and keeps track of sessions using session id
Client passes session id for each interaction with the server
Server keeps track of session ids
Common approach is to add session id to the cookie in the header

### How is session id maintained?

Monolothic web app can have a session id server/cache on itself
Modern web appps are containerized or hosted on multiple servers with a Load balancer in front of them. Shared session cache is the solution (redis) so that all web servers can point to the distributed cache for session id verification on each request

### Alternate solution for redis

Sticky session... load balancer remembers the session (session stickiness) and forwards subsequent requests to the server that assigned the sesssion token initially

## JWT

Server creates JSON signed token and hands off to client. Server doesn't save the token anywhere. The token is a JSON object
Client sends it back to Server in the header as "Authorization: Bearer JWTToken"
Server verifies the signature first and then the client autherization details
JWT can also be saved in cookiee or browser cache

### JWT Format

There are 3 parts to JWT, separated by . (period)
* Header  (Probably base 64 encoded content that has algorithm used to verify signature and token type)
* Payload  (Probably base 64 encoded content that has JWT content)
* Signature (Only server can verify this, has the secret used to create the signature. Secret is saved on the server)

**Server computes the signature using the secret (stored somewhere) and the header,payload passed in JWT as the header in client request, compares the signature with that of JWT in client request. If mataches, successfull authorization!**

Go to debugger section on [JWT](https://jwt.io) website to decode, verify and generate JWT

