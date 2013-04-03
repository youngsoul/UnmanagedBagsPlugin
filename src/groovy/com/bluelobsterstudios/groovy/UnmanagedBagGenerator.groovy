package com.bluelobsterstudios.groovy

import grails.util.GrailsNameUtils
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsApplication

/**
 * Generator used to create the unmanaged Bag Methods
 *
 * User: youngsoul
 *
 * Methods Create:
 * The plugin adds an additional static identifier for a Domain, for example:
 *
 *  static hasUnmanagedBags = [cars: Car]
 *
 *  The plugin in will add the following dynamic methods for the definition above:
 *
 *  removeAllCars()
 *
 *  removeFromCars(aCar)
 *
 *  getCars()
 *
 *  setCars(List<Car>)
 *
 *  addToCars(aCar)
 *
 * addAllToCars(List<Car>)
 *
 *  deleteAllCars()
 *
 */
class UnmanagedBagGenerator {

    /**
     * optionally set this if you want the generated methods to validate that the objects being associated are
     * infact domain objects.  if this is not set, that check will be skipped.
     */
    static GrailsApplication grailsApplication


    /**
     * creates methods like addToMyKids if prefix='addTo' and propertyName='myKids'
     *
     * @param prefix
     * @param propertyName
     * @return
     */
    static String getPrefixedMethodName(String prefix, String propertyName) {
        return prefix+propertyName.substring(0,1).toUpperCase()+ propertyName.substring(1);
    }

    static void generateUnmanagedBagMethods(Class domainClass, String parentKeyPropertyName, String collectionPropertyName, String parentFKPropertyName, Class childClass) {
        generateUnmanagedBagMethods(new DefaultGrailsDomainClass(domainClass), parentKeyPropertyName, collectionPropertyName, parentFKPropertyName, childClass)
    }

    static void generateUnmanagedBagMethods(DefaultGrailsDomainClass domainClass, String parentKeyPropertyName, String collectionPropertyName, String parentFKPropertyName, Class childClass) {
        println "AnnotatedCollection (${parentKeyPropertyName},${collectionPropertyName},${parentFKPropertyName})"

        String removeAllMethodName = getPrefixedMethodName("removeAll", collectionPropertyName)
        String removeFromMethodName = getPrefixedMethodName("removeFrom", collectionPropertyName)
        String getterMethodName = GrailsNameUtils.getGetterName(collectionPropertyName)
        String setterMethodName = GrailsNameUtils.getSetterName(collectionPropertyName)
        String addToMethodName = getPrefixedMethodName("addTo", collectionPropertyName)
        String addAllToMethodName = getPrefixedMethodName("addAllTo", collectionPropertyName)
        String deleteAllMethodName = getPrefixedMethodName("deleteAll", collectionPropertyName)

        //-------------------------  get Method Setup  -------------------------------------------
        domainClass.metaClass."$getterMethodName" = {
            def findAllByMethodName = getPrefixedMethodName("findAllBy", parentFKPropertyName)
            def parentIdValue = delegate."${parentKeyPropertyName}"
            def allChildren = childClass."$findAllByMethodName"(parentIdValue)
            return allChildren
        }

        //-------------------------  set Method Setup  -------------------------------------------
        domainClass.metaClass."$setterMethodName" = { value ->

            if (value == null) {
                throw new RuntimeException("Value to add to collection cannot be null")
            }
            if (!value instanceof Collection) {
                throw new RuntimeException("Value must be a collection")
            }

            // disassociate the existing items
            delegate."${removeAllMethodName}"()

            // for each of the items in the collection add them as a child
            (value as Collection).each {
                if( it.class != childClass ) {
                    throw new RuntimeException("Value is not of the correct type. value was of type: ${it.class.name} but expected: ${childClass.name}")
                }

                delegate."${addToMethodName}"(it)
            }

        }

        //-------------------------  addTo Method Setup  -------------------------------------------
        domainClass.metaClass."$addToMethodName" = { value ->
            if (value == null) {
                throw new RuntimeException("Value to add to collection cannot be null")
            }
            if (!grailsApplication?.isDomainClass(value.getClass())) {
                throw new RuntimeException("Value must be a domain class")
            }
            if( value.class != childClass ) {
                throw new RuntimeException("Value is not of the correct type. value was of type: ${value.class.name} but expected: ${childClass.name}")
            }

            // parent id value cannot be null - parent must have been persisted first
            def parentIdValue = delegate."${parentKeyPropertyName}"
            if (parentIdValue == null) {
                throw new RuntimeException("Parent key property cannot be null: " + "${parentKeyPropertyName} was null")
            }
            // set the childs parent FK property to the parents id value
            value."${parentFKPropertyName}" = parentIdValue

            // save value
            value.save()
        }

        //-------------------------  addAllTo Method Setup  -------------------------------------------
        domainClass.metaClass."$addAllToMethodName" = { value ->
            if (value == null) {
                throw new RuntimeException("Value to add to collection cannot be null")
            }
            if (!value instanceof Collection) {
                throw new RuntimeException("Value must be a collection")
            }


            (value as Collection).each {
                if( it.class != childClass ) {
                    throw new RuntimeException("Value is not of the correct type. value was of type: ${it.class.name} but expected: ${childClass.name}")
                }

                delegate."${addToMethodName}"(it)
            }

        }

        //-------------------------  removeFrom Method Setup  -------------------------------------------
        // removeFromThings( t )
        // this method will disassociate the instance 't' from the hosts collection but it will not delete 't'
        domainClass.metaClass."$removeFromMethodName" = { value ->
            if (value == null) {
                throw new RuntimeException("Value to remove from collection cannot be null")
            }
            if (!grailsApplication?.isDomainClass(value.getClass())) {
                throw new RuntimeException("Value must be a domain class")
            }
            def parentIdValue = delegate."${parentKeyPropertyName}"
            if (parentIdValue != value."${parentFKPropertyName}") {
                throw new RuntimeException("Value does not have the correct foreign key");
            }
            if( value.class != childClass ) {
                throw new RuntimeException("Value is not of the correct type. value was of type: ${value.class.name} but expected: ${childClass.name}")
            }

            // disassociate the child from the parent but do not delete the child
            value."${parentFKPropertyName}" = null
            value.save()

        }

        //-------------------------  removeAll Method Setup  -------------------------------------------
        domainClass.metaClass."$removeAllMethodName" = {
            def parentIdValue = delegate."${parentKeyPropertyName}"
            String query = "update ${childClass.name} a set a.${parentFKPropertyName} = null where a.${parentFKPropertyName} = :idvalue"
            childClass.executeUpdate(query, [idvalue: parentIdValue])

        }

        //-------------------------  deleteAll Method Setup  -------------------------------------------
        domainClass.metaClass."$deleteAllMethodName" = {
            def parentIdValue = delegate."${parentKeyPropertyName}"
            String query = "delete from ${childClass.name} a where a.${parentFKPropertyName} = :idvalue"
            childClass.executeUpdate(query, [idvalue: parentIdValue])

        }


    }
}