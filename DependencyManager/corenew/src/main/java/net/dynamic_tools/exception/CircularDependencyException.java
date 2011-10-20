package net.dynamic_tools.exception;

/**
 * Created by IntelliJ IDEA.
 * User: Peter
 * Date: 4/30/11
 * Time: 3:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class CircularDependencyException extends Exception {
    public CircularDependencyException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public CircularDependencyException(String message) {
		super(message);
	}
}
