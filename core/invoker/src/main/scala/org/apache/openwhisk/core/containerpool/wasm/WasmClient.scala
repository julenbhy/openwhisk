package org.apache.openwhisk.core.containerpool.wasm

import scala.concurrent.{Future}
import scala.concurrent.ExecutionContext
import org.apache.openwhisk.common.Logging
import akka.http.scaladsl.Http
import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.model.HttpRequest
import org.apache.openwhisk.core.containerpool.ContainerAddress
import spray.json.DefaultJsonProtocol._
import spray.json._
import org.apache.openwhisk.core.containerpool.ContainerId

case class WasmClientApiException(e: String) extends Exception

class WasmClient()(implicit
  protected val ec: ExecutionContext,
  protected val logging: Logging,
  protected val as: ActorSystem)
    extends WasmRuntimeApi {

  private val endpoint = "127.0.0.1:9000"

  case class WasmRuntimeResponse(containerId: String, port: Int)
  implicit val WasmRuntimeResponseFormat = jsonFormat2(WasmRuntimeResponse)

  def start(): Future[(ContainerId, ContainerAddress)] = {

    Http().singleRequest(HttpRequest(HttpMethods.POST, endpoint + "/start")).flatMap { response =>
      logging
        .info("WasmClient.start", s"Got response ${response.status} content <${response.entity.dataBytes.toString()}>")
      if (response.status.isSuccess()) {
        val responseString = response.entity.dataBytes.toString().parseJson
        logging.info(this, s"Got response string $responseString")
        val responseObj = responseString.convertTo[WasmRuntimeResponse]
        Future { (ContainerId(responseObj.containerId), ContainerAddress("127.0.0.1", responseObj.port)) }
      } else {
        Future.failed(WasmClientApiException(s"WasmRuntime returned ${response.status}"))
      }
    }
  }
}

trait WasmRuntimeApi {
  def start(): Future[(ContainerId, ContainerAddress)]
}
