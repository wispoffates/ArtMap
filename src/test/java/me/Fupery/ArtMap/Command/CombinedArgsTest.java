package me.Fupery.ArtMap.Command;

import org.junit.Assert;
import org.junit.Test;

public class CombinedArgsTest {

	@Test
	public void TestCombinedArgsNoSpaces() {
		String[] before = { "give", "artwork:test", "2" };
		String[] after = CommandHandler.fixQuotedArgs(before);
		Assert.assertArrayEquals(before, after);
	}

	@Test
	public void TestCombinedArgsSimpleSpaces() {
		String[] before = { "give", "\"artwork:test", "test\"", "2" };
		String[] after = CommandHandler.fixQuotedArgs(before);
		String[] result = { "give", "artwork:test test", "2" };
		Assert.assertArrayEquals(result, after);
	}

	@Test
	public void TestCombinedArgsSingleWordQuoted() {
		String[] before = { "give", "\"artwork:test\"", "2" };
		String[] after = CommandHandler.fixQuotedArgs(before);
		String[] result = { "give", "artwork:test", "2" };
		Assert.assertArrayEquals(result, after);
	}

	@Test
	public void TestCombinedArgsDoubleCombine() {
		String[] before = { "give", "\"artwork:test", "test\"", "\"more", "space\"", "2" };
		String[] after = CommandHandler.fixQuotedArgs(before);
		String[] result = { "give", "artwork:test test", "more space", "2" };
		Assert.assertArrayEquals(result, after);
	}

	@Test
	public void TestCombinedWierdInternalQuoteCombine() {
		String[] before = { "give", "artwork:\"test", "test\"", "\"more", "space\"", "2" };
		String[] after = CommandHandler.fixQuotedArgs(before);
		String[] result = { "give", "artwork:test test", "more space", "2" };
		Assert.assertArrayEquals(result, after);
	}

}
