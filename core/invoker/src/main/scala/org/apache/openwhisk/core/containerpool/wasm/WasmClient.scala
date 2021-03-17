package org.apache.openwhisk.core.containerpool.wasm

import scala.concurrent.{Future}
import scala.concurrent.ExecutionContext
import org.apache.openwhisk.common.Logging
import akka.http.scaladsl.Http
import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.ContentTypes
import org.apache.openwhisk.core.containerpool.ContainerAddress
import spray.json.DefaultJsonProtocol._
import org.apache.openwhisk.core.containerpool.ContainerId

case class WasmClientApiException(e: String) extends Exception

class WasmClient()(implicit
  protected val ec: ExecutionContext,
  protected val logging: Logging,
  protected val as: ActorSystem)
    extends WasmRuntimeApi {

  private val endpoint = "127.0.0.1"
  private val port = 9000

  case class WasmRuntimeResponse(containerId: String, port: Int)
  implicit val WasmRuntimeResponseFormat = jsonFormat2(WasmRuntimeResponse)

  def start(): Future[(ContainerId, ContainerAddress)] = {
    val containerId = java.util.UUID.randomUUID().toString()

    logging.info(this, s"Generated new container id $containerId")

    Future { (ContainerId(containerId), ContainerAddress(endpoint, port)) }
  }

  def destroy(id: ContainerId): Future[Unit] = {
    logging.info(this, s"Destroying container ${id.asString}")

    Http().singleRequest(HttpRequest(
      HttpMethods.POST,
      s"http://$endpoint:$port/${id.asString}/destroy",
      entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, id.asString)
      )).flatMap { response =>
      if (response.status.isSuccess()) {
        Future.successful(())
      } else {
        Future.failed(WasmClientApiException(s"WasmRuntime returned ${response.status}"))
      }
    }
  }
}

trait WasmRuntimeApi {
  def start(): Future[(ContainerId, ContainerAddress)]
  def destroy(id: ContainerId): Future[Unit]
}
