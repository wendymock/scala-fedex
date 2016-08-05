package com.gilt.fedex

import com.fedex.ship.stub._
import scala.concurrent.Future
import java.util.UUID

/**
 * Basic Fedex Client that wraps most of the boiler plate
 * of using our generated soap client. Simply instantiate an
 * instance and then invoke our processShipment method.
 */
class FedexClient(
    val key: String,
    val password: String,
    val accountNumber: String,
    val meterNumber: String,
    val baseAddressOpt: Option[String] = None,
    val parentKeyOpt: Option[String] = None,
    val parentPasswordOpt: Option[String] = None,
    val integratorIdOpt: Option[String] = None,
    val localizationOpt: Option[Localization] = None,
    val version: VersionId = VersionId("ship", 17, 0, 0)) extends FedexShipServiceSoapClient {

  val userCredentials = WebAuthenticationCredential(key, password)

  // Default Base Address is based on our beta url
  // For production simply remove beta from the url
  override def baseAddress = baseAddressOpt.map(new java.net.URI(_)).getOrElse(super.baseAddress)

  val parentCredentialsOpt = for {
    parentKey <- parentKeyOpt
    parentPassword <- parentPasswordOpt
  } yield { WebAuthenticationCredential(parentKey, parentPassword) }

  // Construct our defaults that will go with every request
  val webAuthenticationDetail = WebAuthenticationDetail(
    parentCredentialsOpt,
    userCredentials)

  val clientDetail = ClientDetail(
    accountNumber,
    meterNumber,
    integratorIdOpt,
    localizationOpt)

  private def genTransactionId = s"Transaction Id: ${UUID.randomUUID()} https://github.com/gilt/scala-fedex"

  private def buildTransactionDetail(transactionId: String) = {
    TransactionDetail(Some(transactionId), localizationOpt)
  }

  /**
   * Wrapper around processShipment function. Invokes processShipment
   * with our credentials and client details.
   */
  def processShipment(
    requestedShipment: RequestedShipment,
    transactionId: String = genTransactionId): Future[ProcessShipmentReply] = {

    service.processShipment(
      webAuthenticationDetail,
      clientDetail,
      Some(buildTransactionDetail(transactionId)),
      version,
      requestedShipment)
  }

  /**
   * Debugging method to serialize a processShipment request to the XML it'll produce
   */
  def processShipmentToXml(requestedShipment: RequestedShipment, transactionId: String = genTransactionId): String = {
    scalaxb.toXML(com.fedex.ship.stub.ProcessShipmentRequest(
      webAuthenticationDetail,
      clientDetail,
      Some(buildTransactionDetail(transactionId)),
      version, requestedShipment),
      Some("http://fedex.com/ws/ship/v17"),
      "ProcessShipmentRequest", defaultScope).toString
  }
}
