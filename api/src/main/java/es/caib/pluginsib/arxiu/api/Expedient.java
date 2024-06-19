/**
 * 
 */
package es.caib.pluginsib.arxiu.api;

import java.util.List;

/**
 * Informació d'un contingut de l'arxiu de tipus expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Expedient extends ContingutArxiu {

	private List<ContingutArxiu> continguts;

	public Expedient() {
		super(ContingutTipus.EXPEDIENT);
	}

	public ExpedientMetadades getMetadades() {
		return expedientMetadades;
	}
	public void setMetadades(ExpedientMetadades metadades) {
		this.expedientMetadades = metadades;
	}
	public List<ContingutArxiu> getContinguts() {
		return continguts;
	}
	public void setContinguts(List<ContingutArxiu> continguts) {
		this.continguts = continguts;
	}

}
