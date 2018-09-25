package com.harystolho.misc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate that the annotated method must be called before
 * other methods in that class
 * 
 * @author Harystolho
 *
 */
@Target(ElementType.METHOD)
public @interface BeforeUsing {

}
