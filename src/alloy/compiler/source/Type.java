package alloy.compiler.source;

import alloy.compiler.type.*;

public sealed interface Type permits BitsLiteralType, FunctionLiteralType, ModuleLiteralType, RationalLiteralType, RecordType, TagType, UnicodeCodePointLiteralType, UnicodeStringLiteralType {

}
