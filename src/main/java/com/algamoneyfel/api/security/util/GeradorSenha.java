package com.algamoneyfel.api.security.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeradorSenha {
	
	public static void main(String[] args) {
		
		/* Ir no Run do menu Spring tools depois Run As e java Aplication para 
		 * executar esta classe GeradorSenha  */
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		System.out.println(encoder.encode("m0b1l30")); // coloque qualquer texto aqui para gerar um encode
	}

}
