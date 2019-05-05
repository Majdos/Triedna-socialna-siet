package sk.majo.maturita.database;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Customized pageable element which allows query data with offset and limit
 * @author Marian Lorinc
 *
 */
public class RangePageRequest implements Pageable {

	private long offset;
	private int limit;
	private Sort sort;

	/**
	 * Creates new pageable object
	 * @param offset offset from start
	 * @param limit number of elements queried
	 * @param sort sort
	 */
	public RangePageRequest(long offset, int limit, Sort sort) {
		this.offset = offset;
		this.limit = limit;
		this.sort = sort != null ? sort : Sort.unsorted();
	}

	/**
	 * Creates new pageable object
	 * @param offset offset from start
	 * @param limit number of elements queried
	 */
	public RangePageRequest(long offset, int limit) {
		this(offset, limit, null);
	}

	@Override
	public int getPageNumber() {
		return (int) (offset / limit);
	}

	@Override
	public int getPageSize() {
		return limit;
	}

	@Override
	public long getOffset() {
		return offset;
	}

	@Override
	public Sort getSort() {
		return sort;
	}

	@Override
	public Pageable next() {
		return new RangePageRequest(offset + limit, limit, sort);
	}

	@Override
	public Pageable previousOrFirst() {
		return hasPrevious() ? new RangePageRequest(offset - limit, limit, sort) : first();
	}

	@Override
	public Pageable first() {
		return new RangePageRequest(0, limit, sort);
	}

	@Override
	public boolean hasPrevious() {
		return offset >= limit;
	}

}
