package es.caib.plugins.arxiu.caib.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

import org.junit.Test;

import es.caib.plugins.arxiu.caib.ArxiuConversioHelper;

/**
 * Test d'alguns mètodes de la classe ArxiuConversioHelper.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArxiuConversioHelperTest {
	
	/** Comprova que diferents noms són vàlids com a noms d'Arxiu després de la conversió. */
	@Test
	public void revisarContingutNomTest() {
		String[] nomsProva = new String[] {
				" Caça Pròva\nd'invàl·lida 2", 	// Caça Pròva d invàl·lida 2
				"prova?.",						// prova
				" prova\tnom d'arxiu miscel·lània (e^2).pdf. ",
				" hòlaÁÑÇaa !|*\\{}[]?"
				
		};
		String nom, nomCorregit;
		boolean correcte;
		for (int i =0; i<nomsProva.length; i++) {
			nom = nomsProva[i];
			nomCorregit = ArxiuConversioHelper.revisarContingutNom(nom);
			correcte = this.pathCorrecte(nomCorregit);
			System.out.println("\"" + nom + "\"-> \"" + nomCorregit + "\"| correcte? " + correcte);
			assertTrue("S'ha detectat un nom d'arxiu corregit incorrecte: \"" + nomCorregit + "\"", correcte);
		}
	}
	
	/** Comprova que el nom sigui correcte. */
	private boolean pathCorrecte(String path) {
		
		// Comprova que el path sigui correcte
		try {
            Paths.get(path);
        } catch (InvalidPathException ex) {
            return false;
        }
		// Comprova que el nom no tingui * ni ?
		File f = new File(path);
	    try {
	       f.getCanonicalPath();
	       return true;
	    }
	    catch (IOException e) {
	       return false;
	    }
 	}

}
