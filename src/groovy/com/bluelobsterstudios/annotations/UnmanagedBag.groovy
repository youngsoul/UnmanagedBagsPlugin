package com.bluelobsterstudios.annotations

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Created by IntelliJ IDEA.
 * User: youngsoul
 * Date: 3/26/13
 * Time: 3:12 PM
 *
 *
 */
@Target([ElementType.TYPE]) // Annotation is for actions as well as controller so target is field and for class
@Retention(RetentionPolicy.RUNTIME) // We need it at run time to identify the annotated controller and action
public @interface UnmanagedBag {

    /**
     * What property should be used to refer back to the parent.  For example, 'id' or 'apId'
     */
    String parentKeyPropertyName() default "id";

    String parentFKPropertyName() default "ownerId";

    Class childClass();

    /**
     * the name of the instance for the collection.  For example if you had a collection of Books, you might
     * call the field, 'bookList', or 'books'.  Methods will be generated of the form:
     * addToBookList or addToBooks
     */
    String collectionPropertyName();

}