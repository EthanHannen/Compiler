// Little Lexer

grammar Little;

r: ;

fragment LOWER : [a-z] ;
fragment UPPER : [A-Z] ;
fragment DIGIT : [0-9] ;

COMMENT        : '--' ~[\r\n]* -> skip ; // skip comments
WHITESPACE     : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines
OPERATOR       : ':=' | '+' | '-' | '*' | '/' | '=' | '!=' | '<' | '>' | '(' | ')' | ';' | ',' | '<=' | '>=' ;

KEYWORD        :
	'PROGRAM'  |
	'BEGIN'    |
	'END'      |
	'FUNCTION' |
	'READ'     |
	'WRITE'    |
	'IF'       |
	'ELSE'     |
	'ENDIF'    |
	'WHILE'    |
	'ENDWHILE' |
	'CONTINUE' |
	'BREAK'    |
	'RETURN'   |
	'INT'      |
	'VOID'     |
	'STRING'   |
	'FLOAT'    ;

FLOATLITERAL   : (DIGIT)*'.'(DIGIT)+ ;
INTLITERAL     : (DIGIT)+ ;
IDENTIFIER     : (LOWER | UPPER)+(LOWER | UPPER | DIGIT)* ;
STRINGLITERAL  : '"' .*? '"' ;
