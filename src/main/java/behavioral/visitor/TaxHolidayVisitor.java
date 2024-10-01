package behavioral.visitor;


public class TaxHolidayVisitor extends TaxVisitor {
	

	@Override
	public void visit(Liquor liquor) {
		taxRate = 0.10;
		computeTax(liquor);
	}
	
	@Override
	public void visit(Tobacco tobacco) {
		taxRate = 0.30;
		computeTax(tobacco);
	}
}