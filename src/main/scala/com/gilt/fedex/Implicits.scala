package com.gilt.fedex

import com.fedex.ship.stub.RequestedShipment

object Implicits {

  implicit def requestedShipmentToXmlType[A](rs: RequestedShipment) = {
    new XmlRequestedShipment(rs)
  }

  val defaultScope = scalaxb.toScope(None -> "http://fedex.com/ws/ship/v17",
    Some("ns") -> "http://fedex.com/ws/ship/v17",
    Some("tns") -> "http://fedex.com/ws/ship/v17",
    Some("xs") -> "http://www.w3.org/2001/XMLSchema",
    Some("xsi") -> "http://www.w3.org/2001/XMLSchema-instance")

  class XmlRequestedShipment(requestedShipment: RequestedShipment) {
    lazy val toXml: String = scalaxb.toXML(requestedShipment, "RequestedShipment", defaultScope).toString
  }
}
