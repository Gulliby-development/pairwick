package pairwick

final case class Failure(message: String, code: Int = 2) extends Exception(message)
