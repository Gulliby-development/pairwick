package pairwick

import java.io.{IOException, InputStream, OutputStream}
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, InvalidPathException, Paths}

object Main:
  /** Runs one command line against real files and the given streams; returns the exit status. */
  def exec(args: Array[String], in: InputStream, out: OutputStream, err: OutputStream): Int =
    def read(path: String): Option[Array[Byte]] =
      try Some(Files.readAllBytes(Paths.get(path)))
      catch case _: IOException | _: InvalidPathException => None
    val result = Cli.run(args.toList, read, () => in.readAllBytes())
    out.write(result.out.getBytes(StandardCharsets.UTF_8))
    out.flush()
    err.write(result.err.getBytes(StandardCharsets.UTF_8))
    err.flush()
    result.exit

  def main(args: Array[String]): Unit =
    sys.exit(exec(args, System.in, System.out, System.err))
