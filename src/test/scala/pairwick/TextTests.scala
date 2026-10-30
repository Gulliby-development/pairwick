package pairwick

import java.nio.charset.StandardCharsets.UTF_8

class TextTests extends munit.FunSuite:
  private def lines(text: String): List[(Int, String)] =
    Text.lines("t.txt", text.getBytes(UTF_8)).toList

  test("keepsThePhysicalLineNumbers") {
    assertEquals(lines("a\n\n# c\nb\n"), List((1, "a"), (4, "b")))
    assertEquals(lines("a\nb"), List((1, "a"), (2, "b")))
    assertEquals(lines(""), Nil)
  }

  test("dropsExactlyOneByteOrderMark") {
    assertEquals(lines("\uFEFFa\n"), List((1, "a")))
    assertEquals(lines("\uFEFF\uFEFFa\n"), List((1, "\uFEFFa")))
    assertEquals(lines("\uFEFF# c\nb\n"), List((2, "b")))
  }

  test("removesOneCarriageReturnPerLine") {
    assertEquals(lines("a\r\nb\r\n"), List((1, "a"), (2, "b")))
    assertEquals(lines("a\r\r\nb\n"), List((1, "a\r"), (2, "b")))
    assertEquals(lines("a\rb\n"), List((1, "a\rb")))
    assertEquals(lines("\r\n#x\r\n"), Nil)
  }

  test("rejectsBadUtf8") {
    val bad = List(
      Array(0x31.toByte, 0xff.toByte),
      Array(0xc3.toByte),
      Array(0xc0.toByte, 0x80.toByte),
      Array(0xed.toByte, 0xa0.toByte, 0x80.toByte),
      Array(0xf4.toByte, 0x90.toByte, 0x80.toByte, 0x80.toByte)
    )
    for bytes <- bad do
      val failure = intercept[Failure](Text.lines("t.txt", bytes))
      assertEquals(failure, Failure("t.txt: input is not valid UTF-8", 2))
  }

  test("pluralWords") {
    assertEquals(Text.plural(0, "round"), "0 rounds")
    assertEquals(Text.plural(1, "round"), "1 round")
    assertEquals(Text.plural(2, "problem"), "2 problems")
  }
