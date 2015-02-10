---
category: Subscriptions
path: '/subscriptions/:topicid'
title: 'Unsubscribe from a topic'
type: 'DELETE'

layout: nil
---

Unsubscribe to no longer receive messages from a particular topic.

### Request Parameters
None.

## Path Parameters
* `:topicid` the ID of the topic from which to unsubscribe

## Body
None.

### Response

**If succeeds**, returns status code 200.

```Status: 200 Deleted```

For error responses, see the [response status codes documentation](#response-status-codes).
