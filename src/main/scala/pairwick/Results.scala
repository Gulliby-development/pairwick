package pairwick

enum Outcome:
  case WhiteWin, BlackWin, Draw, Bye

/** A game preserves the listed player order in white and black = Some(second ID). A bye stores its
  * player in white, uses black = None, and has outcome Bye.
  */
final case class Entry(line: Int, round: Int, white: Int, black: Option[Int], outcome: Outcome)

object Results:
  private val gameLine = raw"([1-9][0-9]?) ([1-9][0-9]{0,2}) ([1-9][0-9]{0,2}) (1-0|0-1|1/2)".r
  private val byeLine = raw"([1-9][0-9]?) ([1-9][0-9]{0,2}) bye".r

  def read(source: Source, path: String): Vector[Entry] =
    val shown = source.shown(path)
    val entries = Vector.newBuilder[Entry]
    for (line, rawLine) <- Text.lines(shown, source.bytes(path)) do
      rawLine match
        case gameLine(round, white, black, result) =>
          val outcome = result match
            case "1-0" => Outcome.WhiteWin
            case "0-1" => Outcome.BlackWin
            case "1/2" => Outcome.Draw
          entries += Entry(line, round.toInt, white.toInt, Some(black.toInt), outcome)
        case byeLine(round, player) =>
          entries += Entry(line, round.toInt, player.toInt, None, Outcome.Bye)
        case _ =>
          throw Failure(
            s"$shown:$line: not a result line of the form ROUND WHITE BLACK RESULT or ROUND ID bye"
          )
    entries.result()
