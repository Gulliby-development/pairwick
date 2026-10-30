package pairwick

import pairwick.Fixtures.*

class StandingsTests extends munit.FunSuite:
  test("noGamesYet") {
    assertEquals(
      Harness.run(List("standings", "p.txt", "r.txt"), Map("p.txt" -> P4, "r.txt" -> "")),
      Result(
        0,
        "1 1 0.0 0.0 0.00 0 Ann\n1 2 0.0 0.0 0.00 0 Bob\n1 3 0.0 0.0 0.00 0 Cy\n1 4 0.0 0.0 0.00 0 Di\nplayers=4 rounds=0\n",
        ""
      )
    )
    assertEquals(
      Harness.run(
        List("standings", "p.txt", "r.txt"),
        Map("p.txt" -> P8, "r.txt" -> "# nothing yet\n")
      ),
      Result(
        0,
        "1 1 0.0 0.0 0.00 0 Ann\n1 2 0.0 0.0 0.00 0 Bob\n1 3 0.0 0.0 0.00 0 Cy\n1 4 0.0 0.0 0.00 0 Di\n1 5 0.0 0.0 0.00 0 Ed\n1 6 0.0 0.0 0.00 0 Flo\n1 7 0.0 0.0 0.00 0 Gus\n1 8 0.0 0.0 0.00 0 Hal\nplayers=8 rounds=0\n",
        ""
      )
    )
  }

  test("afterOneRound") {
    assertEquals(
      Harness.run(List("standings", "p.txt", "r.txt"), Map("p.txt" -> P8, "r.txt" -> R8_1)),
      Result(
        0,
        "1 1 1.0 0.0 0.00 1 Ann\n1 6 1.0 0.0 0.00 1 Flo\n1 7 1.0 0.0 0.00 1 Gus\n4 3 0.5 0.5 0.25 0 Cy\n4 4 0.5 0.5 0.25 0 Di\n6 2 0.0 1.0 0.00 0 Bob\n6 5 0.0 1.0 0.00 0 Ed\n6 8 0.0 1.0 0.00 0 Hal\nplayers=8 rounds=1\n",
        ""
      )
    )
  }

  test("afterTwoRounds") {
    assertEquals(
      Harness.run(List("standings", "p.txt", "r.txt"), Map("p.txt" -> P8, "r.txt" -> R8_2)),
      Result(
        0,
        "1 1 2.0 1.5 1.50 2 Ann\n2 3 1.5 2.0 1.50 1 Cy\n3 6 1.0 3.0 1.00 1 Flo\n4 4 1.0 2.0 1.00 0 Di\n5 7 1.0 1.5 0.00 1 Gus\n6 5 1.0 1.0 0.00 1 Ed\n7 2 0.5 3.0 0.50 0 Bob\n8 8 0.0 2.0 0.00 0 Hal\nplayers=8 rounds=2\n",
        ""
      )
    )
  }

  test("afterThreeRounds") {
    assertEquals(
      Harness.run(List("standings", "p.txt", "r.txt"), Map("p.txt" -> P8, "r.txt" -> R8_3)),
      Result(
        0,
        "1 1 2.5 5.5 4.50 2 Ann\n2 6 2.0 5.5 3.00 2 Flo\n3 3 2.0 4.5 2.75 1 Cy\n4 5 2.0 3.0 1.00 2 Ed\n5 2 1.5 3.5 0.50 1 Bob\n6 4 1.0 5.5 1.75 0 Di\n7 7 1.0 4.0 0.00 1 Gus\n8 8 0.0 4.5 0.00 0 Hal\nplayers=8 rounds=3\n",
        ""
      )
    )
  }

  test("byesCountAsAWinForPoints") {
    assertEquals(
      Harness.run(List("standings", "p.txt", "r.txt"), Map("p.txt" -> P5, "r.txt" -> R5_1)),
      Result(
        0,
        "1 1 1.0 0.0 0.00 1 Ann\n1 4 1.0 0.0 0.00 1 Di\n3 5 1.0 0.0 0.00 0 Ed\n4 2 0.0 1.0 0.00 0 Bob\n4 3 0.0 1.0 0.00 0 Cy\nplayers=5 rounds=1\n",
        ""
      )
    )
    assertEquals(
      Harness.run(List("standings", "p.txt", "r.txt"), Map("p.txt" -> P5, "r.txt" -> R5_3)),
      Result(
        0,
        "1 5 3.0 2.5 2.50 2 Ed\n2 3 2.0 3.0 1.50 1 Cy\n3 4 1.5 6.5 2.75 1 Di\n4 1 1.5 4.5 1.75 1 Ann\n5 2 1.0 4.5 0.00 0 Bob\nplayers=5 rounds=3\n",
        ""
      )
    )
  }

  test("tiedPlayersShareARank") {
    assertEquals(
      Harness.run(
        List("standings", "p.txt", "r.txt"),
        Map("p.txt" -> P4, "r.txt" -> "1 1 2 1/2\n1 3 4 1/2\n")
      ),
      Result(
        0,
        "1 1 0.5 0.5 0.25 0 Ann\n1 2 0.5 0.5 0.25 0 Bob\n1 3 0.5 0.5 0.25 0 Cy\n1 4 0.5 0.5 0.25 0 Di\nplayers=4 rounds=1\n",
        ""
      )
    )
    assertEquals(
      Harness.run(List("standings", "p.txt", "r.txt"), Map("p.txt" -> P4, "r.txt" -> R4_2)),
      Result(
        0,
        "1 3 1.5 1.5 1.25 1 Cy\n2 1 1.0 2.5 1.00 1 Ann\n3 2 1.0 1.5 0.50 1 Bob\n4 4 0.5 2.5 0.75 0 Di\nplayers=4 rounds=2\n",
        ""
      )
    )
  }

  test("standardInput") {
    assertEquals(
      Harness.run(
        List("standings", "-", "r.txt"),
        Map("r.txt" -> R8_1),
        P8
      ),
      Result(
        0,
        "1 1 1.0 0.0 0.00 1 Ann\n1 6 1.0 0.0 0.00 1 Flo\n1 7 1.0 0.0 0.00 1 Gus\n4 3 0.5 0.5 0.25 0 Cy\n4 4 0.5 0.5 0.25 0 Di\n6 2 0.0 1.0 0.00 0 Bob\n6 5 0.0 1.0 0.00 0 Ed\n6 8 0.0 1.0 0.00 0 Hal\nplayers=8 rounds=1\n",
        ""
      )
    )
    assertEquals(
      Harness.run(
        List("standings", "p.txt", "-"),
        Map("p.txt" -> P4),
        "1 1 2 1-0\n1 3 4 0-1\n"
      ),
      Result(
        0,
        "1 1 1.0 0.0 0.00 1 Ann\n1 4 1.0 0.0 0.00 1 Di\n3 2 0.0 1.0 0.00 0 Bob\n3 3 0.0 1.0 0.00 0 Cy\nplayers=4 rounds=1\n",
        ""
      )
    )
  }

  test("refusesProblems") {
    assertEquals(
      Harness
        .run(List("standings", "p.txt", "r.txt"), Map("p.txt" -> P4, "r.txt" -> "1 1 2 1-0\n")),
      Result(2, "", "pairwick: r.txt: 2 problems, run pairwick check\n")
    )
    assertEquals(
      Harness.run(
        List("standings", "p.txt", "r.txt"),
        Map("p.txt" -> P4, "r.txt" -> "1 1 2 1-0\n1 1 3 1-0\n1 3 4 1-0\n")
      ),
      Result(2, "", "pairwick: r.txt: 1 problem, run pairwick check\n")
    )
  }
