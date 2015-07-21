package com.zy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 
 * 标识数据库字段的ID 
 */  
@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.FIELD)  
public @interface Id {	  
	    /** 
	     * ID的名称 
	     * @return 
	     */  
	    String value();  
}
