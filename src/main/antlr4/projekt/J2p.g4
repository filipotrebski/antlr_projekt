grammar J2p;

compilationUnit: pkg?
imports*
class_def
EOF;


pkg: 'package' qualifiedName ';' ;
imports: 'import' qualifiedName ';';

class_def: modificator? 'class' Identifier classBody;

method: modificator? staticType? returnType methodName '(' (argumentsDefList) ')' block;

fieldDef: modificator? staticType? qualifiedName variable '=' literal ';' ;

variableDef: qualifiedName variable '=' literal ';';
variable: Identifier ;

classBody: '{' classMember* '}';
classMember: statement
       | fieldDef
       ;

block:  '{' blockStatement '}';

blockStatement: blockElement* ;

blockElement: statement
   | variableDef
   ;

statement: method
| methodCall;
// qualifiedName(argumentsList)
methodCall: qualifiedName '(' argumentsList')' ';';


argumentsDefList: argumentDef? (',' argumentDef)* ;
argumentDef: qualifiedName'[]'? Identifier;

argumentsList: argument? (',' argument)* ;
argument: literal
| variable;

literal: StringLiteral ;

StringLiteral:	'"' ~["\\]* '"';




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
//VALUE: ["a-zA-Z0-9];
//NAME: [a-zA-Z0-9];
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