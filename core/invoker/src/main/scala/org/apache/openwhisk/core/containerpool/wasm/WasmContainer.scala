package org.apache.openwhisk.core.containerpool.wasm

import org.apache.openwhisk.common.Logging
import org.apache.openwhisk.core.containerpool._
import akka.actor.ActorSystem
import scala.concurrent.ExecutionContext
import akka.stream.scaladsl.Source
import akka.util.ByteString
import org.apache.openwhisk.common.TransactionId
import org.apache.openwhisk.core.entity.ByteSize
import scala.concurrent.Future
import org.apache.openwhisk.core.entity.{ByteSize, ExecManifest}

object WasmContainer {

  def createContainer(
    tid: TransactionId,
    name: String,
    actionImage: ExecManifest.ImageName,
    userProvidedImage: Boolean,
    memory: ByteSize,
    cpuShares: Int)(implicit
    wasmRuntime: WasmRuntimeApi,
    actorSystem: ActorSystem,
    ec: ExecutionContext,
    logging: Logging): Future[WasmContainer] = {
    val address = wasmRuntime.start()

    address.flatMap { value =>
      Future { new WasmContainer(value._1, value._2) }
    }
  }

}

/**
 * Represents a container as run by the Wasm runtime.
 *
 * @constructor
 * @param id the id of the container
 * @param addr the ip & port of the container
 * @param workerIP the ip of the workernode on which the container is executing
 * @param nativeContainerId the docker/containerd lowlevel id for the container
 */
class WasmContainer(protected[core] val id: ContainerId, protected[core] val addr: ContainerAddress)(implicit
  wasmRuntime: WasmRuntimeApi,
  override protected val as: ActorSystem,
  protected val ec: ExecutionContext,
  protected val logging: Logging)
    extends Container {

  override def logs(limit: ByteSize, waitForSentinel: Boolean)(implicit
    transid: TransactionId): Source[ByteString, Any] = {
      Source.empty
    }

}
