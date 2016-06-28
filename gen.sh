#!/bin/bash
#
# Small script to regenerate our soap files as needed

FEDEX_PACKAGE="src/main/scala/com/fedex/ship/stub"

scalaxb -p "com.fedex.ship.stub" ShipService_v17.wsdl -d "$FEDEX_PACKAGE"
