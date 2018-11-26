package actor.child

import akka.actor.{Actor, ActorSystem, Props}

object NaiveImprovedBankAccount extends App{

  object NaiveBankAccount {
    case class Deposit(amount: Int)
    case class Withdraw(amount: Int)
    case object InitializeAccount
  }
  class NaiveBankAccount extends Actor {
    import CreditCard._
    import NaiveBankAccount._

    var amount = 0
    override def receive: Receive = {
      case InitializeAccount =>
        val creditCardRef = context.actorOf(Props[CreditCard], "card")
        creditCardRef ! AttachToAccount(this) // !!
      case Deposit(funds ) => deposit(funds )
      case Withdraw(funds ) => withdraw(funds )
    }

    def deposit(funds: Int ) ={
      println(s"${self.path} depositing $funds on top of $amount")
      amount += funds
    }
    def withdraw(funds : Int ) = {
      println(s"${self.path} withdrawing $funds from $amount")
      amount -= funds
    }
  }

  object CreditCard {
    case class AttachToAccount(bankAccount: NaiveBankAccount) // THE SOLUTION THIS PROBLEM should start from changing BankAccount to -> Actor Ref
    case object CheckStatus
  }
  class CreditCard extends Actor {
    import CreditCard._
    def attachedTo(account: NaiveBankAccount): Receive = {
      case CheckStatus =>
        println(s"${self.path} your message has been processed.")
        //
        account.withdraw(1) // because I can
    }
    override def receive: Receive = {
      case AttachToAccount(account ) => context.become(attachedTo(account))
    }
  }

  import CreditCard._
  import NaiveBankAccount._
  val system = ActorSystem("ParentChildDemo")
  val bankAccountRef = system.actorOf(Props[NaiveBankAccount], "account")
  bankAccountRef ! InitializeAccount
  bankAccountRef ! Deposit(100)

  Thread.sleep(500)
  val ccSelection = system.actorSelection("/user/account/card")
  ccSelection ! CheckStatus

  // WRONG !
}
