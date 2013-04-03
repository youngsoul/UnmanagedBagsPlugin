package com.bluelobsterstudios.annotations

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Created by IntelliJ IDEA.
 * User: youngsoul
 * Date: 3/26/13
 * Time: 4:03 PM
 */
@Target([ElementType.TYPE]) // Annotation is for actions as well as controller so target is field and for class
@Retention(RetentionPolicy.RUNTIME) // We need it at run time to identify the annotated controller and action
public @interface UnmanagedBags {

    UnmanagedBag[] all();

}