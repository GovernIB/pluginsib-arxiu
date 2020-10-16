/**
 * 
 */
package es.caib.plugins.arxiu.api;

/**
 * Tipus de document NTI addicional als tipus bàsics.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentTipusAddicional {

	private String codi;
	private String descripcio;

	public DocumentTipusAddicional(String codi, String descripcio) {
		super();
		this.codi = codi;
		this.descripcio = descripcio;
	}
	public String getCodi() {
		return codi;
	}
	public String getDescripcio() {
		return descripcio;
	}

	@Override
	public String toString() {
		return "(" + codi + ") " + descripcio;
	}

}
