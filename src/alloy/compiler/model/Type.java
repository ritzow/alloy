package alloy.compiler.model;

import alloy.compiler.type.*;

public sealed interface Type permits
	BitsLiteralType,
	FunctionLiteralType,
	RationalLiteralType,
	alloy.compiler.type.RecordType,
	UnicodeCodePointLiteralType,
	UnicodeStringLiteralType {

}
