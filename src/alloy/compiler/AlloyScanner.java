package alloy.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class AlloyScanner {
	public static IntStream codePoints(Path file) throws IOException {
		return StreamSupport.intStream(new Spliterator.OfInt() {
			BufferedReader in = Files.newBufferedReader(file, StandardCharsets.UTF_8);

			@Override
			public OfInt trySplit() {
				return null; //can't be split for now
			}

			@Override
			public long estimateSize() {
				return Long.MAX_VALUE;
			}

			@Override
			public int characteristics() {
				return ORDERED | NONNULL | IMMUTABLE;
			}

			@Override
			public boolean tryAdvance(IntConsumer action) {
				try {
					int unit0 = in.read();
					if(unit0 >= 0) {
						if(Character.isHighSurrogate((char)unit0)) {
							int unit1 = in.read();
							if(unit1 >= 0) {
								if(Character.isLowSurrogate((char)unit1)) {
									action.accept(Character.toCodePoint((char)unit0, (char)unit1));
									return true;
								} else {
									in.close();
									throw new RuntimeException("Invalid surrogate pair");
								}
							} else {
								in.close();
								return false;
							}
						} else {
							action.accept(unit0);
							return true;
						}
					} else {
						in.close();
						return false; //EOF
					}
				} catch(IOException e) {
					throw new RuntimeException(e);
				}
			}
		}, false);
	}
}
