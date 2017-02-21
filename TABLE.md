# AMI-TABLE

the `org.xmlcml.svg2xml.table` package supports the extraction of HTML tables from PDF2SVG output.

## examples

Key example code is in 
```
/svg2xml/src/test/java/org/xmlcml/svg2xml/table/TableBodyTest.java
/svg2xml/src/test/java/org/xmlcml/svg2xml/table/TableCellTest.java
/svg2xml/src/test/java/org/xmlcml/svg2xml/table/TableContentCreatorTest.java
/svg2xml/src/test/java/org/xmlcml/svg2xml/table/TableFixtures.java
/svg2xml/src/test/java/org/xmlcml/svg2xml/table/TableGridTest.java
/svg2xml/src/test/java/org/xmlcml/svg2xml/table/TableMarkupTest.java
/svg2xml/src/test/java/org/xmlcml/svg2xml/table/TableRowTest.java
/svg2xml/src/test/java/org/xmlcml/svg2xml/table/TableStructurerTest.java
/svg2xml/src/test/java/org/xmlcml/svg2xml/table/TableTableTest.java
```
and examples in
```
/svg2xml/src/test/resources/org/xmlcml/svg2xml/table
```

### heavily used examples

#### testSuscriptSVG1
```
org.xmlcml.svg2xml.table.TableContentCreatorTest.testSuscriptSVG1()
```
contains key operations:

```
		TableContentCreator tableContentCreator = new TableContentCreator(); 
		// annotate the geometric regions of the SVG
		tableContentCreator.markupAndOutputTable(inputFile1, outDir);
```



