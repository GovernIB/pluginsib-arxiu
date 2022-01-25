/**
 * 
 */
package es.caib.plugins.arxiu.api;

import java.util.List;

/**
 * Informació sobre un contingut genèric de l’arxiu.
 * Aquest contingut pot ser de tipus document, expedient
 * o carpeta.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ContingutArxiu {

	protected String identificador;
	protected String nom;
	protected String descripcio;
	protected ContingutTipus tipus;
	protected String versio;
	protected List<Firma> firmes;
	protected ExpedientMetadades expedientMetadades;
	protected DocumentMetadades documentMetadades;
	
	public ContingutArxiu(ContingutTipus tipus) {
		super();
		this.tipus = tipus;
	}

	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public ContingutTipus getTipus() {
		return tipus;
	}
	public String getVersio() {
		return versio;
	}
	public void setVersio(String versio) {
		this.versio = versio;
	}
	public List<Firma> getFirmes() {
		return firmes;
	}
	public void setFirmes(List<Firma> firmes) {
		this.firmes = firmes;
	}
	public ExpedientMetadades getExpedientMetadades() {
		return expedientMetadades;
	}
	public void setExpedientMetadades(ExpedientMetadades expedientMetadades) {
		this.expedientMetadades = expedientMetadades;
	}
	public DocumentMetadades getDocumentMetadades() {
		return documentMetadades;
	}
	public void setDocumentMetadades(DocumentMetadades documentMetadades) {
		this.documentMetadades = documentMetadades;
	}

}
