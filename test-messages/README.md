# Test messages

This directory contains test messages which can be used a guidance for development.

Currently we want to support in the lis-integration for the ordering process:
* `HL7/ORM-O01/one-single-test.hl7`
* `HL7/ORM-O01/one-panel-test.hl7`

For Senaite required fields are:
* OBR.4.2 Test Name or Panel Name
* OBR.7 Sample taking time
* OBR.15.1.2 Sample Type
* OBR.39.2 Text "Observation Request is Panel Test" set if you have ordered a panel  

## HL7

### ORM-O01

#### One single test message
`HL7/ORM-O01/one-single-test.hl7` contains one single test. 

#### One panel test message
`HL7/ORM-O01/one-panel-test.hl7` contains one panel test.

#### Not supported messages
`HL7/ORM-O01/not-supported-messages` contains messages which must not be produced by lis-integration.
These can also be use for negative testing if needed, especially on the target e.g. LIS.
