# About
Money Transfers is a demo application for money transfers between internal user accounts. It stores accounts in memory
and exposes RESTful API that allows following operations:
* Create a new account
* Delete an existing account
* Retrieve an account
* Transfer money between two accounts

The application was written in Scala using Akka, Akka HTTP.

# How to Run
```
sbt run
```
The server starts at [http://localhost:8080/](http://localhost:8080/)

# REST API
See [API documentation](API.md).

# See Also
* [money-transfers](https://github.com/aakatov/money-transfers) - Java implementation using concurrency synchronization and Jersey.