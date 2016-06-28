package com.gilt.fedex.models

/**
 * Descriptive data for a physical location. May be used as an actual physical address (place to which one could go), or as a container of "address parts" which should be handled as a unit (such as a city-state-ZIP combination within the US).
 */
case class Address(
  StreetLines: Seq[String] = Nil,
  City: Option[String] = None,
  StateOrProvinceCode: Option[String] = None,
  PostalCode: Option[String] = None,
  UrbanizationCode: Option[String] = None,
  CountryCode: Option[String] = None,
  CountryName: Option[String] = None,
  Residential: Option[Boolean] = None)