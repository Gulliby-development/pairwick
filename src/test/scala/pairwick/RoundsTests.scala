package pairwick

class RoundsTests extends munit.FunSuite:
  test("ceilingOfTheLogarithm") {
    assertEquals(Harness.run(List("rounds", "2")), Result(0, "2 players: 1 round\n", ""))
    assertEquals(Harness.run(List("rounds", "3")), Result(0, "3 players: 2 rounds\n", ""))
    assertEquals(Harness.run(List("rounds", "4")), Result(0, "4 players: 2 rounds\n", ""))
    assertEquals(Harness.run(List("rounds", "5")), Result(0, "5 players: 3 rounds\n", ""))
    assertEquals(Harness.run(List("rounds", "8")), Result(0, "8 players: 3 rounds\n", ""))
    assertEquals(Harness.run(List("rounds", "9")), Result(0, "9 players: 4 rounds\n", ""))
    assertEquals(Harness.run(List("rounds", "16")), Result(0, "16 players: 4 rounds\n", ""))
    assertEquals(Harness.run(List("rounds", "17")), Result(0, "17 players: 5 rounds\n", ""))
    assertEquals(Harness.run(List("rounds", "999")), Result(0, "999 players: 10 rounds\n", ""))
  }

  test("badCounts") {
    assertEquals(
      Harness.run(List("rounds", "1")),
      Result(2, "", "pairwick: bad player count \"1\": expected an integer from 2 to 999\n")
    )
    assertEquals(
      Harness.run(List("rounds", "0")),
      Result(2, "", "pairwick: bad player count \"0\": expected an integer from 2 to 999\n")
    )
    assertEquals(
      Harness.run(List("rounds", "1000")),
      Result(2, "", "pairwick: bad player count \"1000\": expected an integer from 2 to 999\n")
    )
    assertEquals(
      Harness.run(List("rounds", "08")),
      Result(2, "", "pairwick: bad player count \"08\": expected an integer from 2 to 999\n")
    )
    assertEquals(
      Harness.run(List("rounds", "x")),
      Result(2, "", "pairwick: bad player count \"x\": expected an integer from 2 to 999\n")
    )
    assertEquals(
      Harness.run(List("rounds", "-3")),
      Result(2, "", "pairwick: unknown option \"-3\"\n")
    )
    assertEquals(
      Harness.run(List("rounds", "2.5")),
      Result(2, "", "pairwick: bad player count \"2.5\": expected an integer from 2 to 999\n")
    )
  }

  test("argumentCount") {
    assertEquals(
      Harness.run(List("rounds")),
      Result(2, "", "pairwick: rounds: wrong number of arguments\n")
    )
    assertEquals(
      Harness.run(List("rounds", "4", "5")),
      Result(2, "", "pairwick: rounds: wrong number of arguments\n")
    )
  }
