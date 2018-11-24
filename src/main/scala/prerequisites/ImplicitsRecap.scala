package prerequisites

import scala.concurrent.Future

object ImplicitsRecap extends App {
  // implicits
  implicit val timeout = 3000
  def setTimeout(f: () => Unit)(implicit timeout: Int) = f()
  setTimeout(() => println("timeout")) // extra parameter list omitted
  // implicit conversions
  //  conversion can be done through -> 1)  implicit defs
  case class Person(name: String) {
    def greet = s"Hi, my name is $name"
  }
  implicit def fromStringToPerson(string: String): Person = Person(string)
  val implicitDefsExample = "Any String but this that this is Person class ".greet
  println(implicitDefsExample)
  //  conversion can be done through -> 2)  implicit classes
  implicit class Dog(name: String) {
    def bark = println("bark! ")
  }
  "Lassie".bark
  // what compiler do -> new Dog("Lassie").bark - automatically done by the compiler

  //organize
  // local scope
  implicit val inverseOrdering: Ordering[Int ] =Ordering.fromLessThan(_ > _ )
  List(1,2,3).sorted // List(3,2,1)

  // imported scope
  import scala.concurrent.ExecutionContext.Implicits.global
  val future = Future{
    println("hello, future")
  }

  object Person {
    implicit val personOrdering : Ordering[Person] = Ordering.fromLessThan((a,b) => a.name.compareTo(b.name) < 0 )

  }

   // List(Person("Bob")) // todo need to finish

}
