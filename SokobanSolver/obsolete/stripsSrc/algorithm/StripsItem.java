package algorithm;

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
