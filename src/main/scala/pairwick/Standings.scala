package pairwick

object Standings:
  def halves(h: Int): String =
    s"${h / 2}.${if h % 2 == 0 then "0" else "5"}"

  def quarters(q: Int): String =
    val decimal = q % 4 match
      case 0 => "00"
      case 1 => "25"
      case 2 => "50"
      case 3 => "75"
    s"${q / 4}.$decimal"

  def render(players: Map[Int, String], accepted: Vector[Entry], rounds: Int): String =
    val scores = Scores.of(players, accepted)
    val ordered = players.keys.toVector.sortWith { (left, right) =>
      val leftScore =
        (scores.points(left), scores.buchholz(left), scores.sb(left), scores.wins(left))
      val rightScore =
        (scores.points(right), scores.buchholz(right), scores.sb(right), scores.wins(right))
      if leftScore != rightScore then Ordering.Tuple4[Int, Int, Int, Int].gt(leftScore, rightScore)
      else left < right
    }
    val rows = ordered.zipWithIndex.map { (id, index) =>
      val rank =
        if index > 0 then
          val previous = ordered(index - 1)
          val sameScore =
            scores.points(id) == scores.points(previous) &&
              scores.buchholz(id) == scores.buchholz(previous) &&
              scores.sb(id) == scores.sb(previous) &&
              scores.wins(id) == scores.wins(previous)
          if sameScore then
            val first = ordered
              .take(index)
              .find { candidate =>
                scores.points(candidate) == scores.points(id) &&
                scores.buchholz(candidate) == scores.buchholz(id) &&
                scores.sb(candidate) == scores.sb(id) &&
                scores.wins(candidate) == scores.wins(id)
              }
              .get
            val firstIndex = ordered.indexOf(first)
            (firstIndex + 1).toString
          else (index + 1).toString
        else "1"
      s"$rank $id ${halves(scores.points(id))} ${halves(scores.buchholz(id))} ${quarters(scores.sb(id))} ${scores.wins(id)} ${players(id)}"
    }
    (rows :+ s"players=${players.size} rounds=$rounds").mkString("", "\n", "\n")
