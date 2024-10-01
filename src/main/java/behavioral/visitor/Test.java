package behavioral.visitor;

import java.util.Arrays;
import java.util.List;


public class Test {
	
	public static void main(String[] args) {	
		List<Product> products = Arrays.asList(
				new Necessity("Milk", 3.47),
				new Liquor("Vodka", 11.99),
				new Tobacco("Cigar", 19.99)
		);
		
		System.out.println("Tax prices\n==========");
		computeTaxForProducts(products, new TaxVisitor());
		
		System.out.println("Holiday Tax prices\n==================");
		computeTaxForProducts(products, new TaxHolidayVisitor());
	}

	private static void computeTaxForProducts(List<Product> products, 
				TaxVisitor taxVisitor) {
		for (Product product: products) {
			product.accept(taxVisitor);
			System.out.println(taxVisitor.getComputedTax() + "\n");
		}
	}
}