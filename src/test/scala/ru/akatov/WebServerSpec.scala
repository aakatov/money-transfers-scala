package ru.akatov

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.stream.ActorMaterializer
import org.scalatest.{Matchers, WordSpec}
import ru.akatov.AccountService.CreateAccount

class WebServerSpec extends WordSpec with Matchers with ScalatestRouteTest {
  //test accounts
  val accounts = List(
    Account(1001L, "Ivan Ivanov", BigDecimal(100)),
    Account(1002L, "Peter Petrov", BigDecimal(50))
  )

  val service = new RestService() {
    implicit val system: ActorSystem = ActorSystem("moneytransfers")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    override val accountService = system.actorOf(AccountService.props(accounts), "accountService")
  }
  val routes = service.routes

  implicit val accountFormat = service.accountFormat
  implicit val createAccountFormat = service.createAccountFormat
  implicit val transferFormat = service.transferFormat

  "The service" should {
    "create an account for POST requests" in {
      val createAccount = CreateAccount("Mary", 500)
      Post("/account", createAccount) ~> routes ~> check {
        status shouldBe StatusCodes.Created
        responseAs[Account] shouldBe Account(1L, createAccount.clientName, createAccount.amount)
      }
    }
    "return an account for GET requests" in {
      val acc = accounts.head
      val id = acc.id
      Get(s"/account/$id") ~> routes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[Account] shouldBe acc
      }
    }
    "transfer money successfully if there is enough funds on the account" in {
      val acc1 = accounts.head
      val acc2 = accounts.tail.head
      val amount = 10
      val dto = TrasnferDto(acc2.id, BigDecimal(amount))
      Post(s"/account/${acc1.id}/transfer", dto) ~> routes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[Account] shouldBe acc1.copy(amount = acc1.amount - amount)
      }
    }
    "fail if there's unsufficient funds to transfer" in {
      val acc1 = accounts.head
      val acc2 = accounts.tail.head
      val amount = acc1.amount + 1
      val dto = TrasnferDto(acc2.id, amount)
      Post(s"/account/${acc1.id}/transfer", dto) ~> routes ~> check {
        status shouldBe StatusCodes.BadRequest
      }
    }
    "return NotFound if the source account wasn't found" in {
      val id = Long.MaxValue
      val acc2 = accounts.tail.head
      val amount = 10
      val dto = TrasnferDto(acc2.id, BigDecimal(amount))
      Post(s"/account/$id/transfer", dto) ~> routes ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }
    "return BadRequest if the target account wasn't found" in {
      val acc1 = accounts.head
      val id = Long.MaxValue
      val amount = 10
      val dto = TrasnferDto(id, BigDecimal(amount))
      Post(s"/account/${acc1.id}/transfer", dto) ~> routes ~> check {
        status shouldBe StatusCodes.BadRequest
      }
    }
    "delete an account for DELETE requests" in {
      val acc = accounts.head
      val id = acc.id
      Delete(s"/account/$id") ~> routes ~> check {
        status shouldBe StatusCodes.NoContent
      }
    }
  }
}
