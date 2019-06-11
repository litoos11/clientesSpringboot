package com.litoos11.bolsaideas.app.component;

public class PaginaComponent {
	private int numero;
	private boolean actual;

	public PaginaComponent(int numero, boolean actual) {
		this.numero = numero;
		this.actual = actual;
	}

	public int getNumero() {
		return numero;
	}

	public boolean isActual() {
		return actual;
	}

}
