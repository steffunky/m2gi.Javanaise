package jvn.dynamicproxy;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JvnAction 
{
	static enum ActionType 
	{
        READ,
        WRITE
    }
    ActionType actionType();
}
