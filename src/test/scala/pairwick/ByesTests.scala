package pairwick

import pairwick.Fixtures.*

class ByesTests extends munit.FunSuite:
  private def byes(players: String, results: String): Result =
    Harness.run(List("byes", "p.txt", "r.txt"), Map("p.txt" -> players, "r.txt" -> results))

  test("noResultsYet") {
    assertEquals(byes(P4, ""), Result(0, "0 byes\nwithout a bye: 1 2 3 4\n", ""))
  }

  test("noByesInAnEvenTournament") {
    assertEquals(byes(P4, R4_ALL), Result(0, "0 byes\nwithout a bye: 1 2 3 4\n", ""))
  }

  test("listsEveryByeWithRoundAndName") {
    assertEquals(
      byes(P5, R5_3),
      Result(0, "1 5 Ed\n2 3 Cy\n3 2 Bob\n3 byes\nwithout a bye: 1 4\n", "")
    )
  }

  test("ordersByRoundWhateverTheOrderOfTheLines") {
    val shuffled = "2 3 bye\n2 1 4 1/2\n2 5 2 1-0\n1 1 2 1-0\n1 3 4 0-1\n1 5 bye\n"
    assertEquals(
      byes(P5, shuffled),
      Result(0, "1 5 Ed\n2 3 Cy\n2 byes\nwithout a bye: 1 2 4\n", "")
    )
  }

  test("everyPlayerHadOne") {
    val players = "1 Ann\n2 Bob\n3 Cy\n"
    val results = "1 1 2 1-0\n1 3 bye\n2 1 3 1-0\n2 2 bye\n3 2 3 1-0\n3 1 bye\n"
    assertEquals(
      byes(players, results),
      Result(0, "1 3 Cy\n2 2 Bob\n3 1 Ann\n3 byes\nwithout a bye: none\n", "")
    )
  }

  test("needsACleanCheckAndTwoArguments") {
    assertEquals(
      byes(P4, "1 1 2 1-0\n"),
      Result(2, "", "pairwick: r.txt: 2 problems, run pairwick check\n")
    )
    assertEquals(
      Harness.run(List("byes", "p.txt")),
      Result(2, "", "pairwick: byes: wrong number of arguments\n")
    )
  }
