/**
 * 
 */
package es.caib.pluginsib.arxiu.api;

/**
 * Excepció que es produeix quan no es troba el sèrie documental
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SerieDocumentalNotFoundException extends ArxiuException {


	public SerieDocumentalNotFoundException() {
		super("Serie documental no trobat");
	}

	private static final long serialVersionUID = 6220288486244096013L;

}
