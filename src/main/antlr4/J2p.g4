grammar J2p;

compilationUnit: pkg?
imports*
class_def
EOF;


pkg: 'package' qualifiedName ';' ;
imports: 'import' qualifiedName ';';

class_def: modificator? 'class' Identifier block;

method: modificator? staticType? returnType methodName '(' (argumentsDefList) ')' block;
variableDef: modificator? staticType? qualifiedName NAME '=' VALUE ';';

block:  '{' blockStatement '}';

blockStatement
    :   statement*
    ;
//Variables

statement: method
| methodCall;
// qualifiedName(argumentsList)
methodCall: qualifiedName '(' argumentsList')' ';';


argumentsDefList: argumentDef? (',' argumentDef)* ;
argumentDef: qualifiedName'[]'? Identifier;

argumentsList: argument? (',' argument)* ;
argument: literal;

literal: StringLiteral ;

StringLiteral:	'"' ~["\\]* '"';


//variable: Identifier ;


modificator
    : 'public'
    | 'private'
    ;

staticType: 'static';

returnType: qualifiedName;

qualifiedName
    :   Identifier ('.' Identifier)*
    ;
methodName: Identifier;
Identifier: [a-zA-Z] [a-zA-Z0-9]*;
VALUE: ["a-zA-Z0-9];
NAME: [a-zA-Z0-9];
//PACKAGE : [a-zA-Z0-9];
//FULL_CLASS: [a-zA-Z0-9];

WS  :  [ \t\r\n\u000C]+ -> skip
    ;

COMMENT
    :   '/*' .*? '*/' -> skip
    ;

LINE_COMMENT
    :   '//' ~[\r\n]* -> skip
    ;