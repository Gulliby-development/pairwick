package pairwick

import scala.util.control.NonFatal

final case class Result(exit: Int, out: String, err: String)

object Cli:
  val usage: String =
    "usage: pairwick COMMAND [ARGS]\n  standings PLAYERS RESULTS\n  pair PLAYERS RESULTS\n  check PLAYERS RESULTS\n  crosstable PLAYERS RESULTS\n  rounds COUNT\n"

  def run(
      args: List[String],
      read: String => Option[Array[Byte]],
      stdin: () => Array[Byte]
  ): Result =
    def error(message: String, code: Int = 2): Result =
      Result(code, "", "pairwick: " + message + "\n")

    def protect[A](body: => A): A =
      try body
      catch
        case failure: Failure => throw failure
        case NonFatal(_)      => throw Failure("internal error")

    try
      if args.isEmpty then Result(2, "", usage)
      else
        val command = args.head
        val operands = args.tail
        val known =
          Set("check", "standings", "pair", "crosstable", "rounds", "help", "-h", "--help")
        if !known.contains(command) then throw Failure("unknown command \"" + command + "\"")
        operands.find(arg => arg.startsWith("-") && arg != "-").foreach { option =>
          throw Failure("unknown option \"" + option + "\"")
        }

        val expected = command match
          case "rounds"                 => 1
          case "help" | "-h" | "--help" => 0
          case _                        => 2
        if operands.length != expected then throw Failure(command + ": wrong number of arguments")

        command match
          case "help" | "-h" | "--help" => Result(0, usage, "")
          case "rounds"                 => Result(0, protect(Rounds.render(operands.head)), "")
          case _ =>
            val playersPath = operands.head
            val resultsPath = operands(1)
            if playersPath == "-" && resultsPath == "-" then
              throw Failure("only one of PLAYERS and RESULTS can be -")

            val source = Source(
              path =>
                try read(path)
                catch
                  case failure: Failure => throw failure
                  case NonFatal(_)      => throw Failure(path + ": cannot read file"),
              () =>
                try stdin()
                catch
                  case failure: Failure => throw failure
                  case NonFatal(_)      => throw Failure("<stdin>: cannot read file")
            )
            val players = protect(Players.read(source, playersPath))
            val entries = protect(Results.read(source, resultsPath))
            val checked = protect(Rules.check(players, source.shown(resultsPath), entries))

            command match
              case "check" =>
                if checked.problems.nonEmpty then
                  Result(
                    1,
                    (checked.problems :+ Text.plural(checked.problems.length, "problem"))
                      .mkString("", "\n", "\n"),
                    ""
                  )
                else
                  val games = checked.accepted.count(_.black.nonEmpty)
                  val byes = checked.accepted.count(_.outcome == Outcome.Bye)
                  Result(
                    0,
                    "ok: " + Text.plural(checked.rounds, "round") + ", " +
                      Text.plural(games, "game") + ", " + Text.plural(byes, "bye") + "\n",
                    ""
                  )
              case _ =>
                if checked.problems.nonEmpty then
                  throw Failure(
                    source.shown(resultsPath) + ": " +
                      Text.plural(checked.problems.length, "problem") + ", run pairwick check"
                  )
                val output = command match
                  case "standings" =>
                    Standings.render(players, checked.accepted, checked.rounds)
                  case "crosstable" =>
                    Crosstable.render(players, checked.accepted, checked.rounds)
                  case "pair" => Pairing.render(players, checked.accepted, checked.rounds)
                Result(0, output, "")
    catch
      case failure: Failure => error(failure.message, failure.code)
      case NonFatal(_)      => error("internal error")
