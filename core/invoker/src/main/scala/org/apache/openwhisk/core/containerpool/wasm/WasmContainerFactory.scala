package org.apache.openwhisk.core.containerpool.wasm

import org.apache.openwhisk.core.containerpool.ContainerFactory
import org.apache.openwhisk.common.{Logging, TransactionId}
import org.apache.openwhisk.core.WhiskConfig
import org.apache.openwhisk.core.containerpool.Container
import org.apache.openwhisk.core.entity.{ByteSize, ExecManifest}
import scala.concurrent.Future
import org.apache.openwhisk.core.containerpool.ContainerFactoryProvider
import akka.actor.ActorSystem
import org.apache.openwhisk.core.entity.InvokerInstanceId
import scala.concurrent.ExecutionContext

class WasmContainerFactory(instance: InvokerInstanceId, config: WhiskConfig)(implicit
  actorSystem: ActorSystem,
  ec: ExecutionContext,
  logging: Logging)
    extends ContainerFactory {

  implicit val wasmClient = initializeWasmClient()

  private def initializeWasmClient(): WasmRuntimeApi = {
    new WasmClient()
  }

  override def createContainer(
    tid: TransactionId,
    name: String,
    actionImage: ExecManifest.ImageName,
    userProvidedImage: Boolean,
    memory: ByteSize,
    cpuShares: Int)(implicit config: WhiskConfig, logging: Logging): Future[Container] = {
    WasmContainer.createContainer(
      tid: TransactionId,
      name: String,
      actionImage: ExecManifest.ImageName,
      userProvidedImage: Boolean,
      memory: ByteSize,
      cpuShares: Int)
  }

  override def init(): Unit = cleanup()

  override def cleanup(): Unit = {}
}

object WasmContainerFactoryProvider extends ContainerFactoryProvider {

  override def instance(
    actorSystem: ActorSystem,
    logging: Logging,
    config: WhiskConfig,
    instance: InvokerInstanceId,
    parameters: Map[String, Set[String]]): ContainerFactory =
    new WasmContainerFactory(instance, config)(actorSystem, actorSystem.dispatcher, logging)
}
