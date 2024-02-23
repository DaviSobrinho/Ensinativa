# Ensinativa

## Ensinativa - The idea

Hi, I'm Davi and I developed Ensinativa starting in January 2024. The application is a kind of helpdesk app, but for Mobile. The idea to build it arose during my last semester of my college course in Systems Analysis and Development. Basically, I thought about the problem of having difficulty in solving some common technology problems and often not having a practical search tool, whether to solve a piece of code that AI struggles with, an architectural question, or even a problem with other areas besides programming, like configuring a router.
The solution was to try to create an application that would allow users to quickly expose a doubt so that others could identify it with a description and images quickly, unlike other platforms like forums or AIs.
So, I decided to start building the application with my previous knowledge, but it wasn't an easy task, and I needed to study and improve a lot to implement a first MVP. It was a great challenge and very rewarding to have built something on a larger scope than what I had done before, and something real that can be used and adds value to people's lives.
So without further ado, let's go to the descriptions and specifications of the application:

## Ensinativa - Specs

Application name: Ensinativa
Description: Education App
Objectives: Solve technology doubts quickly and through mobile devices. In the application, users can create an account and create tickets about technology problems, being able to add images to them. Once this ticket is created, it will appear for other users, and they can accept and start a chat with that person to solve their problem. The idea is to bring everything quickly and dinnamically.
Architecture: MVVM (M
Client side: Kotlin, XML, and HTML
Security: FirebaseAuth, AppCheck, Play Integrity 
Backend: All done by Firebase (FirebaseAuth, FirebaseRTDB, FirebaseStorage, AppCheck)
Testing: SonarLint and SonarCloud
Deployment: PlayStore
Kotlin Resources: Views, Activities, Dialogs, Adapters, Framents, ViewBinding, Tasks, Coroutines, Glide, ViewPager.

## Ensinativa - How it works

Basically, the application was built in Kotlin and can be downloaded by the user from the PlayStore (Currently awaiting validation of tests from GooglePlay). Once installed, the application runs on the client side and basically needs the Firebase to get nearly all data, whether for authentication, creating requests, conversations, and profile configuration. So it works like an MVVM, but the database is managed by Firebase's own Controller.
About the future: I'll look to implement a lot of new features to the app, like adding video calls in the chats or adding a functional rating system. Expectations are high, and I think this MVP is just the beginning. Of course, everything takes some time, especially alone, but I think it will be very beneficial.

## Ensinativa - Presentation Video

Below is the video of the application in operation and images of it. Thank you everyone for your attention and support.


## Implementation Diagram



## Class Diagram 

This diagram made using mermaid helped me when create the Ensinativa objects, it will be more powerfull when I complete the custom API, so there will be more available FR and NFR.

```mermaid
classDiagram
    User <.. Request : create
    User <-- Chat : uses
    Connection <-- Chat : starts
    Connection <.. Call : starts
    Request <.. Connection : starts
    Chat <-- Message : has
    User -- Rating : has
    class User{
      -userID: Int
      -username: String
      -firstName: String
      -lastName: String
      -email: String
      -password: String
      -birthday: String
      -rating: Double
      -status: String
      -availableForRequest: Boolean
      -achievments: ArrayList<Achievments>
      -skills: ArrayList<Skills>
      +keepUser() Void
      +login() Void
      +logout() Void
    }
    class Request{
      -requestID: Int
      -callerID: Int
      -receiverID: Int
      -description: String
      -dateTimeRequest: String
      -status: Int
      -skills: ArrayList<Skills>
      +keepRequest() Void
      +acceptRequest() Void
      +denyRequest() Void
    }
    class Rating{
      -ratingID: Int
      -rating: Int
      -ratedUserID: Int
      -raterUserID: Int
      -ratedRequestID: Int
      +createRating() Void
      +readRating() Void
    }
    class Chat{
      -chatID: Int;
      -messages: ArrayList<Message>
      -users: ArrayList<User>
      +createChat() Void
      +readChat() Void
    }
    class Message{
      -messageID: Int
      -content: String
      -senderID: Int
      -receiverID: Int
      +createMessage() Void
      +readMessage() Void
    }
    class Connection{
      -connectionID: Int
      -dateTimeConnectionStart: String
      -dateTimeConnectionEnd: String
      -status: String
      -callerID: Int
      -receiverID: Int
      +createConnection() Void
      +readConnection() Void
      +updateConnection() Void
      +startConnection() Void
      +pauseConnection() Void
      +endConnection() Void
    }
    class Call{
      -callID: Int
      -dateTimeCallStart: String
      -dateTimeCallEnd: String
      -callerID: Int
      -receiverID: Int
      +createCall() Void
      +updateCall() Void
      +readCall() Void
      +startCall() Void
      +endCall() Void
    }
```
If you have any question or problem, describe it and contact us in this email: davisobrinho82452@gmail.com
