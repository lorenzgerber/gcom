package order;

public enum Orderers {
	Unordered("Unordered"), Causal("Causal");

	private String name;

	Orderers(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
