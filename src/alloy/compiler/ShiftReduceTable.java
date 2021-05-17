package alloy.compiler;

import alloy.compiler.symbol.Construct;

public class ShiftReduceTable {

	public void /* should return Action+next_state? */ given(int state, Construct token)

	/*

	from http://underpop.online.fr/j/java/help/lr-parsing-compiler-java-programming-language.html.gz

	sn Shift into state n;

	gn Goto state n;

	rk Reduce by rule k;

	a Accept;

	Error (denoted by a blank entry in the table);



	Read from:

	We can now construct a parsing table for this grammar (Table 3.22). For each edge Java ScreenShot
	where X is a terminal, we put the action shift J at position (I, X) of the table; if X is a nonterminal,
	 we put goto J at position (I, X). For each state I containing an item S′ → S.$ we put an accept action
	 at (I, $). Finally, for a state containing an item A → γ. (production n with the dot at the end), we put
	  a reduce n action at (I, Y) for every token Y

	 */
}
