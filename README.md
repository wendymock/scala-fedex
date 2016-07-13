# scala-fedex
A thin async Scala client for [FedEx’s Ship Service SOAP API](https://www.fedex.com/us/developer/web-services/process.html). 

[![Build Status](https://travis-ci.org/gilt/scala-fedex.svg?branch=master)](https://travis-ci.org/gilt/scala-fedex)

scala-fedex reduces the boilerplate and verbosity of generated code from the Ship Service WSDL. It provides a nice abstraction and cleaner example of usage than the Java sample code on FedEx’s site 🌸 👍

### Usage

```scala
// Create a client instance
val fedexClient = new FedexClient(key, password, accountNumber, meterNumber)

// Process shipments
val result: Future[ProcessShipmentReply] = fedexClient.processShipment(requestedShipment)
```

### Testing
There’s one automated spec [FedexClientSpec.scala](https://github.com/gilt/scala-fedex/blob/master/src/test/scala/com/gilt/fedex/FedexClientSpec.scala). This will call FedEx’s API and do a basic check of the label. The test needs environment variables defined for your FedEx test credentials. You can obtain these by registering on [FedEx’s developer portal](http://www.fedex.com/us/developer/web-services/index.html).
```bash
# Export your password, key, meter number, and account number
export FEDEX_PASSWORD="............."
export FEDEX_KEY=".................."
export FEDEX_METER_NUMBER="........."
export FEDEX_ACCOUNT_NUMBER="......."
```


You can also save and check the writing of the label by setting the environment variable `FUNCTIONAL_TEST="true"`

```bash
# Will write out a label to test-label-${trackingNumber}.pdf in the local directory
export FUNCTIONAL_TEST="true"
# Run our test to create a label
sbt clean test
```

### Models
This project’s FedEx models are generated using [scalaxb](https://github.com/eed3si9n/scalaxb) and [FedEx’s Ship Service WSDL version 17](https://github.com/gilt/scala-fedex/blob/master/ShipService_v17.wsdl). To regenerate the models run the gen script.

```bash
./gen.sh
```

### Publish
```bash
git tag X.X.X
git push --tags
sbt publish
```
