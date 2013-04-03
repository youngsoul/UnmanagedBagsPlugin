package com.bluelobsterstudios.annotations

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Annotation to collect UnmanagedBag annotations
 * User: youngsoul
 *
 * Usage
 * @UnmanagedBags(all=[
 *   @UnmanagedBag(parentKeyPropertyName="id", collectionPropertyName="myKids", parentFKPropertyName = "ownerId", childClass = com.bluelobsterstudios.Child),
 *   @UnmanagedBag(collectionPropertyName="myHobbies", childClass = Hobby)
 * ])
 */
@Target([ElementType.TYPE]) // Annotation is for actions as well as controller so target is field and for class
@Retention(RetentionPolicy.RUNTIME) // We need it at run time to identify the annotated controller and action
public @interface UnmanagedBags {

    UnmanagedBag[] all();

}