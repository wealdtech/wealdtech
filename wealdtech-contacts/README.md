#Contacts

Contacts manages contact information.  Contacts consist of the contacts themselves and the relationships between them.

A Contact is the canonical information for a person, place or service.  It details all available methods by which they can be reached, from names to 'phone numbers to physical addresses, and the spheres to which the methods relate (personal/professional).

##Contacts

A Contact describes the canonical information for a contact.  This includes both past and current information.  The specific data contained by a contact is described below.

###Handles
A handle is a way of identifying a particular contact.  Examples of handles are names, nick names, email addresses and application-specific usernames.

Each handle can be configured with a particular lifetime (i.e. the range of time over which the handle is valid for the particular contact).  It can also be restricted to a subset of contexts.

It should be noted that handles cannot be used by themselves to uniquely identify a contact as it is possible for multiple contacts to use the same handle.

###Events
An event is a way of defining a calendar event in a contact's existence.  Examples of events are birth, marriage and start of a job.

Each event can be configured with a particular lifetime.  It can also be restricted to a subset of contexts.

##Relationships
A relationship describes the unidirectional relationship from one contact to another.  There can be multiple relationships between the same two contacts in different contexts (professional life, personal life, etc).

Each relationship can have local handles (for example a person might use the nickname handle 'Dad' for their father in personal life but if they work at the same large company they won't use it professionally).

###Spheres
A sphere is a constraint on where a handle is valid.  For example, a contact might have two cellphones where one is for their personal life and another for their professional life.  Equally they may well have different addresses for personal life (i.e. home) and professional life (i.e. work).

###Contexts
A context is a 

#Purpose

Provides the ability to create and manage contacts, along with the relationships beween them

Contacts have both official and unofficial names.

##Familiarity

Familiarity is a property of a handle that allows selection of contacts with the same handle in a given context.  For example if a user knows a "Chris Smith" and a "Chris Jones" then a request for "Chris" in any given context needs to be resolved to one or the other.  This is resolved by a system of positive feedback, where a mention of "Chris" linked to another handle that matches the contact increases familiarity for that context and further mentions of "Chris" increase the familiarity of the handle.  Familiarity decays over time, resulting in a forgetting of once commonly-used handles when they fall in to disuse.