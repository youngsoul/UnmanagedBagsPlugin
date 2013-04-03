package com.bluelobsterstudios.annotations

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * User: youngsoul
 * Annotation used to specify an UnmanagedBag collection.  Currently will add dynamic methods, but future
 * implementation may use AST
 *
 * Usage:
 * @UnmanagedBag(parentKeyPropertyName="id", collectionPropertyName="myKids", parentFKPropertyName = "ownerId", childClass = com.bluelobsterstudios.Child),
 */
@Target([ElementType.TYPE]) // Annotation is for actions as well as controller so target is field and for class
@Retention(RetentionPolicy.RUNTIME) // We need it at run time to identify the annotated controller and action
public @interface UnmanagedBag {


    /**
     * What property should be used to refer back to the parent.  For example, 'id' or 'apId'
     * @return
     */
    String parentKeyPropertyName() default "id";

    /**
     * Name of the property in the childClass that will be used to hold the fk of the parent object.
     * @return
     */
    String parentFKPropertyName() default "ownerId";

    /**
     * Type of the child collection
     * @return
     */
    Class childClass();

    /**
     * the name of the instance for the collection.  For example if you had a collection of Books, you might
     * call the field, 'bookList', or 'books'.  Methods will be generated of the form:
     * addToBookList or addToBooks
     */
    String collectionPropertyName();

}