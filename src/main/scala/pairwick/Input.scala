package pairwick

final case class Source(read: String => Option[Array[Byte]], stdin: () => Array[Byte]):
  def shown(path: String): String =
    if path == "-" then "<stdin>" else path

  def bytes(path: String): Array[Byte] =
    if path == "-" then stdin()
    else read(path).getOrElse(throw Failure(s"$path: cannot read file"))
