package pairwick

object Byes:
  def render(players: Map[Int, String], accepted: Vector[Entry]): String =
    val awarded = accepted
      .filter(_.outcome == Outcome.Bye)
      .sortBy(entry => (entry.round, entry.white))
    val lines = awarded.map(entry => s"${entry.round} ${entry.white} ${players(entry.white)}")
    val without = players.keys.toVector.sorted.filterNot(id => awarded.exists(_.white == id))
    val tail = if without.isEmpty then "none" else without.mkString(" ")
    (lines :+ Text.plural(awarded.length, "bye") :+ s"without a bye: $tail")
      .mkString("", "\n", "\n")
