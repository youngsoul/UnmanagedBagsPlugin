UnmanagedBagsPlugin
===================

see:  https://github.com/youngsoul/UnmanagedBagsTestDriver  for a test application project

Plugin that will add dynamic methods to a Domain object to maintain a lightweight, non-hibernate/gorm managed
bag collection of things.

The inspiration for this plugin, was a talk from Burt Beckwith on the inefficiencies of a 1-to-a lot of many
relation.  Hibernate has to do a lot of work to maintain the proper Object semantics.  This is true for a List
collection, a Set collection and even for a Hibernate Bag collection.

Burt suggested a pattern where instead of using the traditional hasMany/belongsTo in grails - you would instead
just a reference to the parent in the child object and manage the relationship that way.  There are restrictions
and caveats but depending upon your usage, it might be well worth it.

We started to incorporate that pattern where it made sense, and I noticed a lot of duplicated, nearly identical code,
so in an effort to stay as DRY as possible, I created this plugin to add the dynamic methods.

The plugin adds an additional static identifier for a Domain, for example:

    static hasUnmanagedBags = [cars: Car]

The plugin in will add the following dynamic methods for the definition above:

removeAllCars()

* Disassociate all car instances from the parent object, but do not delete the car instances.  If there is a need
to get a reference to the collection of cars, then it is expected the user called getCars() prior to this.  Otherwise
there is no way to get the cars through the parent object.

removeFromCars(aCar)

* Disassociate the car instance from the parent, but it will NOT delete the car instance.

getCars()

* Get the collection of associated car instances


setCars(List<Car>)

* Will remove any existing cars associated with the parent object, save the cars in the new list and associate with the parent object

addToCars(aCar)

* associate the new car with the parent object adding to the collection of all cars.

addAllToCars(List<Car>)

* add the collect of Cars to the existing collection of cars

deleteAllCars()

* delete the cars associated with the parent object.  This is a destructive call on the Car collection objects.

List<Parent> findAllByCarInList(List<Car>)    This is a static method of the Parent class

* find all of the Parent objects that are associated with the cars in the collection.  To use a single Car just
pass a single object in the collection such as:

Parent.findAllByCarInList([car1])

Will find the Parent object that is related to car1.


There are two ways to get this behavior.  One is use to the static hasUnmanagedBag and the other is an Annotation.

As a side note, at some point when I better understand ASTs, would like to create an AST annotation but that will be
the next step.

There are some restrictions and caveats with this approach, which you need to know to use it correctly.

1) It assumes that the parent object has a database id assigned to it.  Therefore it must be saved first

2) There are no cascades from the parent to the children

3) This is a 1-to-many relationship

4) It is a bag, so there is no implied order built into the collection.

Usage:

    static hasUnmanagedBags = [cars: Car]

Implies that the parent object has an 'id' property

Implies the Car object has an 'ownerId' property

The default values of 'id' and 'ownerId' can be changed as shown below.


    static hasUnmanagedBags = [animals:[parentFKPropertyName: "ownerFkId", childClass: Pet, parentKeyPropertyName="someId"] ]


Allows to change some of the default values.

Optional: parentFKPropertyName specifies the property in the child object used to hold the parents id value.

Optional: parentKeyPropertyName specifies the property in the parent object that holds its primary key

Required: childClass property holds the class of the child.


Annotations:
Annotations can also be used instead of, or in addition to, the hasUnmanagedBags.  At present the Annotation is not
an AST but will also add the methods dynamically.

    @UnmanagedBags(all=[
        @UnmanagedBag(parentKeyPropertyName="id", collectionPropertyName="myKids", parentFKPropertyName = "ownerId", childClass = Child),
        @UnmanagedBag(collectionPropertyName="myHobbies", childClass = Hobby)
    ])



