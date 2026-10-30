package pairwick

object Rounds:
  // A valid count is a nonempty ASCII decimal string with no leading zero and value 2..999.
  // Validate every character before conversion so malformed input is echoed unchanged in Failure.
  def render(text: String): String =
    var count = 0
    var valid = text.nonEmpty && text.length <= 3 && text.head != '0'
    var index = 0
    while valid && index < text.length do
      val digit = text.charAt(index)
      if digit < '0' || digit > '9' then valid = false
      else count = count * 10 + (digit - '0')
      index += 1

    if !valid || count < 2 || count > 999 then
      throw Failure(s"bad player count \"$text\": expected an integer from 2 to 999")

    var rounds = 0
    var capacity = 1
    while capacity < count do
      capacity *= 2
      rounds += 1

    s"${count} players: ${Text.plural(rounds, "round")}\n"
