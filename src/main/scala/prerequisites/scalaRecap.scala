package prerequisites

object scalaRecap extends App {
  val anonymousIncrementer = (x: Int) => x + 1
  println(anonymousIncrementer(32))
  val pairs_for_comprehension = for {
    num <- List(1, 2, 3, 4)
    char <- List('a', 'b', 'c', 'd')
  } yield num + " - " + char
  // identical to
  val pairs = List(1, 2, 3, 4).flatMap(num => List('a', 'b', 'c', 'd').map(char => num + " - " + char))
  println(pairs_for_comprehension + "\n" + pairs)
  // lifting parcial function
  val partialFunction: PartialFunction[Int, Int] = {
    case 1 => 12
    case 2 => 23
    case 5 => 999
  }
  val lift = partialFunction.lift // total function Int => Option[Int]
  val pfChain = partialFunction.orElse[Int, Int] {
    case 60 => 9000
  }
  pfChain(5) /// 999
  pfChain(60) /// 9000
  // pfChain(543) /// throw a MatchError

  // type aliases
  type ReceiveFunction_this_name_is_default = PartialFunction[Any,Unit]
  def receive : ReceiveFunction_this_name_is_default = {
    case 1 => println("hello")
    case _ => println("confused...")
  }

  val test = receive(1)


}
