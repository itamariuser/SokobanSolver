package algorithm;

public abstract class Predicate<T> extends StripsItem<T> {
	
	T data;
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Predicate(String name) {
		super(name);
		this.name = name;
	}

	public Predicate(String name, T data) {
		super(name);
		this.data = data;
	}

	@Override
	public int hashCode() {
		return data.hashCode();
	}

	@Override
	public String toString() {
		return "Type: "+this.getClass().getSimpleName()+"\nName: "+this.name+"\nData: "+data.toString();
	}
	
}
