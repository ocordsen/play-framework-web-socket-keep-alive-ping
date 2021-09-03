package controllers

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.stream.Materializer
import play.api.libs.streams.ActorFlow
import play.api.mvc._

import javax.inject._

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents)(implicit system: ActorSystem, materializer: Materializer) extends BaseController {

  def index: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def websocket: WebSocket = WebSocket.accept[String, String](_ => ActorFlow.actorRef(out => EchoActor.props(out)))
}

object EchoActor {

  def props(actorRef: ActorRef): Props = Props(new EchoActor(actorRef))
}

class EchoActor(out: ActorRef) extends Actor {

  def receive: Receive = {
    case message =>
      out ! message
  }
}
