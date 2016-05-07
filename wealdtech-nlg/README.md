#NLG

NLG provides a framework for generating text.  It focuses on providing personalised text from a template based on parameters such as formality, context and target platform.

##Overview

A request to attend a meeting is a generic example with many different possibilities.  A formal request using email where the participants are unfamiliar with each other might look something like this:

    Dear Fred,
       I hope that this email finds you well.  I'm writing to invite you to a meeting with Joe Smith next Tuesday at 11am.
    The meeting will take place at Joe's office.  Please let me know if you can attend.  Thank you.

    Yours sincerely,
    Ellie Jones.

whereas an informal request using text where the participants are familiar to each other might look something like this:

    Hey are you around next Tues 11am for a meeting at Joe's?

However both are based on a similar template, which looks something like:

    [Greeting] [Pleasantry] [Invitation] [RSVP] [Sign off]
    
NLG takes the template and uses it to generate the required output.