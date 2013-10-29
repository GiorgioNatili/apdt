package com.gnstudio.apdt.pseudo.core;

import java.util.HashSet;
import java.util.Set;

public enum PseudoKeywords {

	PROGRAM, PACKAGE, CLASS, INTERFACE, ENDPROGRAM,
	ENDPACKAGE, ENDCLASS, ENDINTERFACE, EXTENDS, IMPLEMENTS,
	PROCEDURE, ENDPROCEDURE, ARGUMENTS, RETURNING,
	RECOVER, SET,SHOW,PROMPT, CALCULATE,VAR, READ, WRITE, PRINT,
	RETURN, RECURSION,CALL,USING,IF, ELSE ,THEN, ENDIF,
	FOR,ENDFOR, EACH,REPEAT, UNTIL ,WHILE,
	DO, ENDWHILE,ENDREPEAT,TRY,ENDTRY, CATCH, FINALLY,
	CASE,IS,OTHERS,ENDCASE
	;

	private static final Set<String> keywords = new HashSet<String>();
	static {
		for (PseudoKeywords keyword : PseudoKeywords.values()) {
			keywords.add(keyword.name().toLowerCase());
		}
	}

	public static boolean iskeyWord(String word) {
		return keywords.contains(word);
	}
}
