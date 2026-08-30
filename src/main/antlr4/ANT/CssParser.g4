
parser grammar CssParser;

options {
    tokenVocab = CssLexer;
}

stylesheet: (charsetRule? importRule* (cssRule | atRule)*) EOF;


// @charset
charsetRule: AT_CHARSET STRING SEMICOLON;

// @import
importRule: AT_IMPORT (STRING | URL) mediaQueryList? SEMICOLON;

// @media
mediaQueryRule: AT_MEDIA mediaQueryList block;
mediaQueryList: mediaQuery (COMMA mediaQuery)*;
mediaQuery: (ONLY | NOT)? mediaType (AND mediaExpression)*
          | mediaExpression (AND mediaExpression)*;
mediaType: IDENTIFIER;
mediaExpression: LPAREN mediaFeature (COLON expr)? RPAREN;
mediaFeature: IDENTIFIER;

// @keyframes
keyframesRule: AT_KEYFRAMES IDENTIFIER block;
keyframeBlock: keyframeSelector block;
keyframeSelector: PERCENT | FROM | TO;


// @font-face
fontFaceRule: AT_FONT_FACE block;

// @supports
supportsRule: AT_SUPPORTS supportsCondition block;
supportsCondition: supportsInParens (AND supportsInParens)* (OR supportsInParens)*;
supportsInParens: (NOT)? LPAREN declaration RPAREN;

//  القواعد الخاصة
atRule: charsetRule | importRule | mediaQueryRule | keyframesRule | fontFaceRule | supportsRule;



block: LBRACE (declaration | cssRule)* RBRACE;


cssRule: selectorGroup block;

//  Selectors
selectorGroup: selector (COMMA selector)*;
selector: simpleSelector (combinator simpleSelector)*;
combinator: (PLUS | GT | TILDE) | WS;

simpleSelector: elementName? (idSelector | classSelector | attributeSelector | pseudoSelector | elementName)*;
elementName: IDENTIFIER | STAR;
idSelector: HASH IDENTIFIER;
classSelector: DOT IDENTIFIER;
attributeSelector: LBRACK attributeName (attributeMatcher attributeValue)? RBRACK;
attributeName: IDENTIFIER;
attributeMatcher: (EQ | STAR EQ | TILDE EQ | PIPE EQ)?;
attributeValue: STRING | IDENTIFIER;
pseudoSelector: COLON COLON? IDENTIFIER;

// Declarations
declaration: property COLON value SEMICOLON?;
property: IDENTIFIER | CUSTOM_PROPERTY;
important: IMPORTANT;


value: expr important?;
expr: term WS* term* | term  (operator term)*;

term:
    functionValue
    |literalValue
    | URL
    | calcFunction
    | varFunction
    | IDENTIFIER
    ;

literalValue:
    NUMBER unit?
    | INTEGER unit?
    | STRING
    | HEX_COLOR
    | color_name
    | keywordValue
    |operator
    ;

unit:
    PX | EM | REM | VH | VW | VMIN | VMAX
    | PERCENT | DEG | GRAD | RAD | TURN
    | MS | S | CM | MM | IN | PT | PC | CH | EX | FR;

keywordValue:
    AUTO | NONE | INITIAL | INHERIT | REVERT | UNSET
    | TRANSPARENT | CURRENTCOLOR
    | FLEX | GRID | BLOCK | INLINE | INLINE_BLOCK | INLINE_FLEX | INLINE_GRID | TABLE
    | RELATIVE | ABSOLUTE | FIXED | STATIC | STICKY
    | CENTER | LEFT | RIGHT | TOP | BOTTOM | START | END | JUSTIFY
    | LTR | RTL
    | TRUE | FALSE
    | MAX_CONTENT | MIN_CONTENT | FIT_CONTENT
    ;

color_name: WHITE | BLACK | RED | GREEN | BLUE | YELLOW | PURPLE | ORANGE | GRAY | GREY;

operator: (PLUS | MINUS | STAR | SLASH | COMMA |PERCENT) WS?;


functionValue: FUNCTION funcContent?  SEMICOLON?;
funcContent: FUNC_CONTENT;
//funcContent:  expr;

calcFunction: CALC expr RPAREN;
varFunction: VAR CUSTOM_PROPERTY (COMMA expr)? RPAREN;