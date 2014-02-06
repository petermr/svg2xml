Architecture of text/character management

TextLine: a (raw) horizontal line of characters
ScriptLine: one or more TextLines (because of Suscripts) are processed into a ScriptLine. contains a StyleSpans
StyleSpan: a group of characters all of the same Font and weight/slant but maybe Suscripts
StyleSpans: a group of StyleSpan/s making a TextLine

TextStructurer: breaks a chunk into TextLines and then to ScriptLines