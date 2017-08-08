package ru.akatov

import akka.actor.{Actor, ActorLogging, Props}

/**
  * Domain model
  */
case class Account(id: Long, clientName: String, amount: BigDecimal)

object AccountService {
  def props(list: List[Account]): Props = Props(new AccountService(list))

  def props: Props = Props(new AccountService(List()))

  // exceptions
  case class AccountNotFoundException(id: Long) extends Exception(s"Account $id not found")

  case class InsufficientFundsException(id: Long, amount: BigDecimal) extends Exception(s"Insufficient funds on account $id, current balance is $amount")

  // messages
  case class Transfer(sourceId: Long, targetId: Long, amount: BigDecimal)

  case class CreateAccount(clientName: String, amount: BigDecimal)

  case class GetAccount(id: Long)

  case class DeleteAccount(id: Long)

}

class AccountService(list: List[Account]) extends Actor with ActorLogging {

  import AccountService._

  val accounts = collection.mutable.Map(list.map(a => a.id -> a): _*).withDefault(id => throw AccountNotFoundException(id))
  var nextId = 1L

  def getNextId() = {
    val id = nextId
    nextId += 1
    id
  }

  def receive = {
    case CreateAccount(clientName, amount) =>
      val acc = Account(getNextId(), clientName, amount)
      accounts += (acc.id -> acc)
      log.info(s"Account saved: $acc")
      sender() ! acc
    case GetAccount(id) =>
      val acc = accounts.get(id)
      log.info(s"Account retrieved: $acc")
      sender() ! acc
    case DeleteAccount(id) =>
      val acc = accounts(id)
      accounts.remove(id)
      log.info(s"Account deleted: $acc")
    case trans@Transfer(sourceId: Long, targetId: Long, amount: BigDecimal) =>
      try {
        if (sourceId == targetId) throw new IllegalArgumentException("")
        val source = accounts(sourceId)
        val target = accounts(targetId)
        if (source.amount < amount) throw InsufficientFundsException(source.id, source.amount)
        accounts.update(sourceId, source.copy(amount = source.amount - amount))
        accounts.update(targetId, target.copy(amount = target.amount + amount))
        log.info(s"Transfer done: $trans")
        sender() ! accounts(sourceId)
      } catch {
        case e: Exception =>
          sender() ! akka.actor.Status.Failure(e)
          throw e
      }
  }
}

