package sk.majo.maturita.controllers.rest.resources;

import org.springframework.hateoas.ResourceSupport;

/**
 * Abstract resource which requires children classes to implement init method. The init method
 * is responsible for setting data which will be sent to client side.
 * @author Marian Lorinc
 *
 * @param <T> type of entity model
 */
public abstract class AbstractResource<T> extends ResourceSupport {

	/**
	 * Initializes data of resource which will be marshaled to JSON and sent to client side. In order to send data
	 * to client, child class must declare and initialize class variables. Getters to these fields
	 * are mandatory as well.
	 * @param entity holding data, which can be marshaled or ignored.
	 */
	public abstract void init(T entity);
	
}
