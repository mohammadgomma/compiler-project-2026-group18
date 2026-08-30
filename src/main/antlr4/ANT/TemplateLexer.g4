lexer grammar TemplateLexer;



// -----------------------------------------
// HTML / Text mode (Default)
// -----------------------------------------

OPEN_TAG : '<' -> pushMode(HTML_MODE);
CLOSE_TAG_START : '</' -> pushMode(HTML_MODE);
OPEN_DOCTYPE : '<!DOCTYPE' -> pushMode(HTML_MODE);
HTML_COMMENT : '<!--' .*? '-->' -> skip ;

STYLE_TAG_OPEN: '<style' -> pushMode(STYLE_TAG_MODE);

DOUBLE_BRACE : '{{' -> pushMode(JINJA_EXPR_MODE);
OPEN_BLOCK : '{%' -> pushMode(JINJA_BLOCK_MODE);

TEXT : ~[<{]+ ;


// -----------------------------------------
// HTML Tag Mode
// -----------------------------------------
mode HTML_MODE;

HTML_TAG_CLOSE : '>' -> popMode;
HTML_SLASH_CLOSE : '/>' -> popMode;

HTML_DOUBLE_BRACE : '{{' -> pushMode(JINJA_EXPR_MODE) ;
HTML_OPEN_BLOCK : '{%' -> pushMode(JINJA_BLOCK_MODE) ;

HTML_NAME : [a-zA-Z_][a-zA-Z0-9_-]* ;
HTML_EQ : '=' ;
HTML_STRING : '"' (~["\r\n] | '\\"')* '"'
            | '\'' (~['\r\n] | '\\\'')* '\'' ;

HTML_WS : [ \t\r\n]+ -> skip ;


// -----------------------------------------
// Style Tag Mode
// -----------------------------------------
mode STYLE_TAG_MODE;

STYLE_TAG_CLOSE: '>' -> popMode, pushMode(CSS_CONTENT_MODE);
STYLE_ATTR_NAME: [a-zA-Z_][a-zA-Z0-9_-]* ;
STYLE_EQ: '=' ;
STYLE_ATTR_VALUE: '"' (~["\r\n] | '\\"')* '"' | '\'' (~['\r\n] | '\\\'')* '\'' ;
STYLE_WS: [ \t\r\n]+ -> skip ;


// -----------------------------------------
// CSS Content Mode
// -----------------------------------------
mode CSS_CONTENT_MODE;

CSS_END_TAG: '</style>' -> popMode;
CSS_DOUBLE_BRACE: '{{' -> pushMode(JINJA_EXPR_MODE);
CSS_OPEN_BLOCK: '{%' -> pushMode(JINJA_BLOCK_MODE);
CSS_COMMENT: '/*' .*? '*/' -> skip ;
CSS_WS: [ \t\r\n]+ -> skip ;
CSS_LBRACE: '{' ;
CSS_RBRACE: '}' ;
CSS_CONTENT: ~[<{]+ ;


// -----------------------------------------
// Jinja Expression Mode {{ ... }}
// -----------------------------------------
mode JINJA_EXPR_MODE;

JINJA_CLOSE : '}}' -> popMode;

J_ID : [a-zA-Z_][a-zA-Z0-9_]* ;
J_NUMBER : [0-9]+ ('.' [0-9]+)? ;
J_STRING : '"' (~["] | '\\"')* '"' | '\'' (~['] | '\\\'')* '\'' ;
J_TRUE : 'true' | 'True' ;
J_FALSE : 'false' | 'False' ;

J_DOT : '.' ;
J_PIPE : '|' ;
J_COLON : ':' ;
J_COMMA : ',' ;
J_EQ : '=' ;
J_PLUS : '+' ;
J_MINUS : '-' ;
J_MUL : '*' ;
J_DIV : '/' ;
J_MOD : '%' ;
J_NOT : '!' | 'not' ;
J_AND : '&&' | 'and' ;
J_OR : '||' | 'or' ;
J_LT : '<' ;
J_GT : '>' ;
J_LE : '<=' ;
J_GE : '>=' ;
J_EQ_EQ : '==' ;
J_NE : '!=' ;
J_LBRACKET : '[' ;
J_RBRACKET : ']' ;
J_LPAREN : '(' ;
J_RPAREN : ')' ;
J_LBRACE : '{' ;
J_RBRACE : '}' ;

J_WS : [ \t\r\n]+ -> skip ;


// -----------------------------------------
// Jinja Block Mode {% ... %}
// -----------------------------------------
mode JINJA_BLOCK_MODE;

JINJA_BLOCK_CLOSE : '%}' -> popMode;

JB_IF : 'if' ;
JB_ELSE : 'else' ;
JB_ELIF : 'elif' ;
JB_FOR : 'for' ;
JB_IN : 'in' ;
JB_ENDFOR : 'endfor' ;
JB_ENDIF : 'endif' ;
JB_SET : 'set' ;

JB_ID : [a-zA-Z_][a-zA-Z0-9_]* ;
JB_NUMBER : [0-9]+ ('.' [0-9]+)? ;
JB_STRING : '"' (~["] | '\\"')* '"' | '\'' (~['] | '\\\'')* '\'' ;
JB_TRUE : 'true' | 'True' ;
JB_FALSE : 'false' | 'False' ;

JB_EQ : '=' ;
JB_PIPE : '|' ;
JB_COLON : ':' ;
JB_COMMA : ',' ;
JB_DOT : '.' ;
JB_LT : '<' ;
JB_GT : '>' ;
JB_LE : '<=' ;
JB_GE : '>=' ;
JB_EQ_EQ : '==' ;
JB_NE : '!=' ;
JB_LBRACKET : '[' ;
JB_RBRACKET : ']' ;
JB_LPAREN : '(' ;
JB_RPAREN : ')' ;

JB_WS : [ \t\r\n]+ -> skip ;
