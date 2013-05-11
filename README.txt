=SVG2XML=

==IMPORTANT NOTE==
This software is ALPHA. This means it doesn't get everything right. Even if it were BETA it wouldn't.
That's because content-mining is an inexact process and beacuse PDFs have NO information other than the 
position of the primitives on the page. We are delighted to have comments, but be aware that some 
problems will not be and some cannot be fixed.

==Dev version==
The development version relies on:
http://bitbucket.org/petermr/euclid-dev
http://bitbucket.org/petermr/html-dev
http://bitbucket.org/petermr/svg-dev
http://bitbucket.org/petermr/pdf2svg-dev

You may need to update and mvn install these if they are not in WWMM maven.

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

java SVG2XML for usage()

CURRENT
java <classname> <file> works where <file> is:
   - anything.pdf
   - a file with a list of pdf files
   - a directory containing PDF files (*.pdf)
    

==Output==
*-pagennn.xml - one for each page

==Limitations==

==Getting started==


