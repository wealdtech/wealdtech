---
category: Subscriptions
path: '/subscriptions/:topicid'
title: 'Subscribe to a topic'
type: 'POST'

layout: nil
---

Subscribe to receive messages from a particular topic.

### Request Parameters
None.

## Path Parameters
* `:topicid` the ID of the topic to which to subscribe

## Body
None.

### Response

**If succeeds**, returns status code 201.

```Status: 201 Created```

For error responses, see the [response status codes documentation](#response-status-codes).
