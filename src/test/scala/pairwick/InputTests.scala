package pairwick

import pairwick.Fixtures.*

class InputTests extends munit.FunSuite:
  test("playerFileSize") {
    assertEquals(
      Harness.run(List("check", "p.txt", "r.txt"), Map("p.txt" -> "1 Ann\n2 Bob\n", "r.txt" -> "")),
      Result(0, "ok: 0 rounds, 0 games, 0 byes\n", "")
    )
    assertEquals(
      Harness.run(List("check", "p.txt", "r.txt"), Map("p.txt" -> "1 Ann\n", "r.txt" -> "")),
      Result(2, "", "pairwick: p.txt: need at least 2 players\n")
    )
    assertEquals(
      Harness.run(List("check", "p.txt", "r.txt"), Map("p.txt" -> "", "r.txt" -> "")),
      Result(2, "", "pairwick: p.txt: need at least 2 players\n")
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> "# only a comment\n", "r.txt" -> "")
      ),
      Result(2, "", "pairwick: p.txt: need at least 2 players\n")
    )
  }

  test("playerLines") {
    assertEquals(
      Harness.run(
        List("standings", "p.txt", "r.txt"),
        Map("p.txt" -> "1 Ann\n2 Bob\n3 Cy Jr.\n4 O'Neil\n5 A-B\n", "r.txt" -> "")
      ),
      Result(
        0,
        "1 1 0.0 0.0 0.00 0 Ann\n1 2 0.0 0.0 0.00 0 Bob\n1 3 0.0 0.0 0.00 0 Cy Jr.\n1 4 0.0 0.0 0.00 0 O'Neil\n1 5 0.0 0.0 0.00 0 A-B\nplayers=5 rounds=0\n",
        ""
      )
    )
    assertEquals(
      Harness.run(
        List("standings", "p.txt", "r.txt"),
        Map("p.txt" -> "1 Ann\r\n2 Bob\r\n", "r.txt" -> "")
      ),
      Result(
        0,
        "1 1 0.0 0.0 0.00 0 Ann\n1 2 0.0 0.0 0.00 0 Bob\nplayers=2 rounds=0\n",
        ""
      )
    )
    assertEquals(
      Harness.run(
        List("standings", "p.txt", "r.txt"),
        Map("p.txt" -> "\n# list\n\n7 Ann\n12 Bob\n", "r.txt" -> "")
      ),
      Result(
        0,
        "1 7 0.0 0.0 0.00 0 Ann\n1 12 0.0 0.0 0.00 0 Bob\nplayers=2 rounds=0\n",
        ""
      )
    )
  }

  test("badPlayerLines") {
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> "1Ann\n2 Bob\n", "r.txt" -> "")
      ),
      Result(2, "", "pairwick: p.txt:1: not a player line of the form ID NAME\n")
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> "1  Ann\n2 Bob\n", "r.txt" -> "")
      ),
      Result(2, "", "pairwick: p.txt:1: not a player line of the form ID NAME\n")
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> "01 Ann\n2 Bob\n", "r.txt" -> "")
      ),
      Result(2, "", "pairwick: p.txt:1: not a player line of the form ID NAME\n")
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> "1 Ann\n1 Bob\n", "r.txt" -> "")
      ),
      Result(2, "", "pairwick: p.txt:2: duplicate player ID\n")
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> "1 ann1\n2 Bob\n", "r.txt" -> "")
      ),
      Result(0, "ok: 0 rounds, 0 games, 0 byes\n", "")
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> "1 Ann \n2 Bob\n", "r.txt" -> "")
      ),
      Result(2, "", "pairwick: p.txt:1: not a player line of the form ID NAME\n")
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> "1 1Ann\n2 Bob\n", "r.txt" -> "")
      ),
      Result(2, "", "pairwick: p.txt:1: not a player line of the form ID NAME\n")
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> "0 Ann\n2 Bob\n", "r.txt" -> "")
      ),
      Result(2, "", "pairwick: p.txt:1: not a player line of the form ID NAME\n")
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> "1000 Ann\n2 Bob\n", "r.txt" -> "")
      ),
      Result(2, "", "pairwick: p.txt:1: not a player line of the form ID NAME\n")
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> "1 Ann\n\n# c\nx Bob\n", "r.txt" -> "")
      ),
      Result(2, "", "pairwick: p.txt:4: not a player line of the form ID NAME\n")
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> "1 A\tn\n2 Bob\n", "r.txt" -> "")
      ),
      Result(2, "", "pairwick: p.txt:1: not a player line of the form ID NAME\n")
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> "1 Ann\n2 B\u00f6b\n", "r.txt" -> "")
      ),
      Result(2, "", "pairwick: p.txt:2: not a player line of the form ID NAME\n")
    )
  }

  test("resultLines") {
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> P4, "r.txt" -> "1 1 2 1-0\n1 3 4 1/2\n")
      ),
      Result(0, "ok: 1 round, 2 games, 0 byes\n", "")
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> P4, "r.txt" -> "1 1 2 1-0\r\n1 3 4 1/2\n")
      ),
      Result(0, "ok: 1 round, 2 games, 0 byes\n", "")
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> P4, "r.txt" -> "# c\n\n1 1 2 1-0\n1 3 4 1/2\n")
      ),
      Result(0, "ok: 1 round, 2 games, 0 byes\n", "")
    )
  }

  test("badResultLines") {
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> P4, "r.txt" -> "1 1 2 2-0\n")
      ),
      Result(
        2,
        "",
        "pairwick: r.txt:1: not a result line of the form ROUND WHITE BLACK RESULT or ROUND ID bye\n"
      )
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> P4, "r.txt" -> "0 1 2 1-0\n")
      ),
      Result(
        2,
        "",
        "pairwick: r.txt:1: not a result line of the form ROUND WHITE BLACK RESULT or ROUND ID bye\n"
      )
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> P4, "r.txt" -> "100 1 2 1-0\n")
      ),
      Result(
        2,
        "",
        "pairwick: r.txt:1: not a result line of the form ROUND WHITE BLACK RESULT or ROUND ID bye\n"
      )
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> P4, "r.txt" -> "1 1 2\n")
      ),
      Result(
        2,
        "",
        "pairwick: r.txt:1: not a result line of the form ROUND WHITE BLACK RESULT or ROUND ID bye\n"
      )
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> P4, "r.txt" -> "1 1 2 1-0 x\n")
      ),
      Result(
        2,
        "",
        "pairwick: r.txt:1: not a result line of the form ROUND WHITE BLACK RESULT or ROUND ID bye\n"
      )
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> P4, "r.txt" -> "1 1 2 1/2-1/2\n")
      ),
      Result(
        2,
        "",
        "pairwick: r.txt:1: not a result line of the form ROUND WHITE BLACK RESULT or ROUND ID bye\n"
      )
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> P4, "r.txt" -> "1 1 bye bye\n")
      ),
      Result(
        2,
        "",
        "pairwick: r.txt:1: not a result line of the form ROUND WHITE BLACK RESULT or ROUND ID bye\n"
      )
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> P4, "r.txt" -> "1 1 bye \n")
      ),
      Result(
        2,
        "",
        "pairwick: r.txt:1: not a result line of the form ROUND WHITE BLACK RESULT or ROUND ID bye\n"
      )
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> P4, "r.txt" -> "a 1 2 1-0\n")
      ),
      Result(
        2,
        "",
        "pairwick: r.txt:1: not a result line of the form ROUND WHITE BLACK RESULT or ROUND ID bye\n"
      )
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> P4, "r.txt" -> "1 01 2 1-0\n")
      ),
      Result(
        2,
        "",
        "pairwick: r.txt:1: not a result line of the form ROUND WHITE BLACK RESULT or ROUND ID bye\n"
      )
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> P4, "r.txt" -> "1 1 2 1-0\n\n1 3 x 1/2\n")
      ),
      Result(
        2,
        "",
        "pairwick: r.txt:3: not a result line of the form ROUND WHITE BLACK RESULT or ROUND ID bye\n"
      )
    )
    assertEquals(
      Harness.run(
        List("check", "p.txt", "r.txt"),
        Map("p.txt" -> P4, "r.txt" -> "1 1 2 0.5\n")
      ),
      Result(
        2,
        "",
        "pairwick: r.txt:1: not a result line of the form ROUND WHITE BLACK RESULT or ROUND ID bye\n"
      )
    )
  }

  test("missingFiles") {
    assertEquals(
      Harness.run(List("check", "p.txt", "r.txt"), Map("r.txt" -> "")),
      Result(2, "", "pairwick: p.txt: cannot read file\n")
    )
    assertEquals(
      Harness.run(List("check", "p.txt", "r.txt"), Map("p.txt" -> P4)),
      Result(2, "", "pairwick: r.txt: cannot read file\n")
    )
  }
