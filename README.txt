=SVG2XML=

==IMPORTANT NOTE==
This software is ALPHA. This means it doesn't get everything right. Even if it 
were BETA it wouldn't. That's because content-mining is an inexact process and
because PDFs have NO information other than the position of the primitives on
 the page. We are delighted to have comments, but be aware that some 
problems will not be and some cannot be fixed.

==Dev version==
The development version relies on:
http://bitbucket.org/petermr/euclid-dev
http://bitbucket.org/petermr/html-dev
http://bitbucket.org/petermr/svg-dev
http://bitbucket.org/petermr/pdf2svg-dev

You may need to update and mvn install these if they are not in WWMM maven.

==Introduction== 
SVG2XML is the second of (probably) three modules in the AMI2 system, the first
 being PDF2SVG. SVG2XML's role is to convert PDF-free SVG (from PDF2SVG) 
into enhanced SVG which creates structure out of isolated SVG primitives 
with as little loss as possible. The aims include:
  * chunking pages by whitespace
  * analysing characters as running text
  * analysing graphics primitives as part of higher level objects
  * normalizing components where possible
  * providing a simpler representation to work with
SVG2XML is particularly aimed at those who wish to process the SVG into 
semantic STM (Science, Technical, Medical) but can be applied elsewhere 
  

==Architecture== 
SVG2XML is written in Java >= 1.5 and built/distributed under Maven. 
It relies on the following libraries:
 * PDFBox: (now 1.8.1)
 * XOM: an XML DOM
 * Euclid and CMLXOM: (libraries for geometry/numeric, CML and general XML).
  The CML is a bit overkill and we may refactor it later to separate out 
  the non-chemical routines
 * SVG: a XOM (XML DOM) for SVG (limited set of (non-animated) primitives
  and attributes, but enough to extract most non-animated semantics)
 
To build:
 * hg clone https://bitbucket.org/petermr/svg2xml (oe ./svg2xml-dev)
  (needs Mercurial)
 * cd SVG2XML
 * mvn clean install (on commandline)
This should create classes and a standalone jar

==Running==
Current usage is still being developed: It is likely to be
mvn -exec:java -Dexec.mainClass="org.xmlcml.svg2xml.analyzer.PDFAnalyzer"
    -Dexec.args="<args>"
or    
java -jar svg2xml.jar <args>  //name of jar may have revno
where args are 
filename.pdf
or directory-with-pdfs
or http://some.where.org/my.file.pdf

java org.xmlcml.svg2xml.analyzer.PDFAnalyzer for usage()

CURRENT
java <classname> <file> works where <file> is:
   - anything.pdf
   - a file with a list of pdf files
   - a directory containing PDF files (*.pdf)
   - a URL of a PDF
    

==Output==
normally to:
target
    output
        fileroot1
            *.svg, *.html, etc. for one PDF
        fileroot2
            *.svg, *.html, etc. for one PDF
    svg
        raw svg-files.svg
            
==Limitations==
2013-05-26  // started sectioning by font sizes
2013-05-12  // tables
2013-04     // running HTML

==Getting started==
Use a modern PDF as test (there are some in
    src/test/resources/pdfs or use your own
    

 

