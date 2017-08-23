package ru.akatov

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import ru.akatov.AccountService._
import spray.json.DefaultJsonProtocol._

import scala.concurrent.duration._

/**
  * DTOs
  */
case class TrasnferDto(targetId: Long, amount: BigDecimal)

/**
  * Account REST service
  */
trait RestService {
  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer
  implicit val accountFormat = jsonFormat3(Account)
  implicit val createAccountFormat = jsonFormat2(CreateAccount)
  implicit val transferFormat = jsonFormat2(TrasnferDto)
  implicit val timeout: Timeout = 5.seconds

  val accountService: ActorRef

  val routes: Route = {
    pathPrefix("accounts") {
      pathEnd {
        post {
          entity(as[CreateAccount]) { createAcnt =>
            val acc = (accountService ? createAcnt).mapTo[Account]
            complete(StatusCodes.Created -> acc)
          }
        }
      } ~
        pathPrefix(LongNumber) { id =>
          pathEnd {
            get {
              val opt = (accountService ? GetAccount(id)).mapTo[Option[Account]]
              rejectEmptyResponse(complete(opt))
            } ~
              delete {
                accountService ! DeleteAccount(id)
                complete(StatusCodes.NoContent -> s"Account $id deleted")
              }
          } ~
            path("transfer") {
              post {
                entity(as[TrasnferDto]) { case TrasnferDto(targetId, amount) =>
                  val future = (accountService ? Transfer(id, targetId, amount)).mapTo[Account]
                  completeOrRecoverWith(future) {
                    case ex@AccountNotFoundException(accId) if accId == id =>
                      complete(StatusCodes.NotFound, ex.getMessage)
                    case ex@AccountNotFoundException(accId) if accId == targetId =>
                      complete(StatusCodes.BadRequest, ex.getMessage)
                    case ex@InsufficientFundsException(_, _) =>
                      complete(StatusCodes.BadRequest, ex.getMessage)
                  }
                }
              }
            }
        }
    }
  }
}
