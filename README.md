# CPIT305 db chat
Advanced programming using java, chat app that required clients to login using username and password. That login info should requested from database (sqlite database).

## Your Task
Complete TODOs:
1. Check user login from database [here](/src/server/ServerApp.java#L64)
1. Get user full name from database using query that search for username [here](/src/server/ServerApp.java#L59)
1. Server accept only one client at time to login, move this code to new thread so that server will serve all clients in the same time! [here](/src/server/ServerApp.java#L31-51)
