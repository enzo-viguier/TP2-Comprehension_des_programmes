package behavioral.visitor;

public interface Visitor {

	void visit(Liquor liquor);
	

	void visit(Tobacco tobacco);
	

	void visit(Necessity necessity);
}