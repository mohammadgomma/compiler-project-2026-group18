lexer grammar PythonLexer;

options { superClass=PythonLexerBase; }



DEF: 'def';
RETURN: 'return';
IF: 'if';
ELIF: 'elif';
ELSE: 'else';
FOR: 'for';
WHILE: 'while';
IMPORT: 'import';
FROM: 'from';
IN: 'in';
IS: 'is';
NOT: 'not';
AND: 'and';
OR: 'or';
NONE: 'None';
TRUE: 'True';
FALSE: 'False';
AS: 'as';

DOT: '.';
PLUS: '+';
MINUS: '-';
MULT: '*';
DIV: '/';
MOD: '%';
ASSIGN: '=';
EQ: '==';
NEQ: '!=';
LT: '<';
GT: '>';
LE: '<=';
GE: '>=';
PLUS_ASSIGN: '+=';
MINUS_ASSIGN: '-=';

LPAREN: '(' { incBrackets(); };
RPAREN: ')' { decBrackets(); };
LBRACK: '[' { incBrackets(); };
RBRACK: ']' { decBrackets(); };
LBRACE: '{' { incBrackets(); };
RBRACE: '}' { decBrackets(); };
COMMA: ',';
COLON: ':';
AT: '@';

INTEGER: [0-9]+;
FLOAT: [0-9]+ '.' [0-9]* | '.' [0-9]+;
STRING: '"' (~["\r\n] | '\\' .)* '"'
      | '\'' (~['\r\n] | '\\' .)* '\'';

DNAME: '__name__' | '__main__';
NAME: [a-zA-Z_][a-zA-Z0-9_]*;

NEWLINE: ( '\r'? '\n' [\t ]* )+ { handleNewLine(); } ;
WS: [ \t]+ -> skip ;
COMMENT: '#' ~[\r\n]* -> skip ;

INDENT: '<INDENT>' ;
DEDENT: '<DEDENT>' ;
