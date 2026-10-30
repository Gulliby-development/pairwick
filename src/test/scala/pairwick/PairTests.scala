package pairwick

import pairwick.Fixtures.*

class PairTests extends munit.FunSuite:
  test("firstRoundPairsNeighbours") {
    assertEquals(
      Harness.run(List("pair", "p.txt", "r.txt"), Map("p.txt" -> P4, "r.txt" -> "")),
      Result(0, "round 1\n1 1 2\n2 3 4\n", "")
    )
    assertEquals(
      Harness.run(List("pair", "p.txt", "r.txt"), Map("p.txt" -> P8, "r.txt" -> "")),
      Result(0, "round 1\n1 1 2\n2 3 4\n3 5 6\n4 7 8\n", "")
    )
  }

  test("firstRoundWithABye") {
    assertEquals(
      Harness.run(List("pair", "p.txt", "r.txt"), Map("p.txt" -> P5, "r.txt" -> "")),
      Result(0, "round 1\n1 1 2\n2 3 4\nbye 5\n", "")
    )
  }

  test("laterRoundsFollowThePoints") {
    assertEquals(
      Harness.run(List("pair", "p.txt", "r.txt"), Map("p.txt" -> P8, "r.txt" -> R8_1)),
      Result(0, "round 2\n1 1 6\n2 7 3\n3 4 2\n4 5 8\n", "")
    )
    assertEquals(
      Harness.run(List("pair", "p.txt", "r.txt"), Map("p.txt" -> P8, "r.txt" -> R8_2)),
      Result(0, "round 3\n1 1 3\n2 4 5\n3 6 7\n4 2 8\n", "")
    )
    assertEquals(
      Harness.run(List("pair", "p.txt", "r.txt"), Map("p.txt" -> P8, "r.txt" -> R8_3)),
      Result(0, "round 4\n1 1 5\n2 3 6\n3 2 7\n4 4 8\n", "")
    )
  }

  test("theByeGoesToTheLowestWithoutOne") {
    assertEquals(
      Harness.run(List("pair", "p.txt", "r.txt"), Map("p.txt" -> P5, "r.txt" -> R5_1)),
      Result(0, "round 2\n1 1 4\n2 5 2\nbye 3\n", "")
    )
    assertEquals(
      Harness.run(List("pair", "p.txt", "r.txt"), Map("p.txt" -> P5, "r.txt" -> R5_2)),
      Result(0, "round 3\n1 5 4\n2 1 3\nbye 2\n", "")
    )
  }

  test("rematchesAreAvoided") {
    assertEquals(
      Harness.run(
        List("pair", "p.txt", "r.txt"),
        Map("p.txt" -> P4, "r.txt" -> "1 1 2 1-0\n1 3 4 1/2\n")
      ),
      Result(0, "round 2\n1 1 3\n2 4 2\n", "")
    )
    assertEquals(
      Harness.run(List("pair", "p.txt", "r.txt"), Map("p.txt" -> P4, "r.txt" -> R4_2)),
      Result(0, "round 3\n1 3 2\n2 1 4\n", "")
    )
  }

  test("noPairingLeft") {
    assertEquals(
      Harness.run(List("pair", "p.txt", "r.txt"), Map("p.txt" -> P4, "r.txt" -> R4_ALL)),
      Result(1, "", "pairwick: no pairing exists for round 4\n")
    )
  }

  test("noByeLeft") {
    assertEquals(
      Harness.run(List("pair", "p.txt", "r.txt"), Map("p.txt" -> P5, "r.txt" -> R5_5)),
      Result(1, "", "pairwick: no player can take the bye in round 6\n")
    )
  }

  test("refusesProblems") {
    assertEquals(
      Harness.run(List("pair", "p.txt", "r.txt"), Map("p.txt" -> P4, "r.txt" -> "1 1 2 1-0\n")),
      Result(2, "", "pairwick: r.txt: 2 problems, run pairwick check\n")
    )
  }

  test("standardInput") {
    assertEquals(
      Harness.run(List("pair", "-", "r.txt"), Map("r.txt" -> ""), stdin = P4),
      Result(0, "round 1\n1 1 2\n2 3 4\n", "")
    )
  }
