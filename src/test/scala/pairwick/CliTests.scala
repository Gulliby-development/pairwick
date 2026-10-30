package pairwick

import pairwick.Fixtures.*

class CliTests extends munit.FunSuite:
  test("helpAndUsage") {
    assertEquals(
      Harness.run(List("help")),
      Result(
        0,
        "usage: pairwick COMMAND [ARGS]\n  standings PLAYERS RESULTS\n  pair PLAYERS RESULTS\n  check PLAYERS RESULTS\n  crosstable PLAYERS RESULTS\n  rounds COUNT\n",
        ""
      )
    )
    assertEquals(
      Harness.run(List("-h")),
      Result(
        0,
        "usage: pairwick COMMAND [ARGS]\n  standings PLAYERS RESULTS\n  pair PLAYERS RESULTS\n  check PLAYERS RESULTS\n  crosstable PLAYERS RESULTS\n  rounds COUNT\n",
        ""
      )
    )
    assertEquals(
      Harness.run(List("--help")),
      Result(
        0,
        "usage: pairwick COMMAND [ARGS]\n  standings PLAYERS RESULTS\n  pair PLAYERS RESULTS\n  check PLAYERS RESULTS\n  crosstable PLAYERS RESULTS\n  rounds COUNT\n",
        ""
      )
    )
    assertEquals(
      Harness.run(Nil),
      Result(
        2,
        "",
        "usage: pairwick COMMAND [ARGS]\n  standings PLAYERS RESULTS\n  pair PLAYERS RESULTS\n  check PLAYERS RESULTS\n  crosstable PLAYERS RESULTS\n  rounds COUNT\n"
      )
    )
  }

  test("unknownThings") {
    assertEquals(Harness.run(List("frob")), Result(2, "", "pairwick: unknown command \"frob\"\n"))
    assertEquals(
      Harness.run(List("check", "-x", "p.txt", "r.txt")),
      Result(2, "", "pairwick: unknown option \"-x\"\n")
    )
    assertEquals(
      Harness.run(List("pair", "--top=3", "p.txt", "r.txt")),
      Result(2, "", "pairwick: unknown option \"--top=3\"\n")
    )
    assertEquals(
      Harness.run(List("frob", "--x")),
      Result(2, "", "pairwick: unknown command \"frob\"\n")
    )
  }

  test("argumentCounts") {
    assertEquals(
      Harness.run(List("standings", "p.txt"), Map("p.txt" -> P4)),
      Result(2, "", "pairwick: standings: wrong number of arguments\n")
    )
    assertEquals(
      Harness.run(List("pair", "p.txt"), Map("p.txt" -> P4)),
      Result(2, "", "pairwick: pair: wrong number of arguments\n")
    )
    assertEquals(
      Harness.run(List("check", "p.txt"), Map("p.txt" -> P4)),
      Result(2, "", "pairwick: check: wrong number of arguments\n")
    )
    assertEquals(
      Harness.run(List("crosstable", "p.txt"), Map("p.txt" -> P4)),
      Result(2, "", "pairwick: crosstable: wrong number of arguments\n")
    )
    assertEquals(
      Harness.run(List("pair", "p.txt", "r.txt", "extra"), Map("p.txt" -> P4, "r.txt" -> "")),
      Result(2, "", "pairwick: pair: wrong number of arguments\n")
    )
    assertEquals(
      Harness.run(List("check")),
      Result(2, "", "pairwick: check: wrong number of arguments\n")
    )
  }

  test("standardInputOnlyOnce") {
    assertEquals(
      Harness.run(List("check", "-", "-"), stdin = P4),
      Result(2, "", "pairwick: only one of PLAYERS and RESULTS can be -\n")
    )
    assertEquals(
      Harness.run(List("crosstable", "-", "r.txt"), Map("r.txt" -> ""), P4),
      Result(0, "ID PTS\n1 0.0\n2 0.0\n3 0.0\n4 0.0\n", "")
    )
  }

  test("preservesCallbackFailures") {
    assertEquals(
      Cli.run(
        List("check", "p.txt", "r.txt"),
        _ => throw Failure("p.txt: cannot read file", 2),
        () => Array.emptyByteArray
      ),
      Result(2, "", "pairwick: p.txt: cannot read file\n")
    )
    assertEquals(
      Cli.run(
        List("check", "p.txt", "r.txt"),
        _ => throw Failure("custom", 1),
        () => Array.emptyByteArray
      ),
      Result(1, "", "pairwick: custom\n")
    )
  }

  test("normalizesCallbackIoFailures") {
    assertEquals(
      Cli.run(
        List("check", "p.txt", "r.txt"),
        _ => throw new java.io.IOException,
        () => Array.emptyByteArray
      ),
      Result(2, "", "pairwick: p.txt: cannot read file\n")
    )
    assertEquals(
      Cli.run(
        List("check", "-", "r.txt"),
        _ => Some("1 Ann\n2 Bob\n".getBytes("UTF-8")),
        () => throw new java.io.IOException
      ),
      Result(2, "", "pairwick: <stdin>: cannot read file\n")
    )
  }

  test("callbacksAreUntouchedForNonInputCommands") {
    var calls = 0
    val read: String => Option[Array[Byte]] = _ => { calls += 1; None }
    val stdin: () => Array[Byte] = () => { calls += 1; Array.emptyByteArray }
    assertEquals(Cli.run(List("help"), read, stdin), Result(0, Cli.usage, ""))
    assertEquals(Cli.run(List("rounds", "9"), read, stdin), Result(0, "9 players: 4 rounds\n", ""))
    assertEquals(Cli.run(Nil, read, stdin), Result(2, "", Cli.usage))
    assertEquals(
      Cli.run(List("check", "-", "-"), read, stdin),
      Result(2, "", "pairwick: only one of PLAYERS and RESULTS can be -\n")
    )
    assertEquals(calls, 0)
  }

  test("readsPlayersBeforeResultsAndConsumesStdinOnce") {
    var reads = List.empty[String]
    var stdinCalls = 0
    val read: String => Option[Array[Byte]] = path => {
      reads = reads :+ path
      if path == "p.txt" then Some("1Ann\n2 Bob\n".getBytes("UTF-8"))
      else Some(Array.emptyByteArray)
    }
    val playersError = Cli.run(List("check", "p.txt", "r.txt"), read, () => Array.emptyByteArray)
    assertEquals(
      playersError,
      Result(2, "", "pairwick: p.txt:1: not a player line of the form ID NAME\n")
    )
    assertEquals(reads, List("p.txt"))
    reads = Nil
    val stdinResult = Cli.run(
      List("crosstable", "-", "r.txt"),
      path => { reads = reads :+ path; Some(Array.emptyByteArray) },
      () => { stdinCalls += 1; P4.getBytes("UTF-8") }
    )
    assertEquals(stdinResult, Result(0, "ID PTS\n1 0.0\n2 0.0\n3 0.0\n4 0.0\n", ""))
    assertEquals(reads, List("r.txt"))
    assertEquals(stdinCalls, 1)
  }

  test("helpArgumentPrecedenceAndDirtyStdin") {
    assertEquals(
      Harness.run(List("help", "extra")),
      Result(2, "", "pairwick: help: wrong number of arguments\n")
    )
    assertEquals(
      Harness.run(List("help", "extra", "--x")),
      Result(2, "", "pairwick: unknown option \"--x\"\n")
    )
    assertEquals(
      Harness.run(List("standings", "p.txt", "-"), Map("p.txt" -> P4), "1 1 2 1-0\n"),
      Result(2, "", "pairwick: <stdin>: 2 problems, run pairwick check\n")
    )
  }
