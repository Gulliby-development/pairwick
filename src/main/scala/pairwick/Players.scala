package pairwick

object Players:
  private val PlayerLine =
    raw"([1-9][0-9]{0,2}) ([A-Za-z](?:[A-Za-z0-9 .'-]{0,28}[A-Za-z0-9.'-])?)".r

  def read(source: Source, path: String): Map[Int, String] =
    val shown = source.shown(path)
    val players = Text.lines(shown, source.bytes(path)).foldLeft(Map.empty[Int, String]) {
      case (found, (line, raw)) =>
        raw match
          case PlayerLine(idText, name) =>
            val id = idText.toInt
            if found.contains(id) then throw Failure(s"$shown:$line: duplicate player ID")
            found.updated(id, name)
          case _ =>
            throw Failure(s"$shown:$line: not a player line of the form ID NAME")
    }
    if players.size < 2 then throw Failure(s"$shown: need at least 2 players")
    players
