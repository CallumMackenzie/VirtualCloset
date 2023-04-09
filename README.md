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

# Instructions for Grader
- You can generate the first required action related to adding Xs to a Y by typing a unique account name in the account list view and pressing create account.
- You can generate the second required action related to adding Xs to a Y by selecting an account, clicking "Delete Account", and clicking confirm in the popup window.
- You can locate my visual component by completing the first required action related to adding Xs to a Y then selecting an account, clicking "Open Selected Account", typing a unique name into the "Closet Name" field, pressing "Create New Closet", selecting the new closet, clicking "Open Closet", clicking "Create Clothing", typing "pants" into "Types", clicking "Add Types", clicking "Save & Exit", clicking "Confirm" in the popup window, typing "type = pants;" into the "Search Expression" box, and pressing enter. There will be a small image of pants in the list view component.
- You can save the state of my application by clicking "Save" on the accounts view, then clicking confirm in the popup window.
- You can reload the state of my application by clicking "Load" on the accounts view, then clicking confirm in the popup window.

# Phase 4: Task 2
Below is a sample of an event output when the program is closed:

> Closet: Added clothing [pants] {
> <br/> brand: adidas
> <br/> size: XL
> <br/> material: silk
> <br/> colors: orange, yellow, red, blue, periwinkle
> <br/> styles: casual
> <br/> dirty: false
> <br/> }.
> <br/> Sat Apr 08 16:50:29 PDT 2023
> <br/> Closet: Added clothing [shorts] {
> <br/> brand: gymshark
> <br/> size: XL
> <br/> material: cotton
> <br/> colors: dark gray
> <br/> styles: workout
> <br/> dirty: false
> <br/> }.
> <br/> Sat Apr 08 16:50:29 PDT 2023
> <br/> Closet: Added clothing [pants, cargos] {
> <br/> brand: old navy
> <br/> size: L
> <br/> material: cargos
> <br/> colors: beige
> <br/> styles: slim, casual
> <br/> dirty: false
> <br/> }.
> <br/> Sat Apr 08 16:50:29 PDT 2023
> <br/> Closet: Added clothing [sweater] {
> <br/> brand: ubc
> <br/> size: XL
> <br/> material: cotton
> <br/> colors: blue, gold
> <br/> styles: crewneck
> <br/> dirty: false
> <br/> }.
> <br/> Sat Apr 08 16:50:29 PDT 2023
> <br/> Closet: Added clothing [pants] {
> <br/> brand: under armor
> <br/> size: XL
> <br/> material: cotton
> <br/> colors: gray
> <br/> styles: casual, sweatpants
> <br/> dirty: false
> <br/> }.
> <br/> Sat Apr 08 16:50:29 PDT 2023
> <br/> Closet: Added clothing [shirt] {
> <br/> brand: youngla
> <br/> size: L
> <br/> material: cotton
> <br/> colors: green, black
> <br/> styles: gym, casual, long sleeve
> <br/> dirty: false
> <br/> }.
> <br/> Sat Apr 08 16:50:29 PDT 2023
> <br/> Closet: Added clothing [sweater] {
> <br/> brand: youngla
> <br/> size: XL
> <br/> material: sherpa
> <br/> colors: black
> <br/> styles: casual, sherpa
> <br/> dirty: false
> <br/> }.
> <br/> Sat Apr 08 16:50:29 PDT 2023
> <br/> AccountManager: Loaded state.
> <br/> Sat Apr 08 16:50:31 PDT 2023
> <br/> AccountManager: Set active Account to jake.
> <br/> Sat Apr 08 16:50:37 PDT 2023
> <br/> Catalogue: Added Outfit casual.
> <br/> Sat Apr 08 16:50:48 PDT 2023
> <br/> AccountManager: Set active Account to jonah.
> <br/> Sat Apr 08 16:50:54 PDT 2023
> <br/> Catalogue: Added Outfit new outfit.
> <br/> Sat Apr 08 16:50:57 PDT 2023
> <br/> Closet: Searching closet by address.
> <br/> Sat Apr 08 16:50:57 PDT 2023
> <br/> Closet: Returning search matches.
> <br/> Sat Apr 08 16:50:59 PDT 2023
> <br/> Outfit: Adding clothing @ 2023-04-08T23:50:59.884397400Z.
> <br/> Sat Apr 08 16:51:00 PDT 2023
> <br/> Outfit: Adding clothing @ 2023-04-08T23:51:00.857752200Z.
> <br/> Sat Apr 08 16:51:01 PDT 2023
> <br/> Outfit: Adding clothing @ 2023-04-08T23:51:01.939167400Z.
> <br/> Sat Apr 08 16:51:12 PDT 2023
> <br/> Closet: Searching closet by address.
> <br/> Sat Apr 08 16:51:12 PDT 2023
> <br/> Closet: Returning search matches.

# Phase 4: Task 3
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