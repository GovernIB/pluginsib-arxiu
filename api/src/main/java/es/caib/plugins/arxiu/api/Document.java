/**
 * 
 */
package es.caib.plugins.arxiu.api;

/**
 * Informació d'un contingut de l'arxiu de tipus document.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Document extends ContingutArxiu {

	private DocumentEstat estat;
	private DocumentContingut contingut;

	public Document() {
		super(ContingutTipus.DOCUMENT);
	}

	public DocumentEstat getEstat() {
		return estat;
	}
	public void setEstat(DocumentEstat estat) {
		this.estat = estat;
	}
	public DocumentMetadades getMetadades() {
		return documentMetadades;
	}
	public void setMetadades(DocumentMetadades metadades) {
		this.documentMetadades = metadades;
	}
	public DocumentContingut getContingut() {
		return contingut;
	}
	public void setContingut(DocumentContingut contingut) {
		this.contingut = contingut;
	}

}
