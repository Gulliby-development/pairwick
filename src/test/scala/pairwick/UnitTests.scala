package pairwick

class UnitTests extends munit.FunSuite:
  test("halfPoints") {
    assertEquals(Standings.halves(0), "0.0")
    assertEquals(Standings.halves(1), "0.5")
    assertEquals(Standings.halves(5), "2.5")
    assertEquals(Standings.halves(12), "6.0")
  }

  test("quarterPoints") {
    assertEquals(Standings.quarters(0), "0.00")
    assertEquals(Standings.quarters(1), "0.25")
    assertEquals(Standings.quarters(2), "0.50")
    assertEquals(Standings.quarters(7), "1.75")
    assertEquals(Standings.quarters(8), "2.00")
    assertEquals(Standings.quarters(41), "10.25")
  }

  test("firstMatching") {
    assertEquals(Matching.first(Vector(1, 2, 3, 4), Set.empty), Some(List((1, 2), (3, 4))))
    assertEquals(Matching.first(Vector(1, 2, 3, 4), Set(Set(1, 2))), Some(List((1, 3), (2, 4))))
    assertEquals(
      Matching.first(Vector(4, 2, 7, 1), Set(Set(4, 2), Set(7, 1))),
      Some(List((4, 7), (2, 1)))
    )
    assertEquals(Matching.first(Vector(1, 2, 3, 4), Set(Set(1, 2), Set(1, 3), Set(1, 4))), None)
    assertEquals(
      Matching.first(Vector(1, 2, 3, 4, 5, 6), Set(Set(1, 2), Set(3, 4))),
      Some(List((1, 3), (2, 4), (5, 6)))
    )
    assertEquals(Matching.first(Vector.empty, Set.empty), Some(Nil))
  }

  test("roundsFormula") {
    assertEquals(Rounds.render("2"), "2 players: 1 round\n")
    assertEquals(Rounds.render("65"), "65 players: 7 rounds\n")
    assertEquals(Rounds.render("64"), "64 players: 6 rounds\n")
  }
