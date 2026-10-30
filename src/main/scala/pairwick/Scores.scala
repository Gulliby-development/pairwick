package pairwick

final case class Scores(
    points: Map[Int, Int],
    wins: Map[Int, Int],
    buchholz: Map[Int, Int],
    sb: Map[Int, Int]
)

object Scores:
  def of(players: Map[Int, String], accepted: Vector[Entry]): Scores =
    val initial = players.keysIterator.map(_ -> 0).toMap
    val (points, wins) = accepted.foldLeft((initial, initial)) { case ((pts, ws), entry) =>
      entry.outcome match
        case Outcome.Bye =>
          (pts.updated(entry.white, pts(entry.white) + 2), ws)
        case Outcome.WhiteWin =>
          (
            pts.updated(entry.white, pts(entry.white) + 2),
            ws.updated(entry.white, ws(entry.white) + 1)
          )
        case Outcome.BlackWin =>
          val black = entry.black.get
          (pts.updated(black, pts(black) + 2), ws.updated(black, ws(black) + 1))
        case Outcome.Draw =>
          val black = entry.black.get
          (pts.updated(entry.white, pts(entry.white) + 1).updated(black, pts(black) + 1), ws)
    }
    val (buchholz, sb) = accepted.foldLeft((initial, initial)) { case ((buch, sonneborn), entry) =>
      entry.black match
        case None => (buch, sonneborn)
        case Some(black) =>
          val whitePoints = entry.outcome match
            case Outcome.WhiteWin => 2
            case Outcome.BlackWin => 0
            case Outcome.Draw     => 1
            case Outcome.Bye      => 0
          val blackPoints = entry.outcome match
            case Outcome.WhiteWin => 0
            case Outcome.BlackWin => 2
            case Outcome.Draw     => 1
            case Outcome.Bye      => 0
          val nextBuch = buch
            .updated(entry.white, buch(entry.white) + points(black))
            .updated(black, buch(black) + points(entry.white))
          val nextSb = sonneborn
            .updated(entry.white, sonneborn(entry.white) + whitePoints * points(black))
            .updated(black, sonneborn(black) + blackPoints * points(entry.white))
          (nextBuch, nextSb)
    }
    Scores(points, wins, buchholz, sb)
