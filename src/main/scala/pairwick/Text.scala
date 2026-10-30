package pairwick

import java.nio.ByteBuffer
import java.nio.charset.{CodingErrorAction, StandardCharsets}
import java.nio.charset.CharacterCodingException

object Text:
  def plural(n: Int, word: String): String =
    s"${n} ${word}${if n == 1 then "" else "s"}"

  def lines(path: String, bytes: Array[Byte]): Vector[(Int, String)] =
    val decoder = StandardCharsets.UTF_8
      .newDecoder()
      .onMalformedInput(CodingErrorAction.REPORT)
      .onUnmappableCharacter(CodingErrorAction.REPORT)
    val text =
      try decoder.decode(ByteBuffer.wrap(bytes)).toString
      catch
        case _: CharacterCodingException =>
          throw Failure(s"${path}: input is not valid UTF-8")

    val withoutBom =
      if text.startsWith("\uFEFF") then text.substring(1) else text
    val result = Vector.newBuilder[(Int, String)]
    var start = 0
    var lineNumber = 1
    while start < withoutBom.length do
      val newline = withoutBom.indexOf('\n', start)
      val end = if newline < 0 then withoutBom.length else newline
      val raw = withoutBom.substring(start, end)
      val line = if raw.endsWith("\r") then raw.dropRight(1) else raw
      if line.nonEmpty && !line.startsWith("#") then result += ((lineNumber, line))
      lineNumber += 1
      start = if newline < 0 then withoutBom.length else newline + 1
    result.result()
