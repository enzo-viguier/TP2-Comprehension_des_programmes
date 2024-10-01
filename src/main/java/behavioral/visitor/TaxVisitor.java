package behavioral.visitor;

public class TaxVisitor implements Visitor {
	

	protected double computedTax;

	protected double taxRate;


	public double getComputedTax() {
		return computedTax;
	}

	public double getTaxRate() {
		return taxRate;
	}
	

	protected void computeTax(Product product) {
		System.out.println(product.getClass().getSimpleName() + " item: Price with Tax");
		computedTax = product.getPrice() * (1 + taxRate);
	}

	@Override
	public void visit(Liquor liquor) {
		taxRate = 0.18;
		computeTax(liquor);
	}

	@Override
	public void visit(Tobacco tobacco) {
		taxRate = 0.32;
		computeTax(tobacco);
	}


	@Override
	public void visit(Necessity necessity) {
		taxRate = 0;
		computeTax(necessity);
	}
}