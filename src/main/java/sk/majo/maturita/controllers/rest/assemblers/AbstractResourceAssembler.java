package sk.majo.maturita.controllers.rest.assemblers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import sk.majo.maturita.controllers.rest.resources.AbstractResource;

/**
 * AbstractResourceAssembler automatically adds self link to resource and initializes it
 * @author Marian Lorinc
 *
 * @param <T> type of model that can be find in package {@link sk.majo.maturita.database.models}
 * @param <D> type of resource which will be created by assembler
 */
public abstract class AbstractResourceAssembler<T extends Identifiable<?>, D extends AbstractResource<T>>
		extends ResourceAssemblerSupport<T, D> {

	@Autowired
	protected RepositoryEntityLinks entityLinks;

	public AbstractResourceAssembler(Class<?> controllerClass, Class<D> resourceType) {
		super(controllerClass, resourceType);
	}

	@Override
	protected D createResourceWithId(Object id, T entity, Object... parameters) {
		D resource = instantiateResource(entity);
		resource.init(entity);
		resource.add(generateSelfLink(entity));
		return resource;
	}

	/**
	 * Creates resource with entity id
	 * @param entity which will be transformed to resource
	 * @param parameters used in transformation
	 * @return created new resource
	 */
	protected D createResourceWithId(T entity, Object... parameters) {
		return createResourceWithId(entity.getId(), entity, parameters);
	}

	/**
	 * Create link to single resource
	 * @param modelClass type of model class
	 * @param identifiable model class which is identifiable by id
	 * @return link to resource
	 */
	protected Link linkToSingleResource(Class<?> modelClass, Identifiable<?> identifiable) {
		return entityLinks.linkToSingleResource(modelClass, identifiable.getId());
	}

	/**
	 * Create link to collection of models
	 * @param modelClass type of model
	 * @return link to collection of resources
	 */
	protected Link linkToCollection(Class<?> modelClass) {
		return entityLinks.linkToCollectionResource(modelClass);
	}

	/**
	 * Return self link to resource
	 * @param entity which represents this resource
	 * @return self link to resource which represents entity
	 */
	protected Link generateSelfLink(T entity) {
		return entityLinks.linkToSingleResource(entity).withSelfRel();
	}

}
