package es.caib.pluginsib.arxiu.caib.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.ExceptionResult;
import es.caib.pluginsib.arxiu.caib.ArxiuConversioHelper;

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
				"PROTECCIÓ DADES.pdf",
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
	
	@Test
	public void jsonMapperTest() {
		String json = "{\n\t\t\"exception\":{\n\t\t\t\"code\":\"COD_021\",\n\t\t\t\"description\":\"Duplicate child name not allowed: nom\n.pdf\"}}";
		System.out.println("JSON: " + json);
		ObjectMapper mapper = new ObjectMapper();
		
		// Permet rebre un sol objecte en el lloc a on hi hauria d'haver una llista.
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		// Mecanisme de deserialització dels enums
		mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
		// Per a no serialitzar propietats amb valors NULL
		mapper.setSerializationInclusion(Include.NON_NULL);

		// Permetre salts de línia en el valors
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
	
		
		try {
		ExceptionResult exceptionResult = mapper.readValue(
				json,
				ExceptionResult.class);
		System.out.println("Exception.description: " + exceptionResult.getException().getDescription());
		} catch(Exception e) {
			fail("Error mapejant el json: " + e.getMessage());
		}
	}

}
