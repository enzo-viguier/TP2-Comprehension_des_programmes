package behavioral.visitor;


public class Tobacco extends Product {

	public Tobacco(String name, double price) {
		super(name, price);
	}
	

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}