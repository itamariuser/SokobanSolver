package algorithm;

/**
 * A class to be used within the Strips algorithm.
 * @param <T> - The domain.
 */
public abstract class StripsItem<T> {
	
	protected String name;
	
	public StripsItem(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
