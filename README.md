<p align="center"><img src="docs/logo.svg" alt="pairwick logo" width="96" height="96"></p>

# pairwick

[![CI](https://github.com/Gulliby-development/pairwick/actions/workflows/ci.yml/badge.svg)](https://github.com/Gulliby-development/pairwick/actions/workflows/ci.yml) [![License: MIT](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

pairwick is an offline, deterministic command-line helper for chess clubs,
board-game evenings, and school tournaments. It validates entered results,
prints standings and crosstables, and proposes the next round. It is not a
complete implementation of any federation's Swiss rules. It uses only the
Scala and Java standard libraries at runtime; all scoring uses exact integer
half-points and quarter-points.

## Install and launch

Install a JDK supported by Scala 3 and sbt 2.0.11, and install sbt using the
instructions for your operating system at [sbt's installation guide](https://www.scala-sbt.org/download/).
The project pins Scala 3.3.7 and sbt 2.0.11; sbt selects these versions from
the project files. Windows, macOS, and Linux are supported. The first sbt
invocation may download the launcher, compiler, and test dependencies; after
those are cached, running pairwick itself does not need network access.

From the project root, pass the application arguments after the sbt `run`
task in one quoted command. For example:

```text
sbt --server -batch "run rounds 9"
```

sbt prints its own startup and task messages in addition to application output.
The example above was run against this project; the application portion is:

```text
9 players: 4 rounds
```

The process status is sbt's status for the run task; pairwick's own command
statuses are 0, 1, or 2 as described below. In Windows Command Prompt, use
`sbt.bat` in place of `sbt` in the commands on this page. No Unix-only
installation or runtime utility is required.

## Input files

A players file has one `ID NAME` record per player. IDs are decimal 1–999,
without leading zeroes. A name is 1–30 ASCII characters: it starts with a
letter, then contains only letters, digits, spaces, periods, apostrophes, or
hyphens, and cannot end in a space. IDs must be unique; names may repeat.
At least two players are required.

A results file has one game record, `ROUND WHITE BLACK RESULT`, or bye record,
`ROUND ID bye`, per line. Fields have exactly one separating space. Rounds
are 1–99 and IDs use the players-file form. Results are `1-0` (white wins),
`0-1` (black wins), or `1/2` (draw). A bye awards a free win. “White” and
“black” just identify the listed players; they do not affect pairing.

Files are decoded as strict UTF-8. One initial byte-order mark is ignored.
Lines may end in LF or CRLF; one trailing CR is removed from each physical
line. Blank lines and lines beginning with `#` are ignored. Diagnostics count
physical lines, including blank and comment lines, starting at 1; a final line
without a line ending is still read.

Use `-` in place of either path to read that input from standard input. Only
one input may be `-`; its diagnostic path is `<stdin>`. For example, in a
shell that supports pipelines, supply player data through standard input while
keeping results in a file:

```text
sbt --server -batch "run check - results.txt"
```

Provide the player-file bytes to that command's standard input. Shell pipeline
and redirection syntax differs between PowerShell, Command Prompt, and
POSIX-compatible shells; the pairwick argument itself is portable.

## Commands

All examples below are run from the project root. To make the examples
reproducible, save this player file as `p.txt`:

```text
1 Ann
2 Bob
3 Cy
4 Di
```

For a no-results starting point, make `r.txt` an empty file. The following
two-round results file is another example:

```text
1 1 2 1-0
1 3 4 1/2
2 1 3 0-1
2 2 4 1-0
```

- `check PLAYERS RESULTS` validates all results and round completeness.
  With no results, `sbt --server -batch "run check p.txt r.txt"` prints:

  ```text
  ok: 0 rounds, 0 games, 0 byes
  ```

  Problems are printed one per line followed by a problem count; for example,
  an incomplete first round is reported as `PATH: round R: player ID has no game`.
- `standings PLAYERS RESULTS` requires a clean check. With the two-round
  example above, `sbt --server -batch "run standings p.txt r.txt"` prints:

  ```text
  1 3 1.5 1.5 1.25 1 Cy
  2 1 1.0 2.5 1.00 1 Ann
  3 2 1.0 1.5 0.50 1 Bob
  4 4 0.5 2.5 0.75 0 Di
  players=4 rounds=2
  ```
- `pair PLAYERS RESULTS` requires a clean check. With the same two-round
  example, `sbt --server -batch "run pair p.txt r.txt"` prints:

  ```text
  round 3
  1 3 2
  2 1 4
  ```
- `crosstable PLAYERS RESULTS` requires a clean check and lists players in
  numeric ID order. A cell is `W<opponent>`, `D<opponent>`, `L<opponent>`,
  or `B` for a bye. For example, with players 1–8 and the first-round sample
  below, use `sbt --server -batch "run crosstable p8.txt r8.txt"`:

  ```text
  1 Ann
  2 Bob
  3 Cy
  4 Di
  5 Ed
  6 Flo
  7 Gus
  8 Hal
  ```

  ```text
  1 1 2 1-0
  1 3 4 1/2
  1 5 6 0-1
  1 7 8 1-0
  ```

  The crosstable output is:

  ```text
  ID PTS R1
  1 1.0 W2
  2 0.0 L1
  3 0.5 D4
  4 0.5 D3
  5 0.0 L6
  6 1.0 W5
  7 1.0 W8
  8 0.0 L7
  ```
- `rounds COUNT` accepts one to three decimal digits, with no leading zero,
  and a value from 2 through 999. It gives the recommended number of rounds:
  the least R for which `2^R >= COUNT`. For example,
  `sbt --server -batch "run rounds 9"` prints `9 players: 4 rounds`. This
  advice does not guarantee that every round can be paired.
- `help`, `-h`, and `--help` print the usage text to standard output and
  return 0. For example, `sbt --server -batch "run --help"` prints:

  ```text
  usage: pairwick COMMAND [ARGS]
    standings PLAYERS RESULTS
    pair PLAYERS RESULTS
    check PLAYERS RESULTS
    crosstable PLAYERS RESULTS
    rounds COUNT
  ```

  Invoking pairwick with no arguments prints usage to standard error and
  returns 2.

The crosstable example's output and the standings, pairing, and empty-check
outputs above are examples supplied by the project request. The rounds output
was captured by running the example against the implementation.

## Validation, scoring, and pairing

The command name is validated before options; options are validated before
argument count. An argument beginning with `-` is an unknown option unless
it is exactly `-`. Inputs are read in order: the players file, then results,
then tournament rules are checked. Thus a players-file error takes precedence
over a results-file error. A result line is tested against rules in order:
self-play; unknown players (white first); already paired in that round (white
first); then rematch. Bye lines check unknown player, prior participation in
the round, and a previous bye. The first applicable error is reported and that
line is excluded from accepted history. The highest round written—even on a
rejected line—sets the round count. Missing rounds and then missing players
are reported after line-specific problems.

Reports and pairing require a clean check. If results have problems, these
commands print an error to standard error telling you to run `pairwick check`;
they do not print a partial report. `check` writes diagnostics and its count
to standard output.

Scoring is exact: a game win is 2 half-points, a draw 1, a loss 0, and a bye
2. Standings are ordered by points, Buchholz, Sonneborn–Berger, and game wins,
all descending, then numeric ID ascending. Players equal on those four fields
share the rank of the first tied row. Buchholz is the sum of final points of
opponents played; byes add nothing. Sonneborn–Berger is the sum of a player's
game points multiplied by the opponent's final points. Half-points print with
one decimal place; quarter-points with two.

Pairing orders players by points descending and ID ascending. In an odd roster,
the last player in that order who has never received a bye gets the bye. Pairwick
then finds the lexicographically first complete matching in order that
avoids all previous opponents, backtracking when a choice leaves no complete
matching. It does not try alternate bye recipients to rescue an impossible
matching. It never schedules a rematch or a second bye, but a legal next round
is not guaranteed: pairwick reports when no eligible bye or no complete
matching exists. Backtracking over candidate matchings has exponential
worst-case search and may be slow for large rosters or difficult histories.
It also refuses to create round 100.

Application output is UTF-8 with LF line endings. Errors use standard error and
the `pairwick: ...` prefix. Exit status 0 means success, 1 means tournament
problems or no possible pairing/bye, and 2 means a usage or input error. Under
sbt, startup messages and its process status are separate from pairwick's
output and command status.

## Development

Run the project checks from its root:

```text
sbt --server -batch scalafmtCheckAll
sbt --server -batch scalafmtAll
sbt --server -batch testFull
```

`scalafmtCheckAll` checks formatting; `scalafmtAll` applies the formatter.
`testFull` runs the complete suite every time. sbt may cache the ordinary
`test` task and report zero tests on a later unchanged run, so use
`testFull` when you need the full suite. On Windows, spell the launcher
`sbt.bat` (for example, `sbt.bat --server -batch testFull`).

## License

pairwick is distributed under the MIT License; see [LICENSE](LICENSE).

---

Built with BAKED
