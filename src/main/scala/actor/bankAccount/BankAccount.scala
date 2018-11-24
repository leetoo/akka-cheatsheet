package actor.bankAccount


import actor.bankAccount.BankAccount.Person.LiveTheLife
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

/**
  * 1. a Counter actor
  *  - Increment
  *  - Decrement
  *  - Print
  *  2. a Bank Account as an actor
  * receives
  *   - Deposit an amount
  *   - Withdraw an amount
  *   - Statement
  * replies with
  *   - Success
  *   - Failure
  *
  * interact with some other kind of actor
  */
object BankAccount extends App {
  // TERM:  DOMAIN of the counter
  object BankAccount { //TERM : companion object for out actor
    case class Deposit(amount: Int)
    case class Withdraw(amount : Int )
    case object Statement

    case class TransactionSuccess(message :String )
    case class TransactionFailure (reason: String )
  }
  class BankAccount extends Actor {
    // todo посмотреть делают ли так ребята из lightbend ( best practice ). имеется ввиду создают компанион object изаписывают все мессаджи туда
      import BankAccount._
    var funds = 0
    override def receive: Receive = {
      case Deposit(amount) =>
        if ( amount < 0 ) sender() ! TransactionFailure("invalid deposit amount")
        else {

          funds += amount
          sender() ! TransactionSuccess(s"successfully deposited $amount")
        }
      case Withdraw(amount) =>
        if ( amount < 0 ) sender() ! TransactionFailure("invalid withdraw amount")
        else if (amount > funds) sender() ! TransactionFailure("insufficient funds ")
        else {

          funds -= amount
          sender() ! TransactionSuccess(s"successfully withdraw $amount")
        }
      case Statement => sender() ! s"Your balance is $funds"
    }
  }

  object Person {
    case class LiveTheLife(account: ActorRef)
  }
  class Person extends Actor {
    import Person._
    import BankAccount._
    override def receive: Receive = {
      case LiveTheLife(account ) =>
        account ! Deposit(10000)
        account ! Withdraw(90000)
        account ! Withdraw(500)
        account ! Statement
      case message => println(message.toString)
    }
  }

  val system = ActorSystem("firstActorSystem")
  val account = system.actorOf(Props[BankAccount], "myBankAccount")
  val person = system.actorOf(Props[Person], "billionaire")

  person ! LiveTheLife(account)



}
