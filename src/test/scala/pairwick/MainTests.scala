package pairwick

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files

class MainTests extends munit.FunSuite:
  private def exec(args: List[String], stdin: Array[Byte]): (Int, String, String) =
    val out = new ByteArrayOutputStream
    val err = new ByteArrayOutputStream
    val code = Main.exec(args.toArray, new ByteArrayInputStream(stdin), out, err)
    (code, out.toString(UTF_8), err.toString(UTF_8))

  test("readsRealFilesAndWritesLfOnly") {
    val dir = Files.createTempDirectory("pairwick")
    val players = dir.resolve("p.txt")
    val results = dir.resolve("r.txt")
    Files.write(players, "1 Ann\r\n2 Bob\r\n".getBytes(UTF_8))
    Files.write(results, "1 1 2 1/2\r\n".getBytes(UTF_8))
    val expected = "1 1 0.5 0.5 0.25 0 Ann\n1 2 0.5 0.5 0.25 0 Bob\nplayers=2 rounds=1\n"
    assertEquals(
      exec(List("standings", players.toString, results.toString), Array.empty),
      (0, expected, "")
    )
    assertEquals(
      exec(List("check", players.toString, "missing.txt"), Array.empty)._3,
      "pairwick: missing.txt: cannot read file\n"
    )
  }

  test("readsStandardInputAsBytes") {
    assertEquals(
      exec(List("standings", "-", "r.txt"), Array(0x31.toByte, 0xff.toByte)),
      (2, "", "pairwick: <stdin>: input is not valid UTF-8\n")
    )
    assertEquals(
      exec(List("rounds", "9"), Array.empty),
      (0, "9 players: 4 rounds\n", "")
    )
  }
