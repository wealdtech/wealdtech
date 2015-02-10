---
title: 'Message Structure'

layout: nil
---

### Message

A message contains the following elements:

* [`topic`](#/data-types#text) the topic to which to post the message
* [`scope`](#/data-types#) the scope of the message.  This can be one of three options:
    * `Everyone` the message will be made available to everyone
    * `Friends` the message will be made available to your friends
    * `Individual` the message will be made available to a single other person
* [`to`](#/data-types#text) the recipient of the message.  This is required if `scope` was set to `Individual`, otherwise this is not required and will be ignored if provided
* [`text`](#/data-types#text) the text of the message.

In addition, when receiving a message the following additional elements will be present:

* [`from`](#/data-types#text) the name of the user who created the message
* [`timestamp`](#/data-types#timestamp) the time when the message was receieved by the chat server

### Examples
```{
    "topic": "myTopic",
    "scope": "Everyone",
    "text": "Hello world",
}```

```{
    "topic": "myTopic",
    "scope": "Individual",
    "to": "a0b124fd23",
    "text": "Hello Fred",
}```
