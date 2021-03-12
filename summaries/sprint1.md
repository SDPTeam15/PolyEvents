# Summary for week 1

## Alessio

I implemented the sign-in with Google system using FirebaseAuth. Two displayable fragment were created to handle login as well as displaying the basic user information.
 
My time estimates were good only for implementing the Sign-In Authentication, i.e. the estimates didn't take into account the time needed to implement the tests. 

Next time, I'll probably take smaller tasks if they require to read a lot of documentation. 

## Cyrille

I implemented a class to describe an Activity (in the event sense, will need to rename this). Also, I designed a small layout to display a summary of an activity as a "card" and add some stubs activities to the home page.

My time estimate was for the activity class was somewhat good (a bit too short) but my estimate for the UI activity card was too short. It is mainly because of some problems to test the UI for the upcoming activities on the homepage.

Next time, I will split the functionalities in more branches so that the PR can be easily reviewed and merged earlier during the week so that others can use my code (or at least its interface) if needed.

## Florian (Scrum Master)

I implemented a RecyclerView and its adapter to show a scrollable list of activities under the list section. I also implemented an (android) Activity showing the activity information.

The estimated time for the task was not enough. I did not take the tests part into account and spent a lot of time searching for documentation and solutions to test my code.

Next time, I'll manage my time to take tests into account.

## Mathieu

I implemented the UI for the mainActivity. It is composed of a Navigation bar used to switch the active fragment and a search bar which will be implemented in the future.

The estimated time for the task was way to short. I spent the estimated time spent to search how to implement a navigation bar and the search button and then about the same time to implement and test.

Next time, I will take into account the search time, even if it may be a lot easier than I think it is. So that I have time to take another task or review more code from pull request.

## Robin

I implemented an adapter from the Firebase class by Google. This adapter will be used to support the concept of user in the rest of the application, for example for permissions.

The estimated time was too low. I didn't predict that there will be many issues with the tests and the documentation.

Next time, I'll break the Product Backlog in smaller task and smaller branches.

## Stephane
I explored backend options using Firebase. I decided to go with the Google Cloud Firestore for storing our data and retrieve it. I read corresponding documentation, played with a couple of demo apps to see how everything works and familiarized with the NoSQL model. I also established an initial Entity-relation diagram for our app with Alessio.

There was no hands-on code involved except for the demo apps. Reading the documentation took much more time than expected, particularly for trying to adapt to the NoSQL model. 

Next week, I will try to get more into the code and implement an initial functional backend. If I have spare time, I'll maybe expose an interface for the others to work with the database if the need arised. 

## Overall team

We implemented all the tasks assigned this week from our sprint backlog and did their corresponding tests. We also took some time to read the documentation about firebase to prepare the database for incoming weeks.

Our time estimates were bad in general and we took much more time than expected to do our tasks. This was mainly due to test time underestimates and documentation reading. We'll try to take this into account for next week.

We did a meeting on tuesday and most of us already finished our tasks. Alessio, Cyrille and Florian had a hard time finding how to test the Fragments but we finally found a good solution to do it.
