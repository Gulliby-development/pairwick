package pairwick

object Matching:
  def first(
      order: Vector[Int],
      met: Set[Set[Int]]
  ): Option[List[(Int, Int)]] =
    def search(remaining: Vector[Int]): Option[List[(Int, Int)]] =
      if remaining.isEmpty then Some(Nil)
      else if remaining.length % 2 != 0 then None
      else
        val player = remaining.head
        remaining.indices
          .drop(1)
          .iterator
          .flatMap { candidateIndex =>
            val candidate = remaining(candidateIndex)
            if met.contains(Set(player, candidate)) then Iterator.empty
            else
              val rest = remaining.tail.patch(candidateIndex - 1, Vector.empty, 1)
              search(rest).iterator.map(pairs => (player, candidate) :: pairs)
          }
          .take(1)
          .toList
          .headOption

    search(order)
