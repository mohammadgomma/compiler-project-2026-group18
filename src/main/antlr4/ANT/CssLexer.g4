

lexer grammar CssLexer;

/////////////////////////////////////////////////////////////////////////////

LBRACE: '{';
RBRACE: '}';
SEMICOLON: ';';
COLON: ':';
COMMA: ',';
LPAREN: '(';
RPAREN: ')';
LBRACK: '[';
RBRACK: ']';
STAR: '*';
DOT: '.';
HASH: '#';
AT: '@';
PERCENT: '%';
SLASH: '/';
PLUS: '+';
MINUS: '-';
GT: '>';
LT: '<';
TILDE: '~';
DOLLAR: '$';
PIPE: '|';
EQ: '=';
EXCLAMATION: '!';


PX: 'px';
EM: 'em';
REM: 'rem';
VH: 'vh';
VW: 'vw';
VMAX: 'vmax';
VMIN: 'vmin';
// PERCENTAGE: '%';
DEG: 'deg';
GRAD: 'grad';
RAD: 'rad';
TURN: 'turn';
MS: 'ms';
S: 's';
CM: 'cm';
MM: 'mm';
IN: 'in';
PT: 'pt';
PC: 'pc';
CH: 'ch';
EX: 'ex';
FR: 'fr';


IMPORTANT: '!important';
AUTO: 'auto';
NONE: 'none';
INITIAL: 'initial';
INHERIT: 'inherit';
REVERT: 'revert';
UNSET: 'unset';
TRANSPARENT: 'transparent';
CURRENTCOLOR: 'currentColor';

FLEX: 'flex ';
GRID: 'grid ';
BLOCK: 'block ';
INLINE: 'inline ';
INLINE_BLOCK: 'inline-block ';
INLINE_FLEX: 'inline-flex ';
INLINE_GRID: 'inline-grid ';
TABLE: 'table ';
RELATIVE: 'relative ';
ABSOLUTE: 'absolute ';
FIXED: 'fixed ';
STATIC: 'static ';
STICKY: 'sticky ';

CENTER: 'center ';
LEFT: 'left ';
RIGHT: 'right ';
TOP: 'top ';
BOTTOM: 'bottom ';
START: 'start ';
END: 'end ';
JUSTIFY: 'justify ';


LTR: 'ltr ';
RTL: 'rtl ';

TRUE: 'true ';
FALSE: 'false ';



FROM: 'from';
TO: 'to';


MAX_CONTENT: 'max-content';
MIN_CONTENT: 'min-content';
FIT_CONTENT: 'fit-content';

AT_MEDIA: '@media';
AT_IMPORT: '@import';
AT_KEYFRAMES: '@keyframes';
AT_FONT_FACE: '@font-face';
AT_PAGE: '@page';
AT_SUPPORTS: '@supports';
AT_CHARSET: '@charset';

ONLY: 'only';
NOT: 'not';
AND: 'and';
OR: 'or';

WHITE: 'white';
BLACK: 'black';
RED: 'red';
GREEN: 'green';
BLUE: 'blue';
YELLOW: 'yellow';
PURPLE: 'purple';
ORANGE: 'orange';
GRAY: 'gray';
GREY: 'grey';

IDENTIFIER: [a-zA-Z_][a-zA-Z0-9_-]*;
CUSTOM_PROPERTY: '--' [a-zA-Z_][a-zA-Z0-9_-]*;

NUMBER: [0-9]+ ('.' [0-9]+)?;
INTEGER: [0-9]+;

HEX_COLOR: '#' [0-9a-fA-F]+;

STRING: '"' (~["\r\n] | '\\"')* '"'
      | '\'' (~['\r\n] | '\\\'')* '\'';

FUNCTION: IDENTIFIER '(' -> pushMode(IN_FUNCTION);
CALC:'calc(';
VAR:'var(';
URL: 'url(' -> pushMode(IN_URL);

COMMENT: '/*' .*? '*/' -> skip;
LINE_COMMENT: '//' ~[\r\n]* -> skip;

WS: [ \t\r\n\f]+ -> skip;

/////////////////////////////////////////////////////////////

mode IN_FUNCTION;

FUNC_RPAREN: ')' -> type(RPAREN);

FUNC_CONTENT:~[;]+;

FUNC_COMMA: ',' -> type(COMMA);
FUNC_LPAREN: '(' -> type(LPAREN);
FUNC_RBRACE: '}' -> type(RBRACE);
FUNC_LBRACE: '{' -> type(LBRACE);
FUNC_COLON: ':' -> type(COLON);
FUNC_SEMICOLON: ';' ->  popMode,type(SEMICOLON);
FUNC_PERCENT: '%' -> type(PERCENT);
FUNC_DOT: '.' -> type(DOT);
FUNC_PLUS: '+' -> type(PLUS);
FUNC_MINUS: '-' -> type(MINUS);
FUNC_STAR: '*' -> type(STAR);
FUNC_SLASH: '/' -> type(SLASH);

FUNC_PX: 'px' -> type(PX);
FUNC_EM: 'em' -> type(EM);
FUNC_REM: 'rem' -> type(REM);
FUNC_VH: 'vh' -> type(VH);
FUNC_VW: 'vw' -> type(VW);
FUNC_DEG: 'deg' -> type(DEG);
// FUNC_PERCENTAGE: '%' -> type(PERCENTAGE);
FUNC_MS: 'ms' -> type(MS);
FUNC_S: 's' -> type(S);
FUNC_FR: 'fr' -> type(FR);


FUNC_TO: 'to';
FUNC_FROM: 'from';
FUNC_CENTER: 'center' -> type(CENTER);
FUNC_LEFT: 'left' -> type(LEFT);
FUNC_RIGHT: 'right' -> type(RIGHT);
FUNC_TOP: 'top' -> type(TOP);
FUNC_BOTTOM: 'bottom' -> type(BOTTOM);


FUNC_NUMBER: [0-9]+ ('.' [0-9]+)? -> type(NUMBER);
FUNC_INTEGER: [0-9]+ -> type(INTEGER);
FUNC_HEX_COLOR: '#' [0-9a-fA-F]+ -> type(HEX_COLOR);
FUNC_STRING: '"' (~["] | '\\"')* '"' -> type(STRING);
FUNC_IDENTIFIER: [a-zA-Z_][a-zA-Z0-9_-]* -> type(IDENTIFIER);
FUNC_WS: [ \t\r\n\f]+ -> skip;

////////////////////////////////////////////////////////////

mode IN_URL;

URL_RPAREN: ')' -> popMode;
URL_CONTENT: ~[)]+;






