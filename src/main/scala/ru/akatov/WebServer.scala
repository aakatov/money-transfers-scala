package ru.akatov

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer

import scala.concurrent.Future
import scala.io.StdIn

class WebServer(implicit val system: ActorSystem,
                implicit val materializer: ActorMaterializer) extends RestService {

  override val accountService = system.actorOf(AccountService.props, "accountService")

  def startServer(address: String, port: Int): Future[Http.ServerBinding] = {
    Http().bindAndHandle(routes, address, port)
  }
}

object WebServer extends Directives {
  implicit val system = ActorSystem("moneytransfers")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  def main(args: Array[String]) {
    val server = new WebServer()
    val bindingFuture = server.startServer("localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
