package com.gilt.fedex

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures
import com.fedex.ship.stub._
import java.util.Date
import javax.xml.datatype.DatatypeFactory
import org.scalatest.time._
import java.io.FileOutputStream
import java.io.BufferedOutputStream
import java.nio.file.Paths
import java.nio.file.Files
import scala.util.Properties
import scala.util.Try

class FedexClientSpec extends FlatSpec with Matchers with ScalaFutures {

  implicit override val patienceConfig =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(5, Millis))

  // Test credentials pulled from environment variables
  val accountNumber = Properties.envOrElse("FEDEX_ACCOUNT_NUMBER", "")
  val meterNumber = Properties.envOrElse("FEDEX_METER_NUMBER", "")
  val fedexKey = Properties.envOrElse("FEDEX_KEY", "")
  val password = Properties.envOrElse("FEDEX_PASSWORD", "")

  // Create our client
  val fedexClient = new FedexClient(fedexKey, password, accountNumber, meterNumber)

  // Mock a bunch of data
  val shipTimestamp = new java.util.GregorianCalendar()
  shipTimestamp.setTime(new Date())

  val shippingContact = Contact(
    PersonName = Some("Barry Obama"),
    Title = Some("Potus"),
    CompanyName = Some("Gilt"),
    PhoneNumber = Some("203-456-3214"))

  val shipper = Party(AccountNumber = Some(accountNumber),
    Contact = Some(shippingContact),
    Address = Some(Address(
      StreetLines = Seq("GILT", "2 PARK AVE, 4th FL"),
      City = Some("NEW YORK"),
      StateOrProvinceCode = Some("NY"),
      PostalCode = Some("10016"),
      CountryCode = Some("US"))))

  val recipient = Party(AccountNumber = Some(accountNumber),
    Contact = Some(shippingContact.copy(PersonName = Some("Michelle Obama"))),
    Address = Some(Address(
      StreetLines = Seq("2 Park Ave."),
      City = Some("New York"),
      StateOrProvinceCode = Some("NY"),
      PostalCode = Some("10003"),
      CountryCode = Some("US"))))

  val smartPostDetail = SmartPostShipmentDetail(
    ProcessingOptionsRequested = None,
    Indicia = Some(PRESORTED_STANDARD),
    AncillaryEndorsement = Some(ADDRESS_CORRECTION),
    HubId = Some("5531"),
    CustomerManifestId = Some("XXX"))

  val lineItem = RequestedPackageLineItem(
    SequenceNumber = Some(1),
    Weight = Some(Weight(LB, 0.5)),
    Dimensions = Some(Dimensions(10, 1, 8, IN)),
    CustomerReferences = Seq(CustomerReference(CUSTOMER_REFERENCE, "GR4567892"), CustomerReference(INVOICE_NUMBER, "INV4567892"), CustomerReference(P_O_NUMBER, "PO4567892")))

  val labelSpecification = LabelSpecification(ImageType = Some(PDF), LabelFormatType = COMMON2D)
  val shippingChargesPayment = Payment(SENDER, Some(Payor(Some(shipper))))

  val requestedShipment = RequestedShipment(
    ShipTimestamp = DatatypeFactory.newInstance().newXMLGregorianCalendar(shipTimestamp),
    DropoffType = REGULAR_PICKUP,
    ServiceType = SMART_POST,
    PackagingType = YOUR_PACKAGING,
    Shipper = shipper,
    Recipient = recipient,
    LabelSpecification = labelSpecification,
    PackageCount = 1,
    ShippingChargesPayment = Option(shippingChargesPayment),
    SmartPostDetail = Some(smartPostDetail),
    RequestedPackageLineItems = Seq(lineItem))

  "FedexClient" should "processShipment" in {

    val result = fedexClient.processShipment(requestedShipment).futureValue

    val packageDetails = result.CompletedShipmentDetail.toSeq.flatMap { shipmentDetail =>
      shipmentDetail.CompletedPackageDetails
    }

    val labelPackageDetails = packageDetails.filter(_.TrackingIds.nonEmpty)
    val trackingIds = labelPackageDetails.map(_.TrackingIds)
    val label = labelPackageDetails.flatMap { _.Label }
    val labelParts = label.headOption.map(_.Parts).toSeq.flatten

    // Check that we got tracking information
    trackingIds shouldBe 'nonEmpty
    trackingIds.head.size should be > 0

    // If this is a functional test write out our label as well and check its presence
    if (Try(sys.env("FUNCTIONAL_TEST").toBoolean).getOrElse(false)) {
      labelParts.foreach { part =>
        val filename = s"test-label-${trackingIds.head.head.TrackingNumber.get}.pdf"
        val outputStream = new BufferedOutputStream(new FileOutputStream(filename))
        val image = part.Image.get.vector.toArray
        Stream.continually(outputStream.write(image))
        outputStream.close()
        Files.exists(Paths.get(filename)) shouldBe true
      }

    }
  }

  it should "output the xml of our process shipment request" in {
    val xml = fedexClient.processShipmentToXml(requestedShipment)
    xml should not be 'Empty
    xml should include("<RequestedShipment>")
    xml should include("<AccountNumber>")
  }

  "Implicits" should "provide toXml for RequestedShipment" in {
    import Implicits._
    requestedShipment.toXml should include("</RequestedShipment>")
  }
}
