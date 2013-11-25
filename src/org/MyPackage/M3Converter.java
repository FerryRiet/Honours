package org.MyPackage;

import java.util.Stack;

import org.eclipse.imp.pdb.facts.IConstructor;
import org.eclipse.imp.pdb.facts.IList;
import org.eclipse.imp.pdb.facts.ISetWriter;
import org.eclipse.imp.pdb.facts.ISourceLocation;
import org.eclipse.imp.pdb.facts.IString;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.type.TypeFactory;
import org.eclipse.imp.pdb.facts.type.TypeStore;

public abstract class M3Converter extends JavaToRascalConverter {
	private static final String DATATYPE_M3_NODE							= "M3";
	private final org.eclipse.imp.pdb.facts.type.Type DATATYPE_M3_NODE_TYPE;
	private final org.eclipse.imp.pdb.facts.type.Type DATATYPE_TYPESYMBOL;
	
	private static final org.eclipse.imp.pdb.facts.type.Type locType 		= TF.sourceLocationType();
	private static final org.eclipse.imp.pdb.facts.type.Type m3TupleType 	= TF.tupleType(locType, locType);
	private final org.eclipse.imp.pdb.facts.type.Type m3LOCModifierType;
	private final org.eclipse.imp.pdb.facts.type.Type m3LOCTypeType;
	
	protected final Stack<ISourceLocation> scopeManager = new Stack<ISourceLocation>();
	
	protected ISetWriter uses;
	protected ISetWriter declarations;
	protected ISetWriter containment;
	protected ISetWriter extendsRelations;
	protected ISetWriter implementsRelations;
	protected ISetWriter fieldAccess;
	protected ISetWriter methodInvocation;
	protected ISetWriter typeDependency;
	protected ISetWriter documentation;
	protected ISetWriter modifiers;
	protected ISetWriter names;
	protected ISetWriter methodOverrides;
	protected ISetWriter types;
	protected final org.eclipse.imp.pdb.facts.type.Type CONSTRUCTOR_M3;
	
	@SuppressWarnings("deprecation")
	M3Converter(final TypeStore typeStore) {
		super(typeStore, true);
		this.DATATYPE_M3_NODE_TYPE = this.typeStore.lookupAbstractDataType(DATATYPE_M3_NODE);
		TypeFactory tf = TypeFactory.getInstance();
    this.CONSTRUCTOR_M3= this.typeStore.lookupConstructor(DATATYPE_M3_NODE_TYPE, "m3", tf.tupleType(tf.sourceLocationType()));
		this.DATATYPE_TYPESYMBOL = this.typeStore.lookupAbstractDataType("TypeSymbol");
		uses = values.relationWriter(m3TupleType);
		declarations = values.relationWriter(m3TupleType);
		containment = values.relationWriter(m3TupleType);
		extendsRelations = values.relationWriter(m3TupleType);
		implementsRelations = values.relationWriter(m3TupleType);
		fieldAccess = values.relationWriter(m3TupleType);
		methodInvocation = values.relationWriter(m3TupleType);
		m3LOCModifierType = TF.tupleType(locType, DATATYPE_RASCAL_AST_MODIFIER_NODE_TYPE);
		modifiers = values.relationWriter(m3LOCModifierType);
		m3LOCTypeType = TF.tupleType(locType, locType);
		typeDependency = values.relationWriter(m3LOCTypeType);
		documentation = values.relationWriter(m3TupleType);
		names = values.relationWriter(TF.tupleType(TF.stringType(), locType));
		methodOverrides = values.relationWriter(TF.tupleType(locType, locType));
		types = values.relationWriter(TF.tupleType(locType, DATATYPE_TYPESYMBOL));
	}
	
	public IValue getModel(boolean insertErrors) {
		ownValue = values.constructor(CONSTRUCTOR_M3, loc);
		setAnnotation("declarations", declarations.done());
		setAnnotation("uses", uses.done());
		setAnnotation("containment", containment.done());
		setAnnotation("extends", extendsRelations.done());
		setAnnotation("implements", implementsRelations.done());
		setAnnotation("methodInvocation", methodInvocation.done());
		setAnnotation("modifiers", modifiers.done());
		setAnnotation("typeDependency", typeDependency.done());
		setAnnotation("documentation", documentation.done());
		setAnnotation("fieldAccess", fieldAccess.done());
		setAnnotation("names", names.done());
		setAnnotation("methodOverrides", methodOverrides.done());
		setAnnotation("types", types.done());
		insertCompilationUnitMessages(insertErrors);
		return ownValue;
	}
	
	public ISourceLocation getParent() {
		return scopeManager.peek();
	}
	
	public void insert(ISetWriter relW, IValue lhs, IValue rhs) {
		if ((isValid((ISourceLocation) lhs) && isValid((ISourceLocation) rhs))) {
			relW.insert(values.tuple(lhs, rhs));
		}
	}

	public void insert(ISetWriter relW, IValue lhs, IValueList rhs) {
		for (IValue oneRHS: (IList)rhs.asList())
			if (lhs.getType().isString() || (isValid((ISourceLocation) lhs) && isValid((ISourceLocation) oneRHS)))
				insert(relW, lhs, oneRHS);
	}
	
	public void insert(ISetWriter relW, IString lhs, IValue rhs) {
		if (isValid((ISourceLocation) rhs)) {
			relW.insert(values.tuple(lhs, rhs));
		}
	}
	
	public void insert(ISetWriter relW, IValue lhs, IConstructor rhs) {
		if (isValid((ISourceLocation) lhs) && rhs != null) {
			relW.insert(values.tuple(lhs, rhs));
		}
	}
	
	protected boolean isValid(ISourceLocation binding) {
		return binding != null && !(binding.getScheme().equals("unknown") || binding.getScheme().equals("unresolved"));
	}
}
