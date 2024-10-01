package behavioral.visitor;


public class Necessity extends Product {

	public Necessity(String name, double price) {
		super(name, price);
	}
	

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}