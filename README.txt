=SVGPlus=

==Introduction== 
SVGPlus is the second of (probably) three modules in the AMI2 system, the first being PDF2SVG.
Its role is to convert PDF-free SVG (from PDF2SVG) into enhanced SVG which creates strcutre out of isolated SVG primitives with as little loss as possible. The aims include:
  * chunking pages by whitespace
  * analysing characters as running text
  * analysing graphics primitives as part of higher level objects
  * normalizing components where possible
  * providing a simpler representation to work with
SVGPlus is particularly aimed at those who wish to process the SVG into semantic STM
(Science, Technical, Medical) but can be applied elsewhere 
  

==Architecture== 
PDF2SVGPlus is written in Java >= 1.5 and built/distributed under Maven. It relies on the following libraries:
 * PDFBox: (currently 1.7.1 though 1.6 should work)
 * Euclid and CMLXOM: (libraries for geometry/numeric, CML and general XML). The CML is a bit overkill and we 
   may refactor it later to separate out the non-chemical routines
 * SVG: a XOM (XML DOM) for SVG (limited set of (static) primitives and attributes, but enough to extract 
   most static semantics)
 * XOM: an XML DOM
 * probably PDF2SVG
 
To build:
 * hg clone https://bitbucket.org/petermr/svgplus (needs Mercurial)
 * cd svgplus
 * mvn clean install (on commandline)
This should create classes and a standalone jar

==Running==
Current usage is:
java svgplus [commandfile]

almost all control is delegated to the commandfile

==Output==

==Limitations==
It compiles but doesn't yet work (2012-11-12) though hopefully by the end of that week



