package pairwick

class MatchingTests extends munit.FunSuite:
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
