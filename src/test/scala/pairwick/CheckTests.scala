package pairwick

import pairwick.Fixtures.*

class CheckTests extends munit.FunSuite:
  test("cleanFiles") {
    assertEquals(
      Harness.run(List("check", "p.txt", "r.txt"), Map("p.txt" -> P8, "r.txt" -> R8_3)),
      Result(0, "ok: 3 rounds, 12 games, 0 byes\n", "")
    )
    assertEquals(
      Harness.run(List("check", "p.txt", "r.txt"), Map("p.txt" -> P5, "r.txt" -> R5_2)),
      Result(0, "ok: 2 rounds, 4 games, 2 byes\n", "")
    )
    assertEquals(
      Harness.run(List("check", "p.txt", "r.txt"), Map("p.txt" -> P4, "r.txt" -> "")),
      Result(0, "ok: 0 rounds, 0 games, 0 byes\n", "")
    )
  }

  test("gameRules") {
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> P4, "r.txt" -> "1 1 1 1-0\n1 1 2 1-0\n1 3 4 1/2\n")
      ),
      Result(1, "r.txt:1: player 1 cannot play themselves\n1 problem\n", "")
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> P4, "r.txt" -> "1 1 9 1-0\n1 1 2 1-0\n1 3 4 1/2\n")
      ),
      Result(1, "r.txt:1: unknown player 9\n1 problem\n", "")
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> P4, "r.txt" -> "1 1 2 1-0\n1 2 3 0-1\n1 3 4 1/2\n")
      ),
      Result(1, "r.txt:2: player 2 is already paired in round 1\n1 problem\n", "")
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> P4, "r.txt" -> "1 1 2 1-0\n1 3 4 1/2\n2 2 1 0-1\n2 3 4 1-0\n")
      ),
      Result(
        1,
        "r.txt:3: players 2 and 1 already met in round 1\nr.txt:4: players 3 and 4 already met in round 1\nr.txt: round 2 has no results\n3 problems\n",
        ""
      )
    )
  }

  test("byeRules") {
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map(
          "p.txt" -> P5,
          "r.txt" -> "1 1 2 1-0\n1 3 4 1/2\n1 5 bye\n2 1 3 1-0\n2 2 5 1-0\n2 4 bye\n"
        )
      ),
      Result(0, "ok: 2 rounds, 4 games, 2 byes\n", "")
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map(
          "p.txt" -> P5,
          "r.txt" -> "1 1 2 1-0\n1 3 4 1/2\n1 5 bye\n2 1 3 1-0\n2 2 4 1-0\n2 5 bye\n"
        )
      ),
      Result(
        1,
        "r.txt:6: player 5 already had a bye in round 1\nr.txt: round 2: player 5 has no game\n2 problems\n",
        ""
      )
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> P5, "r.txt" -> "1 1 2 1-0\n1 3 4 1/2\n1 6 bye\n")
      ),
      Result(1, "r.txt:3: unknown player 6\nr.txt: round 1: player 5 has no game\n2 problems\n", "")
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> P5, "r.txt" -> "1 1 2 1-0\n1 1 bye\n")
      ),
      Result(
        1,
        "r.txt:2: player 1 is already paired in round 1\nr.txt: round 1: player 3 has no game\nr.txt: round 1: player 4 has no game\nr.txt: round 1: player 5 has no game\n4 problems\n",
        ""
      )
    )
  }

  test("incompleteRounds") {
    assertEquals(
      Harness.run(List("check", "p.txt", "r.txt"), Map("p.txt" -> P4, "r.txt" -> "1 1 2 1-0\n")),
      Result(
        1,
        "r.txt: round 1: player 3 has no game\nr.txt: round 1: player 4 has no game\n2 problems\n",
        ""
      )
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> P4, "r.txt" -> "1 1 2 1-0\n1 3 4 1/2\n3 1 3 1-0\n3 2 4 1-0\n")
      ),
      Result(1, "r.txt: round 2 has no results\n1 problem\n", "")
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> P5, "r.txt" -> "1 1 2 1-0\n1 3 4 1/2\n")
      ),
      Result(1, "r.txt: round 1: player 5 has no game\n1 problem\n", "")
    )
  }

  test("everyProblemIsReported") {
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map(
          "p.txt" -> P4,
          "r.txt" -> "1 1 2 1-0\n1 1 2 0-1\n1 2 2 1/2\n1 7 3 1-0\n1 3 4 1/2\n"
        )
      ),
      Result(
        1,
        "r.txt:2: player 1 is already paired in round 1\nr.txt:3: player 2 cannot play themselves\nr.txt:4: unknown player 7\n3 problems\n",
        ""
      )
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map(
          "p.txt" -> P4,
          "r.txt" -> "# a comment\n\n1 1 2 1-0\r\n1 3 4 1/2\r\n2 1 4 1-0\r\n2 2 3 1-0\r\n2 1 3 1-0\r\n"
        )
      ),
      Result(1, "r.txt:7: player 1 is already paired in round 2\n1 problem\n", "")
    )
  }
