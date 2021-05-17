package alloy.compiler.symbol;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.IntPredicate;

public sealed interface TokenGroup {
	TokenGroup NAME = TokenGroup.of(Character::isAlphabetic);
	TokenGroup ANY = TokenGroup.of(codePoint -> true);
	//TokenGroup SEPARATORS = TokenGroup.of(List.of(' ', '\t', '\n', '\r'), TokenGroup.ANY);

	//TokenGroup TAG = TokenGroup.of();

//	static TokenGroup of(List<Character> codePoints, TokenGroup... next) {
//		return new CodePointGroup(codePoints.stream().mapToInt(Character::charValue).sorted().toArray());
//	}

	static TokenGroup of(IntPredicate isToken, TokenGroup... next) {
		return new PredicateGroup(isToken);
	}

	boolean isMember(int codePoint);
//	Optional<TokenGroup> next()
//
//	record CodePointGroup(int[] tokens, ) implements TokenGroup {
//		public boolean isMember(int codePoint) {
//			return Arrays.binarySearch(tokens, codePoint) >= 0;
//		}
//	}
//
//	record PredicateGroup(IntPredicate isToken) implements TokenGroup {
//		public boolean isMember(int codePoint) {
//			return isToken.test(codePoint);
//		}
//	}
}
