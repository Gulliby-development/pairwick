package pairwick

import java.nio.charset.StandardCharsets.UTF_8

object Harness:
  def run(
      args: List[String],
      files: Map[String, String] = Map.empty,
      stdin: String = ""
  ): Result =
    Cli.run(args, path => files.get(path).map(_.getBytes(UTF_8)), () => stdin.getBytes(UTF_8))
