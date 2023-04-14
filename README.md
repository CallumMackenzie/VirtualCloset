# Virtual Closet

## About

Virtual closet is a Java desktop application with the purpose of *keeping track of items of clothing* in your closet(s),
allowing you to *combine them into outfits* which can be *saved for later viewing and reference*.

**What is Virtual Closet useful for?**

- Individuals interested in fashion
- Keeping track of clothing
- Remembering what clothing you have in your closet(s)
- Recording outfits you've liked in the past
- Testing combinations of clothing you have

## Motivation for the Idea

Often times I rush in the morning to throw together a suitable and tasteful fit, but there are many articles that slip
my mind and sit unused. This is the primary reason I am interested in creating this application; to make choosing an
outfit
a quicker and easier process. It takes time to put on clothing combinations to see if they fit together, which is time
that
can be saved using an application like Virtual Closet.

## User Stories

As a user, I want to be able to ...

- create & remove accounts
- create & remove closets from accounts
- add & remove clothing from my closets
- search through closets
- create & remove outfits out of clothing
- modify outfits
- search my catalogue
- save my data automatically and if I choose
- load my data automatically and if I choose

# Refactoring Reflection
If I were to redo my design, there are a couple of main things I would refactor.
The first is the View class and respective subclasses of the swing UI, and the second
is the way I implemented my classes with named accounts.

Notice in the UML diagram located in the project root that all subclasses of View
contain a reference to an AccountManager with multiplicity 1; this could be refactored
into the common superclass View, since this instance persists across the application
lifecycle. In fact, since there is only ever one AccountManager, it could
also be refactored to be a singleton class, removing the need for any classes to 
track a reference to it. This would reduce the amount of code required overall.

I would also refactor classes with relationships such as in the AccountManager and Account where 
there are objects which must have a unique name within a container class. My implementation of these
used a list to store the collected objects, and when the container was requested to add
a new one, these objects would be searched in a linear fashion to see if there was another
object with the same name. If there was, the new one could not be added, otherwise it was
added as it was unique. The time efficiency of this could be improved by changing the implementation
to map an object to its name in a HashMap, but at the same time this would reduce space efficiency. 
Yet for the application it is unlikely that there will be sufficient data to the point
there is a noticeable delay in these adding / removing procedures, and there will certainly
be enough memory which makes the HashMap space complexity issue irrelevant.