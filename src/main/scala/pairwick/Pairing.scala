package pairwick

object Pairing:
  def render(players: Map[Int, String], accepted: Vector[Entry], rounds: Int): String =
    if rounds == 99 then throw Failure("round limit of 99 reached")

    val nextRound = rounds + 1
    val scores = Scores.of(players, accepted)
    val order = players.keys.toVector.sortWith { (left, right) =>
      val leftPoints = scores.points(left)
      val rightPoints = scores.points(right)
      if leftPoints != rightPoints then leftPoints > rightPoints
      else left < right
    }
    val byeRecipients = accepted.collect {
      case entry if entry.outcome == Outcome.Bye => entry.white
    }.toSet
    val met = accepted.collect {
      case entry if entry.black.nonEmpty => Set(entry.white, entry.black.get)
    }.toSet

    val (pairingOrder, bye) =
      if order.length % 2 == 0 then (order, None)
      else
        val selected = order.reverseIterator.find(id => !byeRecipients.contains(id)).getOrElse {
          throw Failure(s"no player can take the bye in round $nextRound", 1)
        }
        (order.filterNot(_ == selected), Some(selected))

    val pairs = Matching.first(pairingOrder, met).getOrElse {
      throw Failure(s"no pairing exists for round $nextRound", 1)
    }
    val rows = pairs.zipWithIndex.map { case ((first, second), index) =>
      s"${index + 1} $first $second"
    }
    val byeRow = bye.map(id => s"bye $id").toList
    (List(s"round $nextRound") ++ rows ++ byeRow).mkString("", "\n", "\n")
