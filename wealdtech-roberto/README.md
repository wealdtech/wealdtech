#Roberto

##Introduction

Roberto simplifies communications with network-based servers and access to the resultant information.  It is built on top of [Retrofit](http://square.github.io/retrofit "Retrofit") and designed to provide advanced features such as conditional actions

##Main Features

###User-defined data providers
Each data provider in Roberto is defined by the user by extending core classes.  This makes each data provider clear and application-specific.

###Priority-based Queues
Roberto allows you to set up multiple priority-based queues, ensuring that low-priority traffic does not interrupt higher-priority traffic.
 
###Transient and Persistent Queues
Queues can be defined as either transient or persistent.  If persistent then once an activity is submitted

###Cancellable Actions and Queues
Every action and queue can be cancelled if required, reducing unnecessary network and CPU load.

##Architecture
This outlines the main components of Roberto's architecture.

###Data Providers
A data provider represents the information being provided.
