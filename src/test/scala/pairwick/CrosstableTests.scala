package pairwick

import pairwick.Fixtures.*

class CrosstableTests extends munit.FunSuite:
  test("noRoundsYet") {
    assertEquals(
      Harness.run(List("crosstable", "p.txt", "r.txt"), Map("p.txt" -> P4, "r.txt" -> "")),
      Result(0, "ID PTS\n1 0.0\n2 0.0\n3 0.0\n4 0.0\n", "")
    )
  }

  test("oneRound") {
    assertEquals(
      Harness.run(List("crosstable", "p.txt", "r.txt"), Map("p.txt" -> P8, "r.txt" -> R8_1)),
      Result(
        0,
        "ID PTS R1\n1 1.0 W2\n2 0.0 L1\n3 0.5 D4\n4 0.5 D3\n5 0.0 L6\n6 1.0 W5\n7 1.0 W8\n8 0.0 L7\n",
        ""
      )
    )
  }

  test("threeRounds") {
    assertEquals(
      Harness.run(List("crosstable", "p.txt", "r.txt"), Map("p.txt" -> P8, "r.txt" -> R8_3)),
      Result(
        0,
        "ID PTS R1 R2 R3\n1 2.5 W2 W6 D3\n2 1.5 L1 D4 W8\n3 2.0 D4 W7 D1\n4 1.0 D3 D2 L5\n5 2.0 L6 W8 W4\n6 2.0 W5 L1 W7\n7 1.0 W8 L3 L6\n8 0.0 L7 L5 L2\n",
        ""
      )
    )
  }

  test("byesShowAsB") {
    assertEquals(
      Harness.run(List("crosstable", "p.txt", "r.txt"), Map("p.txt" -> P5, "r.txt" -> R5_3)),
      Result(
        0,
        "ID PTS R1 R2 R3\n1 1.5 W2 D4 L3\n2 1.0 L1 L5 B\n3 2.0 L4 B W1\n4 1.5 W3 D1 L5\n5 3.0 B W2 W4\n",
        ""
      )
    )
  }

  test("refusesProblems") {
    assertEquals(
      Harness.run(
        List("crosstable", "p.txt", "r.txt"),
        Map("p.txt" -> P4, "r.txt" -> "1 1 2 1-0\n")
      ),
      Result(2, "", "pairwick: r.txt: 2 problems, run pairwick check\n")
    )
  }
