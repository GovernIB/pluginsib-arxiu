package es.caib.plugins.arxiu.filesystem;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import es.caib.plugins.arxiu.api.AbstractArxiuPlugin;
import es.caib.plugins.arxiu.api.ArxiuException;
import es.caib.plugins.arxiu.api.Carpeta;
import es.caib.plugins.arxiu.api.ConsultaFiltre;
import es.caib.plugins.arxiu.api.ConsultaResultat;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.ContingutTipus;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.DocumentEstat;
import es.caib.plugins.arxiu.api.DocumentRepositori;
import es.caib.plugins.arxiu.api.DocumentTipusAddicional;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.plugins.arxiu.api.ExpedientEstat;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.plugins.arxiu.api.FirmaTipus;
import es.caib.plugins.arxiu.api.IArxiuPlugin;

public class ArxiuPluginFilesystem extends AbstractArxiuPlugin implements IArxiuPlugin {

	private static final String ARXIUFILESYSTEM_BASE_PROPERTY = ARXIU_BASE_PROPERTY + "filesystem.";

	private FileSystemHelper filesystemHelper;

	public ArxiuPluginFilesystem() {
		super();
	}
	public ArxiuPluginFilesystem(Properties properties) {
		super("", properties);
	}
	public ArxiuPluginFilesystem(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}
	public ArxiuPluginFilesystem(String propertyKeyBase) {
		super(propertyKeyBase);
	}

	@Override
	public ContingutArxiu expedientCrear(
			Expedient expedient) throws ArxiuException {
		try {
			LuceneHelper luceneHelper = getLuceneHelper();
			String uuid = UUID.randomUUID().toString();
			String identificadorEni = generarIdentificadorEni(
					uuid,
					true);
			String path = getFilesystemHelper().expedientCrear(
					expedient,
					identificadorEni);
			luceneHelper.expedientCrear(
					expedient,
					uuid,
					identificadorEni,
					path);
			return crearContingutArxiu(
					uuid, 
					expedient.getNom(),
					ContingutTipus.EXPEDIENT,
					null);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error creant l'expedient: " + ex.getMessage(),
					ex);
		} finally {
			try {
				LuceneHelper.closeLuceneWriter();
			} catch (IOException ex) {
				throw new ArxiuException(
						"No s'ha pogut tancar l'índex de lucene writer" + ex.getMessage(),
						ex);
			}
		}
	}

	@Override
	public ContingutArxiu expedientModificar(
			Expedient expedient) throws ArxiuException {
		try {
			LuceneHelper luceneHelper = getLuceneHelper();
			comprovarExpedientEstat(
					ExpedientEstat.OBERT,
					luceneHelper.getExpedientPareEstat(
							expedient.getIdentificador()));
			luceneHelper.expedientModificar(
					expedient);
			return crearContingutArxiu(
					expedient.getIdentificador(), 
					expedient.getNom(),
					ContingutTipus.EXPEDIENT,
					null);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error modificant l'expedient: " + ex.getMessage(),
					ex);
		} finally {
			try {
				LuceneHelper.closeLuceneWriter();
			} catch (IOException ex) {
				throw new ArxiuException(
						"No s'ha pogut tancar l'índex de lucene writer" + ex.getMessage(),
						ex);
			}
		}
	}

	@Override
	public void expedientEsborrar(
			String identificador) throws ArxiuException {
		try {
			LuceneHelper luceneHelper = getLuceneHelper();
			comprovarExpedientEstat(
					ExpedientEstat.OBERT,
					luceneHelper.getExpedientPareEstat(
							identificador));
			comprovarExpedientNoConteDocumentsDefinitius(
					identificador,
					luceneHelper);
			String path = luceneHelper.getPath(
					identificador);
			getFilesystemHelper().directoriEsborrar(path);
			luceneHelper.contingutEsborrar(
					identificador);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error esborrant l'expedient: " + ex.getMessage(),
					ex);
		} finally {
			try {
				LuceneHelper.closeLuceneWriter();
			} catch (IOException ex) {
				throw new ArxiuException(
						"No s'ha pogut tancar l'índex de lucene writer" + ex.getMessage(),
						ex);
			}
		}
	}

	@Override
	public Expedient expedientDetalls(
			String identificador,
			String versio) throws ArxiuException {
		try {
			LuceneHelper luceneHelper = getLuceneHelper();
			if (versio != null) {
				throw new ArxiuException(
						"Aquesta implementació de l'API d'arxiu no suporta el versionat d'expedients");
			}
			return luceneHelper.expedientDetalls(identificador);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error al obtenir els detalls de l'expedient: " + ex.getMessage(),
					ex);
		} finally {
			try {
				LuceneHelper.closeLuceneWriter();
			} catch (IOException ex) {
				throw new ArxiuException(
						"No s'ha pogut tancar l'índex de lucene writer" + ex.getMessage(),
						ex);
			}
		}
	}

	@Override
	public ConsultaResultat expedientConsulta(
			List<ConsultaFiltre> filtres,
			Integer pagina,
			Integer itemsPerPagina) throws ArxiuException {
		try {
			LuceneHelper luceneHelper = getLuceneHelper();
			return luceneHelper.contingutCercar(
					ContingutTipus.EXPEDIENT,
					filtres,
					pagina,
					itemsPerPagina);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error en la consulta d'expedients: " + ex.getMessage(),
					ex);
		} finally {
			try {
				LuceneHelper.closeLuceneWriter();
			} catch (IOException ex) {
				throw new ArxiuException(
						"No s'ha pogut tancar l'índex de lucene writer" + ex.getMessage(),
						ex);
			}
		}
	}

	@Override
	public ContingutArxiu expedientCrearSubExpedient(
			final Expedient expedient, 
			final String identificadorPare) throws ArxiuException {
		try {
			LuceneHelper luceneHelper = getLuceneHelper();
			comprovarExpedientEstat(
					ExpedientEstat.OBERT,
					luceneHelper.getExpedientPareEstat(
							identificadorPare));
			String uuid = UUID.randomUUID().toString();
			String identificadorEni = generarIdentificadorEni(
					uuid,
					true);
			String parePath = luceneHelper.getPath(
					identificadorPare);
			String path = getFilesystemHelper().subExpedientCrear(
					parePath,
					identificadorEni);
			luceneHelper.expedientCrear(
					expedient,
					uuid,
					identificadorEni,
					path);
			return crearContingutArxiu(
					uuid, 
					expedient.getNom(),
					ContingutTipus.EXPEDIENT,
					null);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error creant el subexpedient: " + ex.getMessage(),
					ex);
		} finally {
			try {
				LuceneHelper.closeLuceneWriter();
			} catch (IOException ex) {
				throw new ArxiuException(
						"No s'ha pogut tancar l'índex de lucene writer" + ex.getMessage(),
						ex);
			}
		}
	}

	@Override
	public List<ContingutArxiu> expedientVersions(
			String identificador) throws ArxiuException {
		throw new ArxiuException(
				"Aquesta implementació de l'API d'arxiu no suporta el versionat d'expedients");
	}

	@Override
	public String expedientTancar(
			String identificador) throws ArxiuException {
		try {
			LuceneHelper luceneHelper = getLuceneHelper();
			comprovarExpedientEstat(
					ExpedientEstat.OBERT,
					luceneHelper.getExpedientPareEstat(
							identificador));
			luceneHelper.expedientCanviEstat(
					identificador,
					ExpedientEstat.TANCAT);
			return null;
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error tancant l'expedient: " + ex.getMessage(),
					ex);
		} finally {
			try {
				LuceneHelper.closeLuceneWriter();
			} catch (IOException ex) {
				throw new ArxiuException(
						"No s'ha pogut tancar l'índex de lucene writer" + ex.getMessage(),
						ex);
			}
		}
	}

	@Override
	public void expedientReobrir(
			String identificador) throws ArxiuException {
		try {
			LuceneHelper luceneHelper = getLuceneHelper();
			comprovarExpedientEstat(
					ExpedientEstat.TANCAT,
					luceneHelper.getExpedientPareEstat(
							identificador));
			luceneHelper.expedientCanviEstat(
					identificador,
					ExpedientEstat.OBERT);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error reobrint l'expedient: " + ex.getMessage(),
					ex);
		} finally {
			try {
				LuceneHelper.closeLuceneWriter();
			} catch (IOException ex) {
				throw new ArxiuException(
						"No s'ha pogut tancar l'índex de lucene writer" + ex.getMessage(),
						ex);
			}
		}
	}

	@Override
	public String expedientExportarEni(
			String identificador) throws ArxiuException {
		throw new ArxiuException(
				"El mètode expedientExportarEni no està disponible");
	}

	@Override
	public String expedientLligar(String identificadorPare, String identificadorFill) throws ArxiuException {
		throw new ArxiuException(
				"El mètode expedientLligar no està disponible");
	}

	@Override
	public void expedientDeslligar(String identificadorPare, String identificadorLligam) throws ArxiuException {
		throw new ArxiuException(
				"El mètode expedientDeslligar no està disponible");
	}

	@Override
	public ContingutArxiu documentCrear(
			Document document,
			String identificadorPare) throws ArxiuException {
		try {
			LuceneHelper luceneHelper = getLuceneHelper();
			comprovarExpedientEstat(
					ExpedientEstat.OBERT,
					luceneHelper.getExpedientPareEstat(
							identificadorPare));
			String uuid = UUID.randomUUID().toString();
			String identificadorEni = generarIdentificadorEni(
					uuid,
					false);
			String parePath = luceneHelper.getPath(identificadorPare);
			String path = getFilesystemHelper().documentActualitzar(
					parePath,
					document,
					identificadorEni);
			luceneHelper.documentCrear(
					document,
					uuid,
					identificadorPare,
					identificadorEni,
					path);
			return crearContingutArxiu(
					uuid, 
					document.getNom(),
					ContingutTipus.DOCUMENT,
					null);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error creant el document: " + ex.getMessage(),
					ex);
		} finally {
			try {
				LuceneHelper.closeLuceneWriter();
			} catch (IOException ex) {
				throw new ArxiuException(
						"No s'ha pogut tancar l'índex de lucene writer" + ex.getMessage(),
						ex);
			}
		}
	}

	@Override
	public ContingutArxiu documentModificar(
			Document document) throws ArxiuException {
		try {
			LuceneHelper luceneHelper = getLuceneHelper();
			comprovarExpedientEstat(
					ExpedientEstat.OBERT,
					luceneHelper.getExpedientPareEstat(
							document.getIdentificador()));
			comprovarDocumentEstat(
					DocumentEstat.ESBORRANY,
					luceneHelper.getDocumentEstat(
							document.getIdentificador()));
			comprovarDocumentDefinitiuAmbFirmes(document, luceneHelper);
			String parePath = luceneHelper.getParePath(
					ContingutTipus.DOCUMENT,
					document.getIdentificador());
			String identificadorEni = luceneHelper.getIdentificadorEni(
					document.getIdentificador());
			getFilesystemHelper().documentActualitzar(
					parePath,
					document,
					identificadorEni);
			luceneHelper.documentModificar(
					document);
			return crearContingutArxiu(
					document.getIdentificador(), 
					document.getNom(),
					ContingutTipus.DOCUMENT,
					null);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error modificant el document: " + ex.getMessage(),
					ex);
		} finally {
			try {
				LuceneHelper.closeLuceneWriter();
			} catch (IOException ex) {
				throw new ArxiuException(
						"No s'ha pogut tancar l'índex de lucene writer" + ex.getMessage(),
						ex);
			}
		}
	}

	@Override
	public void documentEsborrar(
			String identificador) throws ArxiuException {
		try {
			LuceneHelper luceneHelper = getLuceneHelper();
			comprovarExpedientEstat(
					ExpedientEstat.OBERT,
					luceneHelper.getExpedientPareEstat(
							identificador));
			comprovarDocumentEstat(
					DocumentEstat.ESBORRANY,
					luceneHelper.getDocumentEstat(
							identificador));
			String path = luceneHelper.getPath(identificador);
			getFilesystemHelper().directoriEsborrar(path);
			luceneHelper.contingutEsborrar(
					identificador);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error esborrant l'expedient: " + ex.getMessage(),
					ex);
		} finally {
			try {
				LuceneHelper.closeLuceneWriter();
			} catch (IOException ex) {
				throw new ArxiuException(
						"No s'ha pogut tancar l'índex de lucene writer" + ex.getMessage(),
						ex);
			}
		}
	}

	@Override
	public Document documentDetalls(
			String identificador,
			String versio,
			boolean ambContingut) throws ArxiuException {
		try {
			identificador = identificador.replace("uuid:",""); //notib adds prefix "uuid"
			if (versio != null) {
				throw new ArxiuException(
						"Aquesta implementació de l'API d'arxiu no suporta el versionat de documents");
			}
			LuceneHelper luceneHelper = getLuceneHelper();
			Document document = luceneHelper.documentDetalls(identificador);
			if (ambContingut && document.getContingut() != null) {
				String path = luceneHelper.getPath(identificador);
				document.getContingut().setContingut(
						getFilesystemHelper().documentContingut(path));
				document.getContingut().setTamany(
						document.getContingut().getContingut().length);
			} else if (ambContingut) {
				String path = luceneHelper.getPath(identificador);
				DocumentContingut contingut = new DocumentContingut();
				byte[] content = getFilesystemHelper().documentContingut(path) != null ? getFilesystemHelper().documentContingut(path) : getFilesystemHelper().documentFirma(path,0);
				contingut.setContingut(content);
				if (getFilesystemHelper().documentContingut(path) != null)
					contingut.setTamany(getFilesystemHelper().documentContingut(path).length);
				contingut.setTipusMime("application/pdf");
				document.setContingut(contingut);
			}
			if (document.getFirmes() != null) {
				String path = luceneHelper.getPath(identificador);
				for (int i = 0; i < document.getFirmes().size(); i++) {
					Firma firma = document.getFirmes().get(i);
					byte[] content = getFilesystemHelper().documentFirma(path, i) != null ? getFilesystemHelper().documentFirma(path, i) : getFilesystemHelper().documentContingut(path);
					firma.setContingut(content);
					firma.setTamany(firma.getContingut().length);
					if (firma.getTipus() != FirmaTipus.CADES_DET) {
						if (document.getContingut() != null) {
							document.getContingut().setArxiuNom(firma.getFitxerNom());
						}
					}
				}
			}
			return document;
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error al obtenir els detalls del document: " + ex.getMessage(),
					ex);
		} finally {
			try {
				LuceneHelper.closeLuceneWriter();
			} catch (IOException ex) {
				throw new ArxiuException(
						"No s'ha pogut tancar l'índex de lucene writer" + ex.getMessage(),
						ex);
			}
		}
	}

	@Override
	public ConsultaResultat documentConsulta(
			List<ConsultaFiltre> filtres,
			Integer pagina,
			Integer itemsPerPagina,
			final DocumentRepositori repositori) throws ArxiuException {
		try {
			LuceneHelper luceneHelper = getLuceneHelper();
			return luceneHelper.contingutCercar(
					ContingutTipus.DOCUMENT,
					filtres,
					pagina,
					itemsPerPagina);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error en la consulta d'expedients: " + ex.getMessage(),
					ex);
		} finally {
			try {
				LuceneHelper.closeLuceneWriter();
			} catch (IOException ex) {
				throw new ArxiuException(
						"No s'ha pogut tancar l'índex de lucene writer" + ex.getMessage(),
						ex);
			}
		}
	}

	@Override
	public List<ContingutArxiu> documentVersions(
			String identificador) throws ArxiuException {
		throw new ArxiuException(
				"Aquesta implementació de l'API d'arxiu no suporta el versionat de documents");
	}

	@Override
	public ContingutArxiu documentCopiar(
			String identificador,
			String identificadorDesti) throws ArxiuException {
		try {
			LuceneHelper luceneHelper = getLuceneHelper();
			Document document = luceneHelper.documentDetalls(identificador);
			comprovarExpedientEstat(
					ExpedientEstat.OBERT,
					luceneHelper.getExpedientPareEstat(
							identificadorDesti));
			String uuid = UUID.randomUUID().toString();
			String identificadorEni = generarIdentificadorEni(
					uuid,
					false);
			String parePath = luceneHelper.getPath(identificadorDesti);
			String path = getFilesystemHelper().documentActualitzar(
					parePath,
					document,
					identificadorEni);
			luceneHelper.documentCrear(
					document,
					uuid,
					identificadorDesti,
					identificadorEni,
					path);
			return crearContingutArxiu(
					uuid, 
					document.getNom(),
					ContingutTipus.DOCUMENT,
					null);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error al copiar el document: " + ex.getMessage(),
					ex);
		} finally {
			try {
				LuceneHelper.closeLuceneWriter();
			} catch (IOException ex) {
				throw new ArxiuException(
						"No s'ha pogut tancar l'índex de lucene writer" + ex.getMessage(),
						ex);
			}
		}
	}

	@Override
	public ContingutArxiu documentMoure(
			String identificador,
			String identificadorDesti) throws ArxiuException {
		return documentMoure(
				identificador,
				identificadorDesti);
	}

	@Override
	public ContingutArxiu documentMoure(
			String identificador,
			String identificadorDesti,
			String identificadorExpedientDesti) throws ArxiuException {
		try {
			LuceneHelper luceneHelper = getLuceneHelper();
			String origenPath = luceneHelper.getPath(identificador);
			String destiPath = luceneHelper.getPath(identificadorDesti);
			luceneHelper.contingutMoure(
					ContingutTipus.DOCUMENT,
					identificador,
					identificadorDesti,
					destiPath);
			getFilesystemHelper().directoriMoure(
					origenPath,
					destiPath);
			return null;
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error al moure el document: " + ex.getMessage(),
					ex);
		} finally {
			try {
				LuceneHelper.closeLuceneWriter();
			} catch (IOException ex) {
				throw new ArxiuException(
						"No s'ha pogut tancar l'índex de lucene writer" + ex.getMessage(),
						ex);
			}
		}
	}

	@Override
	public String documentExportarEni(
			String identificador) throws ArxiuException {
		throw new ArxiuException(
				"El mètode documentExportarEni no està disponible");
	}

	@Override
	public DocumentContingut documentImprimible(String identificador) throws ArxiuException {
		identificador = identificador.replace("uuid:",""); //notib adds prefix "uuid"
		try {
			LuceneHelper luceneHelper = getLuceneHelper();
			Document document = luceneHelper.documentDetalls(identificador);
			String path = luceneHelper.getPath(identificador);
			
			boolean firmaAttached = false;
			String tipusMime = null;
			List<Firma> firmes = document.getFirmes();
			if (firmes != null && !firmes.isEmpty()) {
				Firma firma = firmes.get(0);
				if (firma.getTipus() != FirmaTipus.CADES_DET) {
					firmaAttached = true;
					tipusMime = firma.getTipusMime();
				}
			}
			if (firmaAttached) {
				document.setContingut(new DocumentContingut());
				document.getContingut().setContingut(
						getFilesystemHelper().documentFirma(path, 0));
				document.getContingut().setTipusMime(tipusMime);
			} else {
				document.getContingut().setContingut(
						getFilesystemHelper().documentContingut(path));
			}

			document.getContingut().setTamany(
					document.getContingut().getContingut().length);

			return document.getContingut();
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error al obtenir el versio imprimbile: " + ex.getMessage(),
					ex);
		} finally {
			try {
				LuceneHelper.closeLuceneWriter();
			} catch (IOException ex) {
				throw new ArxiuException(
						"No s'ha pogut tancar l'índex de lucene writer" + ex.getMessage(),
						ex);
			}
		}
	}
	
	

	@Override
	public ContingutArxiu carpetaCrear(
			Carpeta carpeta,
			String identificadorPare) throws ArxiuException {
		try {
			LuceneHelper luceneHelper = getLuceneHelper();
			comprovarExpedientEstat(
					ExpedientEstat.OBERT,
					luceneHelper.getExpedientPareEstat(
							identificadorPare));
			String uuid = UUID.randomUUID().toString();
			String parePath = luceneHelper.getPath(identificadorPare);
			String path = getFilesystemHelper().carpetaCrear(
					parePath,
					uuid);
			luceneHelper.carpetaCrear(
					carpeta,
					uuid,
					identificadorPare,
					path);
			return crearContingutArxiu(
					uuid, 
					carpeta.getNom(),
					ContingutTipus.CARPETA,
					null);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error creant la carpeta: " + ex.getMessage(),
					ex);
		} finally {
			try {
				LuceneHelper.closeLuceneWriter();
			} catch (IOException ex) {
				throw new ArxiuException(
						"No s'ha pogut tancar l'índex de lucene writer" + ex.getMessage(),
						ex);
			}
		}
	}

	@Override
	public ContingutArxiu carpetaModificar(
			Carpeta carpeta) throws ArxiuException {
		try {
			LuceneHelper luceneHelper = getLuceneHelper();
			comprovarExpedientEstat(
					ExpedientEstat.OBERT,
					luceneHelper.getExpedientPareEstat(
							carpeta.getIdentificador()));
			luceneHelper.carpetaModificar(
					carpeta);
			return crearContingutArxiu(
					carpeta.getIdentificador(), 
					carpeta.getNom(),
					ContingutTipus.CARPETA,
					null);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error modificant la carpeta: " + ex.getMessage(),
					ex);
		} finally {
			try {
				LuceneHelper.closeLuceneWriter();
			} catch (IOException ex) {
				throw new ArxiuException(
						"No s'ha pogut tancar l'índex de lucene writer" + ex.getMessage(),
						ex);
			}
		}
	}

	@Override
	public void carpetaEsborrar(
			String identificador) throws ArxiuException {
		try {
			LuceneHelper luceneHelper = getLuceneHelper();
			comprovarExpedientEstat(
					ExpedientEstat.OBERT,
					luceneHelper.getExpedientPareEstat(
							identificador));
			String path = luceneHelper.getPath(identificador);
			getFilesystemHelper().directoriEsborrar(path);
			luceneHelper.contingutEsborrar(
					identificador);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error esborrant la carpeta: " + ex.getMessage(),
					ex);
		} finally {
			try {
				LuceneHelper.closeLuceneWriter();
			} catch (IOException ex) {
				throw new ArxiuException(
						"No s'ha pogut tancar l'índex de lucene writer" + ex.getMessage(),
						ex);
			}
		}
	}

	@Override
	public Carpeta carpetaDetalls(
			String identificador) throws ArxiuException {
		try {
			LuceneHelper luceneHelper = getLuceneHelper();
			return luceneHelper.carpetaDetalls(identificador);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error al obtenir els detalls de l'expedient: " + ex.getMessage(),
					ex);
		} finally {
			try {
				LuceneHelper.closeLuceneWriter();
			} catch (IOException ex) {
				throw new ArxiuException(
						"No s'ha pogut tancar l'índex de lucene writer" + ex.getMessage(),
						ex);
			}
		}
	}

	@Override
	public ContingutArxiu carpetaCopiar(
			String identificador,
			String identificadorDesti) throws ArxiuException {
		try {
			LuceneHelper luceneHelper = getLuceneHelper();
			Carpeta carpeta = luceneHelper.carpetaDetalls(identificador);
			comprovarExpedientEstat(
					ExpedientEstat.OBERT,
					luceneHelper.getExpedientPareEstat(
							identificadorDesti));
			String uuid = UUID.randomUUID().toString();
			String parePath = luceneHelper.getPath(identificadorDesti);
			String path = getFilesystemHelper().carpetaCrear(
					parePath,
					uuid);
			luceneHelper.carpetaCrear(
					carpeta,
					uuid,
					identificadorDesti,
					path);
			return crearContingutArxiu(
					uuid, 
					carpeta.getNom(),
					ContingutTipus.CARPETA,
					null);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error al copiar el document: " + ex.getMessage(),
					ex);
		} finally {
			try {
				LuceneHelper.closeLuceneWriter();
			} catch (IOException ex) {
				throw new ArxiuException(
						"No s'ha pogut tancar l'índex de lucene writer" + ex.getMessage(),
						ex);
			}
		}
	}

	@Override
	public void carpetaMoure(
			String identificador,
			String identificadorDesti) throws ArxiuException {
		try {
			LuceneHelper luceneHelper = getLuceneHelper();
			String origenPath = luceneHelper.getPath(identificador);
			String destiPath = luceneHelper.getPath(identificadorDesti);
			luceneHelper.contingutMoure(
					ContingutTipus.CARPETA,
					identificador,
					identificadorDesti,
					destiPath);
			getFilesystemHelper().directoriMoure(
					origenPath,
					destiPath);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error al moure el document: " + ex.getMessage(),
					ex);
		} finally {
			try {
				LuceneHelper.closeLuceneWriter();
			} catch (IOException ex) {
				throw new ArxiuException(
						ex);
			}
		}
	}

	@Override
	public List<DocumentTipusAddicional> documentTipusAddicionals() {
		return null;
	}

	@Override
	public boolean suportaVersionatExpedient() {
		return false;
	}

	@Override
	public boolean suportaVersionatDocument() {
		return false;
	}

	@Override
	public boolean suportaVersionatCarpeta() {
		return false;
	}

	@Override
	public boolean suportaMetadadesNti() {
		return true;
	}

	@Override
	public boolean generaIdentificadorNti() {
		return true;
	}



	private LuceneHelper getLuceneHelper() {
		return new LuceneHelper(getPropertyBasePath());
	}
	private FileSystemHelper getFilesystemHelper() {
		if (filesystemHelper == null) {
			filesystemHelper = new FileSystemHelper(getPropertyBasePath());
		}
		return filesystemHelper;
	}

	private void comprovarExpedientEstat(
			ExpedientEstat estatEsperat,
			ExpedientEstat estatPerComprovar) {
		if (!estatPerComprovar.equals(estatEsperat)) {
			throw new ArxiuException(
					"L'expedient no està en estat " + estatEsperat + " (estat=" + estatPerComprovar + ")");
		}
	}

	private void comprovarDocumentEstat(
			DocumentEstat estatEsperat,
			DocumentEstat estatPerComprovar) {
		if (!estatPerComprovar.equals(estatEsperat)) {
			throw new ArxiuException(
					"El document no està en estat " + estatEsperat + " (estat=" + estatPerComprovar + ")");
		}
	}

	private void comprovarDocumentDefinitiuAmbFirmes(
			Document document, LuceneHelper luceneHelper) throws IOException, ParseException {
		boolean esDefinitiu = DocumentEstat.DEFINITIU.equals(document.getEstat());
		boolean conteFirmes = document.getFirmes() != null;
		if (esDefinitiu && !conteFirmes && !luceneHelper.isDocumentConteFirmes(document.getIdentificador())) {
			throw new ArxiuException(
					"No es pot marcar com a definitiu un document sense firmes");
		}
	}

	private void comprovarExpedientNoConteDocumentsDefinitius(
			String uuid, LuceneHelper luceneHelper) throws IOException {
		if (luceneHelper.isExpedientConteDocumentsDefinitius(uuid)) {
			throw new ArxiuException(
					"L'expedient no es pot esborrar si conté documents definitius");
		}
	}

	private ContingutArxiu crearContingutArxiu(
			String identificador, 
			String nom,
			ContingutTipus tipus,
			String versio) {
		ContingutArxiu informacioItem = new ContingutArxiu(tipus);
		informacioItem.setIdentificador(identificador);
		informacioItem.setNom(nom);
		return informacioItem;
	}

	private String generarIdentificadorEni(
			String uuid,
			boolean esExpedient) throws DecoderException {
		String uuidHex = uuid.replaceAll("-", "");
		byte[] rnd = new byte[5];
		new Random().nextBytes(rnd);
		uuidHex += new String(Hex.encodeHex(rnd));
		byte[] bytes = Hex.decodeHex(uuidHex.toCharArray());
		String uuidBase64 = "FS" + new String(Base64.encodeBase64(bytes));
		int anyActual = Calendar.getInstance().get(Calendar.YEAR);
		String exp = (esExpedient) ? "EXP_" : "";
		return "ES_" + getPropertyOrganCodiDir3() + "_" + anyActual + "_" + exp + uuidBase64.replace("/", "-");
	}

	private String getPropertyBasePath() {
		return getProperty(ARXIUFILESYSTEM_BASE_PROPERTY + "base.path");
	}

	private String getPropertyOrganCodiDir3() {
		return getProperty(ARXIUFILESYSTEM_BASE_PROPERTY + "organ.codi.dir3");
	}

	@Override
	public String getCsv(String identificadorDoc) throws ArxiuException {
		// TODO No se SI ESTA BE
		return identificadorDoc;
	}

	@Override
	protected String getPropertyBase() {
		return ARXIUFILESYSTEM_BASE_PROPERTY;
	}

}
