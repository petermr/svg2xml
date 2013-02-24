=SVG2XML=

==Introduction== 
SVG2XML is the second of (probably) three modules in the AMI2 system, the first being PDF2SVG.
Its role is to convert PDF-free SVG (from PDF2SVG) into enhanced SVG which creates structure out 
of isolated SVG primitives with as little loss as possible. The aims include:
  * chunking pages by whitespace
  * analysing characters as running text
  * analysing graphics primitives as part of higher level objects
  * normalizing components where possible
  * providing a simpler representation to work with
SVG2XML is particularly aimed at those who wish to process the SVG into semantic STM
(Science, Technical, Medical) but can be applied elsewhere 
  

==Architecture== 
PDF2SVG2XML is written in Java >= 1.5 and built/distributed under Maven. It relies on the following libraries:
 * PDFBox: (currently 1.7.1 though 1.6 should work)
 * XOM: an XML DOM
 * Euclid and CMLXOM: (libraries for geometry/numeric, CML and general XML). The CML is a bit overkill and we 
   may refactor it later to separate out the non-chemical routines
 * SVG: a XOM (XML DOM) for SVG (limited set of (non-animated) primitives and attributes, but enough to extract 
   most non-animated semantics)
 * probably PDF2SVG
 
To build:
 * hg clone https://bitbucket.org/petermr/SVG2XML (needs Mercurial)
 * cd SVG2XML
 * mvn clean install (on commandline)
This should create classes and a standalone jar

==Running==
Current usage is still being developed: It is likely to be
java SVG2XML -i inputfile.pdf and or
java SVG2XML -c commandfile.xml
or some combination

use
java SVG2XML for usage()

almost all control is delegated to the commandfile

==Output==
*-pagennn.xml - one for each page

==Limitations==



