package pairwick

object Crosstable:
  def render(players: Map[Int, String], accepted: Vector[Entry], rounds: Int): String =
    val scores = Scores.of(players, accepted)
    val cells = accepted.flatMap { entry =>
      entry.black match
        case None => Vector((entry.white, entry.round) -> "B")
        case Some(black) =>
          val (whiteResult, blackResult) = entry.outcome match
            case Outcome.WhiteWin => ("W", "L")
            case Outcome.BlackWin => ("L", "W")
            case Outcome.Draw     => ("D", "D")
            case Outcome.Bye      => ("B", "B")
          Vector(
            (entry.white, entry.round) -> s"$whiteResult$black",
            (black, entry.round) -> s"$blackResult${entry.white}"
          )
    }.toMap
    val header = ("ID" +: "PTS" +: (1 to rounds).map(round => s"R$round")).mkString(" ")
    val rows = players.keys.toVector.sorted.map { id =>
      val roundCells = (1 to rounds).map(round => cells.getOrElse((id, round), ""))
      (Vector(id.toString, Standings.halves(scores.points(id))) ++ roundCells).mkString(" ")
    }
    (header +: rows).mkString("", "\n", "\n")
