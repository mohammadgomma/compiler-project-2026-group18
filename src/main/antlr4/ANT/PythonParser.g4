parser grammar PythonParser;

options { tokenVocab=PythonLexer; }



program: (statement)* EOF;

statement:
      simple_stmt
    | compound_stmt
    | NEWLINE
    ;

simple_stmt: small_stmt (NEWLINE | EOF) ;

small_stmt:
      import_stmt
    | assign_stmt
    | expr_stmt
    | return_stmt
    ;

compound_stmt:
      func_def
    | if_stmt
    ;

import_stmt:
      FROM module_name IMPORT imported_names
    | IMPORT import_list
    ;

imported_names: imported_name (COMMA imported_name)* ;
imported_name: NAME (AS NAME)? ;
import_list: module_name (COMMA module_name)* ;
module_name: NAME (DOT NAME)* (AS NAME)? ;

assign_stmt:
      test (COMMA test)* ASSIGN test (COMMA test)*
    ;

expr_stmt:
      test
    ;

return_stmt:
      RETURN test?
    ;

func_def:
      decorators? DEF NAME parameters COLON suite
    ;

decorators: decorator+ ;
decorator: AT dotted_name (LPAREN arglist? RPAREN)? NEWLINE? ;
dotted_name: NAME (DOT NAME)* ;

parameters: LPAREN (param_list)? RPAREN ;
param_list: param (COMMA param)* ;
param: NAME (ASSIGN test)? ;

if_stmt:
      IF test COLON suite
      (ELIF test COLON suite)*
      (ELSE COLON suite)?
    ;

suite:
      simple_stmt
    | NEWLINE INDENT statement+ DEDENT
    ;

test:
      or_test
    ;

or_test: and_test (OR and_test)* ;
and_test: not_test (AND not_test)* ;
not_test: NOT not_test | comparison ;

comparison: expr (comp_op expr)* ;
comp_op: LT | GT | EQ | GE | LE | NEQ | IS NOT | IS | IN | NOT IN ;

expr: term ( (PLUS|MINUS) term )* ;
term: factor ( (MULT|DIV|MOD) factor )* ;
factor: (PLUS|MINUS) factor | power ;
power: atom_expr ;

atom_expr:
      atom trailer*
    ;

atom:
      NAME
    | DNAME
    | INTEGER
    | FLOAT
    | STRING+
    | TRUE | FALSE | NONE
    | LPAREN test? RPAREN
    | LBRACK (test (COMMA test)* COMMA?)? RBRACK
    | LBRACE (dictorsetmaker)? RBRACE
    ;

dictorsetmaker:
      test COLON test (COMMA test COLON test)* COMMA?
    ;

trailer:
      LPAREN arglist? RPAREN
    | LBRACK test RBRACK
    | DOT NAME
    ;

arglist: argument (COMMA argument)* COMMA? ;
argument:
      test
    | NAME ASSIGN test
    ;
