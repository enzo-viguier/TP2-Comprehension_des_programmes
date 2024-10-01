package behavioral.visitor;


public class Liquor extends Product {

	public Liquor(String name, double price) {
		super(name, price);
	}
	

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}