package com.algamoneyfel.api.exceptionhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

// Classe que voce pode costumizar as exceções ocorridas no App
@ControllerAdvice
public class AlgamoneyfelExceptionHandler extends ResponseEntityExceptionHandler {
	
	@Autowired
	private MessageSource msgSource;
	
	@Override // Exception que tra atibutos inválido ou desconhecidos 
	protected ResponseEntity<Object>  handleHttpMessageNotReadable(HttpMessageNotReadableException ex, 
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		// Primeriro parametro de getMessage() mensagem.invalida vinda do
		//  arquivo messages.properties que contém a mensagem de exceção.
		String msgUsuario = msgSource.getMessage("mensagem.invalida", null, LocaleContextHolder.getLocale());
		String msgDesenvolvedor = ex.getCause() != null ? ex.getCause().toString() : ex.toString();
		                            // Classe Erro instanciada com os parametros inseridos com o metodo construtor
		List<Erro> erros = Arrays.asList(new Erro(msgUsuario, msgDesenvolvedor));
		
		return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
	}
	
	//basta digitar o nome do metodo que gera automaticamente
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
	    
		List<Erro> erros = criarListaDeErros(ex.getBindingResult());
		return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
	}
	
	// Metodo que retorna uma mensagem costumizada para o Status NotFound 404 desta Exception
	@ExceptionHandler({ EmptyResultDataAccessException.class })
	public ResponseEntity<Object> emptyResultDataAccessException(EmptyResultDataAccessException ex, WebRequest request) {
		
		// Primeriro parametro de getMessage() recurso.nao-encontrado vinda do
		// arquivo messages.properties que contém a mensagem de exceção.
		String msgUsuario = msgSource.getMessage("recurso.nao-encontrado", null, LocaleContextHolder.getLocale());
		// Nesse não precisa do .getCause() pois jah eh uma exceção pronta para o metodo remover()
		String msgDesenvolvedor = ex.toString();  
		List<Erro> erros = Arrays.asList(new Erro(msgUsuario, msgDesenvolvedor));
		
		return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}
	
	@ExceptionHandler({ DataIntegrityViolationException.class })
	public ResponseEntity<Object> dataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
		
				String msgUsuario = msgSource.getMessage("recurso.operacao-nao-permitida", null, LocaleContextHolder.getLocale());
				String msgDesenvolvedor = ExceptionUtils.getRootCauseMessage(ex); // Dependencia Commons lang3 para verificar o tipo de erro SQL nas Exceptions 
				List<Erro> erros = Arrays.asList(new Erro(msgUsuario, msgDesenvolvedor));
				
				return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}
	
	// Aqui Vai listar todos os erros com o bindingResult
	private List<Erro> criarListaDeErros(BindingResult bindingResult) {
		
		List<Erro> erros = new ArrayList<>();
		for(FieldError fieldError : bindingResult.getFieldErrors()) {
	
			String msgUsuario = msgSource.getMessage(fieldError, LocaleContextHolder.getLocale()); 
			String msgDesenvolvedor = fieldError.toString();
			
			erros.add(new Erro(msgUsuario, msgDesenvolvedor));
		}
		
		return erros;
	}
	
	public static class Erro {
		
		private String msgUsuario;
		private String msgDesenvolvedor;
		
		// metodo contrutor 
		public Erro(String msgUsuario, String msgDesenvolvedor) {
			
			this.msgUsuario = msgUsuario;
			this.msgDesenvolvedor = msgDesenvolvedor;
		}

		public String getMsgUsuario() {
			return msgUsuario;
		}

		public String getMsgDesenvolvedor() {
			return msgDesenvolvedor;
		}
		
		
		
		
	
	}

}
