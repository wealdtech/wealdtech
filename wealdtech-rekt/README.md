#rekt

rekt uses information provided by a user to fill out internal data structures.  It allows for validation of 

##Example

A knowledge lookup system might receive a request "What is the population of the United Kingdom?"  To process this the system might want the data in a suitable format, for example (using JSON as the data display format):

    {
      "type": "request",
      "subject": "United Kingdom",
      "data": "Population",
      "date": "2016-05-01T00:00:00Z"
    }
    
Moving from the free-form text to the structured data is helped by NLP but there is no programmatic way of filling in the data structure, and importantly resolving confusion when some of the information present is missing or ambiguous.

##Rules
There are a number of rules around each piece of data that can be placed in the structure.  These are:

  * A piece of data can be required or optional
  * A piece of data can have a default if not supplied
  * A piece of data might be free form or follow a given structure
  
This leads on to the data in the structure being either missing, ambiguous, invalid or resolved, and the overall structure being valid or invalid.
