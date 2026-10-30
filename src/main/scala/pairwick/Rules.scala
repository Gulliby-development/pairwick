package pairwick

final case class Checked(accepted: Vector[Entry], problems: Vector[String], rounds: Int)

object Rules:
  def check(players: Map[Int, String], shown: String, entries: Vector[Entry]): Checked =
    val accepted = Vector.newBuilder[Entry]
    val problems = Vector.newBuilder[String]
    val pairedByRound = scala.collection.mutable.Map.empty[Int, Set[Int]]
    val metInRound = scala.collection.mutable.Map.empty[(Int, Int), Int]
    val firstByeRound = scala.collection.mutable.Map.empty[Int, Int]
    var highestRound = 0

    def paired(round: Int): Set[Int] = pairedByRound.getOrElse(round, Set.empty)
    def pairKey(first: Int, second: Int): (Int, Int) =
      if first < second then (first, second) else (second, first)

    for entry <- entries do
      if entry.round > highestRound then highestRound = entry.round
      val prefix = s"$shown:${entry.line}:"
      entry.outcome match
        case Outcome.Bye =>
          val player = entry.white
          if !players.contains(player) then problems += s"$prefix unknown player $player"
          else if paired(entry.round).contains(player) then
            problems += s"$prefix player $player is already paired in round ${entry.round}"
          else if firstByeRound.contains(player) then
            problems +=
              s"$prefix player $player already had a bye in round ${firstByeRound(player)}"
          else
            accepted += entry
            pairedByRound(entry.round) = paired(entry.round) + player
            firstByeRound(player) = entry.round
        case _ =>
          val white = entry.white
          val black = entry.black.get
          if white == black then problems += s"$prefix player $white cannot play themselves"
          else if !players.contains(white) then problems += s"$prefix unknown player $white"
          else if !players.contains(black) then problems += s"$prefix unknown player $black"
          else if paired(entry.round).contains(white) then
            problems += s"$prefix player $white is already paired in round ${entry.round}"
          else if paired(entry.round).contains(black) then
            problems += s"$prefix player $black is already paired in round ${entry.round}"
          else
            val key = pairKey(white, black)
            metInRound.get(key) match
              case Some(round) =>
                problems += s"$prefix players $white and $black already met in round $round"
              case None =>
                accepted += entry
                pairedByRound(entry.round) = paired(entry.round) + white + black
                metInRound(key) = entry.round

    val acceptedEntries = accepted.result()
    val lineProblems = problems.result()
    val completeness = Vector.newBuilder[String]
    val playersInOrder = players.keys.toVector.sorted
    var round = 1
    while round <= highestRound do
      val played = paired(round)
      if played.isEmpty then completeness += s"$shown: round $round has no results"
      else
        playersInOrder.iterator.filterNot(played.contains).foreach { player =>
          completeness += s"$shown: round $round: player $player has no game"
        }
      round += 1

    Checked(acceptedEntries, lineProblems ++ completeness.result(), highestRound)
