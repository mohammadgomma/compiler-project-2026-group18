parser grammar TemplateParser;

options { tokenVocab=TemplateLexer; }



template: element* EOF;

element:
      htmlElement
    | jinjaExpression
    | jinjaBlock
    | styleElement
    | TEXT
    | HTML_COMMENT
    ;

htmlElement:
      OPEN_DOCTYPE htmlParts HTML_TAG_CLOSE
    | OPEN_TAG htmlParts HTML_TAG_CLOSE element* closeTag
    | OPEN_TAG htmlParts HTML_SLASH_CLOSE
    ;

htmlParts: HTML_NAME htmlAttribute* ;
htmlAttribute: HTML_NAME (HTML_EQ HTML_STRING)? ;
closeTag: CLOSE_TAG_START HTML_NAME HTML_TAG_CLOSE ;

styleElement:
      STYLE_TAG_OPEN styleAttribute* STYLE_TAG_CLOSE cssContent* CSS_END_TAG
    ;

styleAttribute: STYLE_ATTR_NAME (STYLE_EQ STYLE_ATTR_VALUE)? ;
cssContent: CSS_CONTENT | CSS_LBRACE | CSS_RBRACE | jinjaExpression | jinjaBlock ;

jinjaExpression:
      DOUBLE_BRACE jExpr JINJA_CLOSE
    | HTML_DOUBLE_BRACE jExpr JINJA_CLOSE
    | CSS_DOUBLE_BRACE jExpr JINJA_CLOSE
    ;

jExpr:
      jAtom (J_DOT J_ID | J_LBRACKET jExpr J_RBRACKET | J_LPAREN jArgs? J_RPAREN)* jFilter*
    ;

jFilter: J_PIPE J_ID (J_COLON jFilterArg (J_COMMA jFilterArg)*)? ;
jFilterArg: J_ID | J_STRING | J_NUMBER | J_TRUE | J_FALSE ;

jAtom:
      J_ID
    | J_NUMBER
    | J_STRING
    | J_TRUE
    | J_FALSE
    | J_LPAREN jExpr J_RPAREN
    | J_LBRACKET (jExpr (J_COMMA jExpr)*)? J_RBRACKET
    | J_LBRACE (jExpr J_COLON jExpr (J_COMMA jExpr J_COLON jExpr)*)? J_RBRACE
    ;

jArgs: jExpr (J_COMMA jExpr)* (J_COMMA J_ID J_EQ jExpr)* ;

jinjaBlock:
      ifBlock
    | forBlock
    | setBlock
    ;

ifBlock:
      OPEN_BLOCK JB_IF jbExpr JINJA_BLOCK_CLOSE element*
      (OPEN_BLOCK JB_ELIF jbExpr JINJA_BLOCK_CLOSE element*)*
      (OPEN_BLOCK JB_ELSE JINJA_BLOCK_CLOSE element*)?
      OPEN_BLOCK JB_ENDIF JINJA_BLOCK_CLOSE
    | HTML_OPEN_BLOCK JB_IF jbExpr JINJA_BLOCK_CLOSE element*
      (HTML_OPEN_BLOCK JB_ELIF jbExpr JINJA_BLOCK_CLOSE element*)*
      (HTML_OPEN_BLOCK JB_ELSE JINJA_BLOCK_CLOSE element*)?
      HTML_OPEN_BLOCK JB_ENDIF JINJA_BLOCK_CLOSE
    | CSS_OPEN_BLOCK JB_IF jbExpr JINJA_BLOCK_CLOSE cssContent*
      (CSS_OPEN_BLOCK JB_ELIF jbExpr JINJA_BLOCK_CLOSE cssContent*)*
      (CSS_OPEN_BLOCK JB_ELSE JINJA_BLOCK_CLOSE cssContent*)?
      CSS_OPEN_BLOCK JB_ENDIF JINJA_BLOCK_CLOSE
    ;

forBlock:
      OPEN_BLOCK JB_FOR JB_ID JB_IN jbExpr JINJA_BLOCK_CLOSE element* OPEN_BLOCK JB_ENDFOR JINJA_BLOCK_CLOSE
    | HTML_OPEN_BLOCK JB_FOR JB_ID JB_IN jbExpr JINJA_BLOCK_CLOSE element* HTML_OPEN_BLOCK JB_ENDFOR JINJA_BLOCK_CLOSE
    | CSS_OPEN_BLOCK JB_FOR JB_ID JB_IN jbExpr JINJA_BLOCK_CLOSE cssContent* CSS_OPEN_BLOCK JB_ENDFOR JINJA_BLOCK_CLOSE
    ;

setBlock:
      OPEN_BLOCK JB_SET JB_ID JB_EQ jbExpr JINJA_BLOCK_CLOSE
    | HTML_OPEN_BLOCK JB_SET JB_ID JB_EQ jbExpr JINJA_BLOCK_CLOSE
    | CSS_OPEN_BLOCK JB_SET JB_ID JB_EQ jbExpr JINJA_BLOCK_CLOSE
    ;

jbExpr:
      jbAtom (JB_DOT JB_ID | JB_LBRACKET jbExpr JB_RBRACKET | JB_LPAREN jbArgs? JB_RPAREN)* jbFilter*
    ;

jbFilter: JB_PIPE JB_ID (JB_COLON jbFilterArg (JB_COMMA jbFilterArg)*)? ;
jbFilterArg: JB_ID | JB_STRING | JB_NUMBER | JB_TRUE | JB_FALSE ;

jbAtom:
      JB_ID
    | JB_NUMBER
    | JB_STRING
    | JB_TRUE
    | JB_FALSE
    | JB_LPAREN jbExpr JB_RPAREN
    | JB_LBRACKET (jbExpr (JB_COMMA jbExpr)*)? JB_RBRACKET
    ;

jbArgs: jbExpr (JB_COMMA jbExpr)* (JB_COMMA JB_ID JB_EQ jbExpr)* ;
