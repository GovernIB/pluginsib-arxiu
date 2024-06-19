/**
 * 
 */
package es.caib.pluginsib.arxiu.caib;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.DocClassification;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.DocumentId;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.DocumentNode;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.FileNode;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.Metadata;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.ResParamSearchDocument;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.RespuestaFileSearch;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.TargetNode;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.VersionNode;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamCreateChildFile;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamCreateDocument;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamCreateDraftDocument;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamCreateFile;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamCreateFolder;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamDispatchDocument;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamGetDocument;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamNodeID_TargetParent;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamNodeId;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamSearch;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamSetDocument;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamSetFile;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamSetFolder;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.CloseFileResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.CopyDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.CopyFolderResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.CreateChildFileResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.CreateDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.CreateDraftDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.CreateFileResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.CreateFolderResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.DispatchDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.ExportFileResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.GenerateDocCSVResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.GetDocVersionListResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.GetDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.GetENIDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.GetFileResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.GetFileVersionListResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.GetFolderResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.MoveDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.MoveFolderResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.RemoveDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.RemoveFileResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.RemoveFolderResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.ReopenFileResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.SearchDocsResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.SearchFilesResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.SetDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.SetFileResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.SetFinalDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.SetFolderResult;
import es.caib.arxiudigital.apirest.CSGD.peticiones.CloseFile;
import es.caib.arxiudigital.apirest.CSGD.peticiones.CopyDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.CopyFolder;
import es.caib.arxiudigital.apirest.CSGD.peticiones.CreateChildFile;
import es.caib.arxiudigital.apirest.CSGD.peticiones.CreateDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.CreateDraftDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.CreateFile;
import es.caib.arxiudigital.apirest.CSGD.peticiones.CreateFolder;
import es.caib.arxiudigital.apirest.CSGD.peticiones.DispatchDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.ExportFile;
import es.caib.arxiudigital.apirest.CSGD.peticiones.GenerateDocCSV;
import es.caib.arxiudigital.apirest.CSGD.peticiones.GetDocVersionList;
import es.caib.arxiudigital.apirest.CSGD.peticiones.GetDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.GetENIDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.GetFile;
import es.caib.arxiudigital.apirest.CSGD.peticiones.GetFileVersionList;
import es.caib.arxiudigital.apirest.CSGD.peticiones.GetFolder;
import es.caib.arxiudigital.apirest.CSGD.peticiones.MoveDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.MoveFolder;
import es.caib.arxiudigital.apirest.CSGD.peticiones.RemoveDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.RemoveFile;
import es.caib.arxiudigital.apirest.CSGD.peticiones.RemoveFolder;
import es.caib.arxiudigital.apirest.CSGD.peticiones.ReopenFile;
import es.caib.arxiudigital.apirest.CSGD.peticiones.SearchDocs;
import es.caib.arxiudigital.apirest.CSGD.peticiones.SearchFiles;
import es.caib.arxiudigital.apirest.CSGD.peticiones.SetDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.SetFile;
import es.caib.arxiudigital.apirest.CSGD.peticiones.SetFinalDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.SetFolder;
import es.caib.arxiudigital.apirest.constantes.MetadatosDocumento;
import es.caib.arxiudigital.apirest.constantes.Servicios;
import es.caib.pluginsib.arxiu.api.AbstractArxiuPlugin;
import es.caib.pluginsib.arxiu.api.ArxiuException;
import es.caib.pluginsib.arxiu.api.ArxiuValidacioException;
import es.caib.pluginsib.arxiu.api.Carpeta;
import es.caib.pluginsib.arxiu.api.ConsultaFiltre;
import es.caib.pluginsib.arxiu.api.ConsultaOperacio;
import es.caib.pluginsib.arxiu.api.ConsultaResultat;
import es.caib.pluginsib.arxiu.api.ContingutArxiu;
import es.caib.pluginsib.arxiu.api.ContingutTipus;
import es.caib.pluginsib.arxiu.api.Document;
import es.caib.pluginsib.arxiu.api.DocumentContingut;
import es.caib.pluginsib.arxiu.api.DocumentEstat;
import es.caib.pluginsib.arxiu.api.DocumentFormat;
import es.caib.pluginsib.arxiu.api.DocumentMetadades;
import es.caib.pluginsib.arxiu.api.DocumentRepositori;
import es.caib.pluginsib.arxiu.api.DocumentTipusAddicional;
import es.caib.pluginsib.arxiu.api.Expedient;
import es.caib.pluginsib.arxiu.api.ExpedientMetadades;
import es.caib.pluginsib.arxiu.api.Firma;
import es.caib.pluginsib.arxiu.api.FirmaTipus;
import es.caib.pluginsib.arxiu.api.IArxiuPlugin;
import es.caib.pluginsib.arxiu.caib.ArxiuCaibClient.GeneradorParam;

/**
 * Implementació de l'API genèrica de l'arxiu per a accedir
 * a l'arxiu de la CAIB mitjançant l'API REST.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 * @author anadal(u80067)
 */
public class ArxiuPluginCaib extends AbstractArxiuPlugin implements IArxiuPlugin {

	private static final String ARXIUCAIB_BASE_PROPERTY = ARXIU_BASE_PROPERTY + "caib.";
	private static final int NUM_PAGINES_RESULTAT_CERCA = 100;
	private static final String VERSIO_INICIAL_CONTINGUT = "1.0";
	private static final String JERSEY_TIMEOUT_CONNECT = "10000";
	private static final String JERSEY_TIMEOUT_READ = "60000";

	private static final String QUERY_TYPE_ENI_EXPEDIENTE = "\"eni:expediente\"";
	private static final String QUERY_TYPE_ENI_DOCUMENTO = "\"eni:documento\"";
	private static final String QUERY_TYPE_GDIB_DOCUMENTO_MIGRADO = "\"gdib:documentoMigrado\"";

	private ArxiuCaibClient arxiuClient;
	private Client versioImprimibleClient;

	/** Propietat amb el número d'elements màxim retornats per l'Arxiu en les consultes pàginades */
	private int itemsPerPaginaArxiu = 50;

	public ArxiuPluginCaib() {
		super();
	}

	public ArxiuPluginCaib(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}

	public ArxiuPluginCaib(String propertyKeyBase) {
		super(propertyKeyBase);
	}

	@Override
	public ContingutArxiu expedientCrear(
			final Expedient expedient) throws ArxiuException {
		this.documentTipusAddicionals();
		String metode = Servicios.CREATE_FILE;
		try {
			CreateFileResult resposta = getArxiuClient().generarEnviarPeticio(
					metode,
					CreateFile.class,
					new GeneradorParam<ParamCreateFile>() {
						@Override
						public ParamCreateFile generar() {
							ParamCreateFile param = new ParamCreateFile();
							param.setFile(
									ArxiuConversioHelper.expedientToFileNode(
											expedient,
											null,
											null,
											null,
											true));
							param.setRetrieveNode(Boolean.TRUE.toString());
							return param;
						}
					},
					ParamCreateFile.class,
					CreateFileResult.class);
			Expedient expedientCreat = ArxiuConversioHelper.fileNodeToExpedient(
					resposta.getCreateFileResult().getResParam(),
					VERSIO_INICIAL_CONTINGUT);
			return crearContingutArxiu(
					expedientCreat.getIdentificador(), 
					expedientCreat.getNom(),
					ContingutTipus.EXPEDIENT,
					expedientCreat.getVersio(),
					expedientCreat.getExpedientMetadades(),
					null);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode + ": " + ex.getMessage(),
					ex);
		}
	}

	@Override
	public ContingutArxiu expedientModificar(
			final Expedient expedient) throws ArxiuException {
		String metode = null;
		try {
			/*metode = Servicios.GET_FILE;
			final GetFileResult resposta = getArxiuClient().generarEnviarPeticio(
					metode,
					GetFile.class,
					new GeneradorParam<ParamNodeId>() {
						@Override
						public ParamNodeId generar() {
							ParamNodeId param = new ParamNodeId();
							param.setNodeId(expedient.getIdentificador());
							return param;
						}
					},
					ParamNodeId.class,
					GetFileResult.class);*/
			metode = Servicios.SET_FILE;
			getArxiuClient().generarEnviarPeticio(
					metode,
					SetFile.class,
					new GeneradorParam<ParamSetFile>() {
						@Override
						public ParamSetFile generar() {
							ParamSetFile param = new ParamSetFile();
							param.setFile(
									ArxiuConversioHelper.expedientToFileNode(
											expedient,
											null, //resposta.getGetFileResult().getResParam().getMetadataCollection(),
											null, //resposta.getGetFileResult().getResParam().getAspects(),
											null,
											false));
							return param;
						}
					},
					ParamSetFile.class,
					SetFileResult.class);
			String versio = expedientDarreraVersio(
					expedient.getIdentificador());
			return crearContingutArxiu(
					expedient.getIdentificador(), 
					expedient.getNom(),
					ContingutTipus.EXPEDIENT,
					versio,
					null,
					null);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public void expedientEsborrar(
			final String identificador) throws ArxiuException {
		String metode = Servicios.REMOVE_FILE;
		try {
			getArxiuClient().generarEnviarPeticio(
					metode,
					RemoveFile.class,
					new GeneradorParam<ParamNodeId>() {
						@Override
						public ParamNodeId generar() {
							ParamNodeId param = new ParamNodeId();
							param.setNodeId(identificador);
							return param;
						}
					},
					ParamNodeId.class,
					RemoveFileResult.class);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public Expedient expedientDetalls(
			final String identificador,
			final String versio) throws ArxiuException {
		String metode = Servicios.GET_FILE;
		try {
			String versioResposta = null;
			if (versio == null) {
				versioResposta = expedientDarreraVersio(identificador);
			} else {
				versioResposta = versio;
			}
			GetFileResult resposta = getArxiuClient().generarEnviarPeticio(
					metode,
					GetFile.class,
					new GeneradorParam<ParamNodeId>() {
						@Override
						public ParamNodeId generar() {
							ParamNodeId param = new ParamNodeId();
							String identificadorAmbVersio;
							if (versio != null) {
								identificadorAmbVersio = versio + "@" + identificador;
							} else {
								identificadorAmbVersio = identificador;
							}
							param.setNodeId(identificadorAmbVersio);
							return param;
						}
					},
					ParamNodeId.class,
					GetFileResult.class);
			return ArxiuConversioHelper.fileNodeToExpedient(
					resposta.getGetFileResult().getResParam(),
					versioResposta);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}
	
	@Override
	public ConsultaResultat expedientConsulta(
			final List<ConsultaFiltre> filtres,
			final Integer pagina,
			final Integer itemsPerPagina) throws ArxiuException {
		String metode = Servicios.SEARCH_FILE;
		try {
			int numRegistres = 0; 
			int numPagines = 0;
			int numRetornat = 0; 
			int paginaActual = 0;
			List<ContingutArxiu> resultats = new ArrayList<ContingutArxiu>();
			
			SearchFilesResult resposta;
			int nPagina;
			if (pagina != null && itemsPerPagina != null) {
				
				// Consulta paginada
				
				int paginaInici = pagina*itemsPerPagina / itemsPerPaginaArxiu;
				int indexInici = pagina*itemsPerPagina % itemsPerPaginaArxiu;
				int paginaFi = ((pagina+1)*itemsPerPagina-1) / itemsPerPaginaArxiu;
				int indexFi = ((pagina+1)*itemsPerPagina-1) % itemsPerPaginaArxiu + 1;
				
				nPagina = paginaInici;
				do {
					resposta = this.consultaPaginaExpedientsArxiu(
							metode,
							SearchFiles.class,
							QUERY_TYPE_ENI_EXPEDIENTE,
							filtres,
							nPagina);

					RespuestaFileSearch respostaFileSearch = resposta.getSearchFilesResult().getResParam();
					if ( respostaFileSearch != null && respostaFileSearch.getFiles() != null) {
						
						List<FileNode> files = respostaFileSearch.getFiles();
						
						int iInici = nPagina == paginaInici ? indexInici : 0;
						int iFi = Math.min(nPagina == paginaFi ? indexFi : itemsPerPaginaArxiu, files.size()); 
						resultats.addAll(ArxiuConversioHelper.fileNodesToFileContingutArxiu(files.subList(iInici, iFi)));
						numRegistres = respostaFileSearch.getTotalNumberOfResults();
					}
					nPagina++;
				} while (nPagina <= paginaFi && (nPagina - paginaInici) <= NUM_PAGINES_RESULTAT_CERCA);

				numRetornat = resultats.size();
				numPagines = numRegistres / itemsPerPagina + (numRegistres % itemsPerPagina == 0 ? 0 : 1);
				paginaActual = pagina;

			} else {
				// Consulta sense paginar
				boolean retornaResultats = false;
				nPagina = 0;
				do {
					resposta = this.consultaPaginaExpedientsArxiu(
							metode,
							SearchFiles.class,
							QUERY_TYPE_ENI_EXPEDIENTE,
							filtres,
							nPagina++);

					RespuestaFileSearch respostaFileSearch = resposta.getSearchFilesResult().getResParam();
					if ( respostaFileSearch.getFiles() != null) {
						List<FileNode> files = respostaFileSearch.getFiles();
						resultats.addAll(ArxiuConversioHelper.fileNodesToFileContingutArxiu(files));
						retornaResultats = files.size() > 0;
					} else {
						retornaResultats = false;
					}
					
				} 
				while ( retornaResultats && nPagina <= NUM_PAGINES_RESULTAT_CERCA);

				numRegistres = resultats.size();
				numRetornat = numRegistres;
				numPagines = (numRegistres > 0 ? 1 : 0);
				paginaActual = 0;
			}

			return new ConsultaResultat(
					numRegistres,
					numPagines,
					numRetornat,
					paginaActual,
					resultats);
			
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	private SearchFilesResult consultaPaginaExpedientsArxiu(
			String metode, 
			Class<SearchFiles> peticioType, 
			final String queryTypeEni,
			final List<ConsultaFiltre> filtres, 
			final Integer pagina) throws Exception {
		
		SearchFilesResult resposta = getArxiuClient().generarEnviarPeticio(
				metode,
				peticioType,
				new GeneradorParam<ParamSearch>() {
					@Override
					public ParamSearch generar() {
						ParamSearch param = new ParamSearch();
						String query = generarConsulta(
								queryTypeEni,
								filtres);
						param.setQuery(query);
						param.setPageNumber(pagina);
						return param;
					}
				},
				ParamSearch.class,
				SearchFilesResult.class);
		
		if (	resposta.getSearchFilesResult() != null 
				&& resposta.getSearchFilesResult().getResParam() != null
				&& resposta.getSearchFilesResult().getResParam().getFiles() != null ) {
			
			int filesLength = resposta.getSearchFilesResult().getResParam().getFiles().size();
			int totalNumberOfResults = resposta.getSearchFilesResult().getResParam().getTotalNumberOfResults();
			if (pagina == 0 
					&& totalNumberOfResults > this.itemsPerPaginaArxiu 
					&& this.itemsPerPaginaArxiu != filesLength) {
				logger.warn("S'ha detectat que la grandària de pàgina estimada " + this.itemsPerPaginaArxiu + 
						" és diferent a la del Arxiu. S'ajusta la nova grandària a " + filesLength);
				this.itemsPerPaginaArxiu = filesLength;
			} else if (totalNumberOfResults < pagina * this.itemsPerPaginaArxiu + filesLength) {
				logger.warn("S'ha detectat que la grandària de pàgina estimada " + this.itemsPerPaginaArxiu + 
						" no pot ser correcta per la la pàgina " + pagina + " amb un total de " + totalNumberOfResults + 
						" i " + filesLength + " resultats retornats.");
			}
		}
		
		return resposta;
	}

	@Override
	public ContingutArxiu expedientCrearSubExpedient(
			final Expedient expedient, 
			final String identificadorPare) throws ArxiuException {
		String metode = Servicios.CREATE_CHILD_FILE;
		try {
			CreateChildFileResult resposta = getArxiuClient().generarEnviarPeticio(
					metode,
					CreateChildFile.class,
					new GeneradorParam<ParamCreateChildFile>() {
						@Override
						public ParamCreateChildFile generar() {
							ParamCreateChildFile param = new ParamCreateChildFile();
							param.setFile(
									ArxiuConversioHelper.expedientToFileNode(
											expedient,
											null,
											null,
											null,
											true));
							param.setRetrieveNode(Boolean.TRUE.toString());
							param.setParent(identificadorPare);
							return param;
						}
					},
					ParamCreateChildFile.class,
					CreateChildFileResult.class);
			Expedient expedientCreat = ArxiuConversioHelper.fileNodeToExpedient(
					resposta.getCreateChildFileResult().getResParam(),
					VERSIO_INICIAL_CONTINGUT);
			return crearContingutArxiu(
					expedientCreat.getIdentificador(), 
					expedientCreat.getNom(),
					ContingutTipus.EXPEDIENT,
					expedientCreat.getVersio(),
					expedientCreat.getExpedientMetadades(),
					null);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode + ": " + ex.getMessage(),
					ex);
		}
	}

	@Override
	public List<ContingutArxiu> expedientVersions(
			final String identificador) throws ArxiuException {
		String metode = Servicios.GET_VERSION_FILE;
		try {
			return expedientVersionsComu(identificador);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public String expedientTancar(
			final String identificador) throws ArxiuException {
		String metode = Servicios.CLOSE_FILE;
		try {
			getArxiuClient().generarEnviarPeticio(
					metode,
					CloseFile.class,
					new GeneradorParam<ParamNodeId>() {
						@Override
						public ParamNodeId generar() {
							ParamNodeId param = new ParamNodeId();
							param.setNodeId(identificador);
							return param;
						}
					},
					ParamNodeId.class,
					CloseFileResult.class);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
		return null;
	}

	@Override
	public String expedientReobrir(
			final String identificador) throws ArxiuException {
		String metode = Servicios.REOPEN_FILE;
		try {
			ReopenFileResult resposta = getArxiuClient().generarEnviarPeticio(
					metode,
					ReopenFile.class,
					new GeneradorParam<ParamNodeId>() {
						@Override
						public ParamNodeId generar() {
							ParamNodeId param = new ParamNodeId();
							param.setNodeId(identificador);
							return param;
						}
					},
					ParamNodeId.class,
					ReopenFileResult.class);
			return resposta != null && resposta.getReopenFileResult() != null ? 
					resposta.getReopenFileResult().getResParam()
					: null;
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public String expedientExportarEni(
			final String identificador) throws ArxiuException {
		String metode = Servicios.EXPORT_FILE;
		try {
			ExportFileResult resposta = getArxiuClient().generarEnviarPeticio(
					metode,
					ExportFile.class,
					new GeneradorParam<ParamNodeId>() {
						@Override
						public ParamNodeId generar() {
							ParamNodeId param = new ParamNodeId();
							param.setNodeId(identificador);
							return param;
						}
					},
					ParamNodeId.class,
					ExportFileResult.class);
			String exportBase64 = resposta.getExportFileResult().getResParam();
			if (exportBase64 != null) {
				return new String(
						Base64.decodeBase64(exportBase64));
			} else {
				return null;
			}
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
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
			final Document document,
			final String identificadorPare) throws ArxiuException {
		String metode = Servicios.CREATE_DOC;
		try {
			comprovarAbsenciaMetadadaCsv(document.getMetadades());
			comprovarFirma(document);
			final String serieDocumental;
			if (document.getMetadades().getSerieDocumental() != null && !document.getMetadades().getSerieDocumental().isEmpty())
				serieDocumental = document.getMetadades().getSerieDocumental();
			else 
				serieDocumental = findSerieDocumentalExpedientPare(
					document,
					identificadorPare);
			Document creat = null;
			if (DocumentEstat.ESBORRANY.equals(document.getEstat())) {
				creat = this.documentCrearDraft(
									document, 
									identificadorPare, 
									serieDocumental);
			} else if (DocumentEstat.DEFINITIU.equals(document.getEstat())) {
				metode = Servicios.GENERATE_CSV;
				GenerateDocCSVResult respostaCsv = getArxiuClient().generarEnviarPeticio(
						metode,
						GenerateDocCSV.class,
						null,
						Object.class,
						GenerateDocCSVResult.class);
				final String csv = respostaCsv.getGenerateDocCSVResult().getResParam();
				comprovarCsv(csv);
				metode = Servicios.CREATE_DOC;
				CreateDocumentResult resposta = getArxiuClient().generarEnviarPeticio(
						metode,
						CreateDocument.class,
						new GeneradorParam<ParamCreateDocument>() {
							@Override
							public ParamCreateDocument generar() {
								ParamCreateDocument param = new ParamCreateDocument();
								param.setParent(identificadorPare);
								param.setDocument(
										ArxiuConversioHelper.documentToDocumentNode(
												document,
												serieDocumental,
												null,
												null,
												null,
												csv,
												getPropertyDefinicioCsv(),
												true,
												true));
								param.setRetrieveNode(Boolean.TRUE.toString());
								return param;
							}
						},
						ParamCreateDocument.class,
						CreateDocumentResult.class);
				creat = ArxiuConversioHelper.documentNodeToDocument(
						resposta.getCreateDocumentResult().getResParam(),
						VERSIO_INICIAL_CONTINGUT);
			} else {
				throw new ArxiuValidacioException(
						"No s'ha emplenat l'estat del document o el document no conte un estat reconegut (ESBORRANY o DEFINITIU)");
			}
			return crearContingutArxiu(
					creat.getIdentificador(), 
					creat.getNom(),
					ContingutTipus.DOCUMENT,
					creat.getVersio(),
					null,
					creat.getDocumentMetadades());
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	private Document documentCrearDraft(
			final Document document, 
			final String identificadorPare, 
			final String serieDocumental) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, UniformInterfaceException, ClientHandlerException, IOException, NoSuchMethodException, SecurityException, Exception {

		String metode = Servicios.CREATE_DRAFT;
		CreateDraftDocumentResult resposta = getArxiuClient().generarEnviarPeticio(
				metode,
				CreateDraftDocument.class,
				new GeneradorParam<ParamCreateDraftDocument>() {
					@Override
					public ParamCreateDraftDocument generar() {
						ParamCreateDraftDocument param = new ParamCreateDraftDocument();
						param.setParent(identificadorPare);
						param.setDocument(
								ArxiuConversioHelper.documentToDocumentNode(
										document,
										serieDocumental,
										null,
										null,
										null,
										null,
										null,
										true,
										false));
						param.setRetrieveNode(Boolean.TRUE.toString());
						return param;
					}
				},
				ParamCreateDraftDocument.class,
				CreateDraftDocumentResult.class);
		Document creat = ArxiuConversioHelper.documentNodeToDocument(
				resposta.getCreateDraftDocumentResult().getResParam(),
				VERSIO_INICIAL_CONTINGUT);
		return creat;
	}

	@Override
	public ContingutArxiu documentModificar(
			final Document document) throws ArxiuException {
		String metode = null;
		try {
			comprovarAbsenciaMetadadaCsv(document.getMetadades());
			comprovarFirma(document);
			if (DocumentEstat.DEFINITIU.equals(document.getEstat())) {
				/** #85 Es treu la validació per poder guardar com a definitiu un document TF04 CAdES detached amb firma i contingut
				if (document.getContingut() != null) {
					throw new ArxiuValidacioException(
							"No és possible marcar el document com a definitiu si es vol modificar el seu contingut.");
				}
				 */
				metode = Servicios.GENERATE_CSV;
				GenerateDocCSVResult respostaCsv = getArxiuClient().generarEnviarPeticio(
						metode,
						GenerateDocCSV.class,
						null,
						Object.class,
						GenerateDocCSVResult.class);
				final String csv = respostaCsv.getGenerateDocCSVResult().getResParam();
				comprovarCsv(csv);
				metode = Servicios.SET_FINAL_DOC;
				getArxiuClient().generarEnviarPeticio(
						metode,
						SetFinalDocument.class,
						new GeneradorParam<ParamSetDocument>() {
							@Override
							public ParamSetDocument generar() {
								ParamSetDocument param = new ParamSetDocument();
								param.setDocument(
										ArxiuConversioHelper.documentToDocumentNode(
												document,
												null,
												null, //resposta.getGetDocumentResult().getResParam().getMetadataCollection(),
												null, //resposta.getGetDocumentResult().getResParam().getAspects(),
												null,
												csv,
												getPropertyDefinicioCsv(),
												false,
												true));
								return param;
							}
						},
						ParamSetDocument.class,
						SetFinalDocumentResult.class);
			} else {
				metode = Servicios.SET_DOC;
				getArxiuClient().generarEnviarPeticio(
						metode,
						SetDocument.class,
						new GeneradorParam<ParamSetDocument>() {
							@Override
							public ParamSetDocument generar() {
								ParamSetDocument param = new ParamSetDocument();
								param.setDocument(
										ArxiuConversioHelper.documentToDocumentNode(
												document,
												null,
												null, //resposta.getGetDocumentResult().getResParam().getMetadataCollection(),
												null, //resposta.getGetDocumentResult().getResParam().getAspects(),
												null,
												null,
												null,
												false,
												false));
								return param;
							}
						},
						ParamSetDocument.class,
						SetDocumentResult.class);
			}
			String versio = documentDarreraVersio(
					document.getIdentificador());
			GetDocumentResult resposta = getDocumentResult(
					document.getIdentificador(),
					versio,
					false);
			Document documentDetalls = ArxiuConversioHelper.documentNodeToDocument(
					resposta.getGetDocumentResult().getResParam(),
					versio);
			return crearContingutArxiu(
					document.getIdentificador(), 
					document.getNom(),
					ContingutTipus.DOCUMENT,
					versio,
					null,
					documentDetalls.getDocumentMetadades());
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public void documentEsborrar(
			final String identificador) throws ArxiuException {
		String metode = Servicios.REMOVE_DOC;
		try {
			getArxiuClient().generarEnviarPeticio(
					metode,
					RemoveDocument.class,
					new GeneradorParam<ParamNodeId>() {
						@Override
						public ParamNodeId generar() {
							ParamNodeId param = new ParamNodeId();
							param.setNodeId(identificador);
							return param;
						}
					},
					ParamNodeId.class,
					RemoveDocumentResult.class);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public Document documentDetalls(
			final String identificador,
			final String versio,
			final boolean ambContingut) throws ArxiuException {
		String metode = Servicios.GET_DOC;
		try {
			String versioResposta = null;
			if (versio == null) {
				try {
					versioResposta = documentDarreraVersio(identificador);
				} catch (Exception e) {
					logger.warn("Error al consultar darrera versió de document", e.getMessage());
					versioResposta = "-";
				}
			} else {
				versioResposta = versio;
			}
			GetDocumentResult resposta = getDocumentResult(
					identificador,
					versio,
					ambContingut);
			return ArxiuConversioHelper.documentNodeToDocument(
					resposta.getGetDocumentResult().getResParam(),
					versioResposta);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public ConsultaResultat documentConsulta(
			final List<ConsultaFiltre> filtres,
			final Integer pagina,
			final Integer itemsPerPagina,
			final DocumentRepositori repositori) throws ArxiuException {
		String metode = Servicios.SEARCH_DOC;
		try {
			String queryTypeEni;
			if (repositori == null || DocumentRepositori.ENI_DOCUMENTO.equals(repositori)) {
				queryTypeEni = QUERY_TYPE_ENI_DOCUMENTO;
			} else {
				queryTypeEni = QUERY_TYPE_GDIB_DOCUMENTO_MIGRADO;
			}
			int numRegistres = 0; 
			int numPagines = 0;
			int numRetornat = 0; 
			int paginaActual = 0;
			List<ContingutArxiu> resultats = new ArrayList<ContingutArxiu>();
			
			SearchDocsResult resposta;
			int nPagina;
			if (pagina != null && itemsPerPagina != null) {
				
				// Consulta paginada
				
				int paginaInici = pagina*itemsPerPagina / itemsPerPaginaArxiu;
				int indexInici = pagina*itemsPerPagina % itemsPerPaginaArxiu;
				int paginaFi = ((pagina+1)*itemsPerPagina-1) / itemsPerPaginaArxiu;
				int indexFi = ((pagina+1)*itemsPerPagina-1) % itemsPerPaginaArxiu + 1;
				
				nPagina = paginaInici;
				do {
					resposta = this.consultaPaginaDocumentsArxiu(
							metode,
							SearchDocs.class,
							queryTypeEni,
							filtres,
							nPagina);

					ResParamSearchDocument respostaDocumentSearch = resposta.getSearchDocumentsResult().getResParam();
					if ( respostaDocumentSearch != null && respostaDocumentSearch.getDocuments() != null) {

						List<DocumentNode> documents = respostaDocumentSearch.getDocuments();						
						int iInici = nPagina == paginaInici ? indexInici : 0;
						int iFi = Math.min(nPagina == paginaFi ? indexFi : itemsPerPaginaArxiu, documents.size()); 
						resultats.addAll(ArxiuConversioHelper.fileNodeToDocumentContingut(documents.subList(iInici, iFi)));
						numRegistres = respostaDocumentSearch.getTotalNumberOfResults();
					}
					nPagina++;
				} while (nPagina <= paginaFi && (nPagina - paginaInici) <= NUM_PAGINES_RESULTAT_CERCA);

				numRetornat = resultats.size();
				numPagines = numRegistres / itemsPerPagina + (numRegistres % itemsPerPagina == 0 ? 0 : 1);
				paginaActual = pagina;

			} else {
				// Consulta sense paginar
				boolean retornaResultats = false;
				nPagina = 0;
				do {
					resposta = this.consultaPaginaDocumentsArxiu(
							metode,
							SearchDocs.class,
							queryTypeEni,
							filtres,
							nPagina++);

					ResParamSearchDocument respostaDocumentSearch = resposta.getSearchDocumentsResult().getResParam();
					if ( respostaDocumentSearch.getDocuments() != null) {
						List<DocumentNode> documents = respostaDocumentSearch.getDocuments();
						resultats.addAll(ArxiuConversioHelper.fileNodeToDocumentContingut(documents));
						retornaResultats = documents.size() > 0;
					} else {
						retornaResultats = false;
					}
					
				} 
				while ( retornaResultats && nPagina <= NUM_PAGINES_RESULTAT_CERCA);

				numRegistres = resultats.size();
				numRetornat = numRegistres;
				numPagines = (numRegistres > 0 ? 1 : 0);
				paginaActual = 0;
			}

			return new ConsultaResultat(
					numRegistres,
					numPagines,
					numRetornat,
					paginaActual,
					resultats);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	private SearchDocsResult consultaPaginaDocumentsArxiu(
			String metode, 
			Class<SearchDocs> peticioType, 
			final String queryTypeEni,
			final List<ConsultaFiltre> filtres, 
			final Integer pagina) throws Exception {
		
		SearchDocsResult resposta = getArxiuClient().generarEnviarPeticio(
				metode,
				peticioType,
				new GeneradorParam<ParamSearch>() {
					@Override
					public ParamSearch generar() {
						ParamSearch param = new ParamSearch();
						String query = generarConsulta(
								queryTypeEni,
								filtres);
						param.setQuery(query);
						param.setPageNumber(pagina);
						return param;
					}
				},
				ParamSearch.class,
				SearchDocsResult.class);
		
		if (	resposta.getSearchDocumentsResult() != null 
				&& resposta.getSearchDocumentsResult().getResParam() != null
				&& resposta.getSearchDocumentsResult().getResParam().getDocuments() != null ) {
			
			int documentsLength = resposta.getSearchDocumentsResult().getResParam().getDocuments().size();
			int totalNumberOfResults = resposta.getSearchDocumentsResult().getResParam().getTotalNumberOfResults();
			if (pagina == 0 
					&& totalNumberOfResults > this.itemsPerPaginaArxiu 
					&& this.itemsPerPaginaArxiu != documentsLength) {
				logger.warn("S'ha detectat que la grandària de pàgina estimada " + this.itemsPerPaginaArxiu + 
						" és diferent a la del Arxiu. S'ajusta la nova grandària a " + documentsLength);
				this.itemsPerPaginaArxiu = documentsLength;
			} else if (totalNumberOfResults < pagina * this.itemsPerPaginaArxiu + documentsLength) {
				logger.warn("S'ha detectat que la grandària de pàgina estimada " + this.itemsPerPaginaArxiu + 
						" no pot ser correcta per la la pàgina " + pagina + " amb un total de " + totalNumberOfResults + 
						" i " + documentsLength + " resultats retornats.");
			}
		}

		
		return resposta;
	}
	@Override
	public List<ContingutArxiu> documentVersions(
			String identificador) throws ArxiuException {
		String metode = Servicios.GET_VERSION_DOC;
		try {
			return documentVersionsComu(identificador);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public ContingutArxiu documentCopiar(
			final String identificador,
			final String identificadorDesti) throws ArxiuException {
		String metode = Servicios.COPY_DOC;
		try {
			CopyDocumentResult resposta = getArxiuClient().generarEnviarPeticio(
					metode,
					CopyDocument.class,
					new GeneradorParam<ParamNodeID_TargetParent>() {
						@Override
						public ParamNodeID_TargetParent generar() {
							ParamNodeID_TargetParent param = new ParamNodeID_TargetParent();
							param.setNodeId(identificador);
							param.setTargetParent(identificadorDesti);
							return param;
						}
					},
					ParamNodeID_TargetParent.class,
					CopyDocumentResult.class);
			resposta.getCopyDocumentResult().getResParam();
			return crearContingutArxiu(
					resposta.getCopyDocumentResult().getResParam(),
					null,
					ContingutTipus.DOCUMENT,
					VERSIO_INICIAL_CONTINGUT,
					null,
					null);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public ContingutArxiu documentMoure(
			final String identificador,
			final String identificadorDesti) throws ArxiuException {
		return documentMoure(
				identificador,
				identificadorDesti,
				null);
	}
	
	@Override
	public ContingutArxiu documentMoure(
			final String identificador,
			final String identificadorDesti,
			final String identificadorExpedientDesti) throws ArxiuException {
		String metode = null;
		try {
			GetDocumentResult respostaGet = getDocumentResult(
					identificador,
					null,
					false);
			Document documentOrigen = ArxiuConversioHelper.documentNodeToDocument(
					respostaGet.getGetDocumentResult().getResParam(),
					null);
			final String serieDocumentalOrigen = findSerieDocumentalExpedientPare(
					documentOrigen,
					null);
			final String serieDocumentalDesti = findSerieDocumentalExpedientPare(
					null,
					(identificadorExpedientDesti != null) ? identificadorExpedientDesti : identificadorDesti);
			if (serieDocumentalOrigen.equals(serieDocumentalDesti)) {
				metode = Servicios.MOVE_DOC;
				getArxiuClient().generarEnviarPeticio(
						metode,
						MoveDocument.class,
						new GeneradorParam<ParamNodeID_TargetParent>() {
							@Override
							public ParamNodeID_TargetParent generar() {
								ParamNodeID_TargetParent param = new ParamNodeID_TargetParent();
								param.setNodeId(identificador);
								param.setTargetParent(identificadorDesti);
								return param;
							}
						},
						ParamNodeID_TargetParent.class,
						MoveDocumentResult.class);
				return null;
			} else {
				
				if (DocumentEstat.DEFINITIU.equals(documentOrigen.getEstat())) {
					
					metode = Servicios.DISPATCH_DOC;
					DispatchDocumentResult respostaDispatch = getArxiuClient().generarEnviarPeticio(
							metode,
							DispatchDocument.class,
							new GeneradorParam<ParamDispatchDocument>() {
								@Override
								public ParamDispatchDocument generar() {
									ParamDispatchDocument param = new ParamDispatchDocument();
									param.setSourceNodeId(identificador);
									TargetNode targetNode = new TargetNode();
									targetNode.setId(identificadorDesti);
									targetNode.setTargetType("-");
									DocClassification docClassification = new DocClassification();
									docClassification.setSerie(serieDocumentalDesti);
									//docClassification.setType(type);
									targetNode.setDocClassification(docClassification);
									param.setTargetNode(targetNode);
									return param;
								}
							},
							ParamDispatchDocument.class,
							DispatchDocumentResult.class);
					respostaDispatch.getDispatchDocumentResult().getResParam();
					return crearContingutArxiu(
							respostaDispatch.getDispatchDocumentResult().getResParam(),
							null,
							ContingutTipus.DOCUMENT,
							VERSIO_INICIAL_CONTINGUT,
							null,
							null);
				} else {
					// Torna a consultar el document amb contingut
					respostaGet = getDocumentResult(
							identificador,
							null,
							true);
					documentOrigen = ArxiuConversioHelper.documentNodeToDocument(
							respostaGet.getGetDocumentResult().getResParam(),
							null);
					// Crea un document draft en el destí
					return this.documentCrearDraft(
							documentOrigen, 
							identificadorDesti, 
							serieDocumentalDesti);					
				}
			}
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public String documentExportarEni(
			final String identificador) throws ArxiuException {
		String metode = Servicios.GET_ENIDOC;
		try {
			GetENIDocumentResult resposta = getArxiuClient().generarEnviarPeticio(
					metode,
					GetENIDocument.class,
					new GeneradorParam<ParamNodeId>() {
						@Override
						public ParamNodeId generar() {
							ParamNodeId param = new ParamNodeId();
							param.setNodeId(identificador);
							return param;
						}
					},
					ParamNodeId.class,
					GetENIDocumentResult.class);
			String exportBase64 = resposta.getGetENIDocResult().getResParam();
			if (exportBase64 != null) {
				return new String(
						Base64.decodeBase64(exportBase64));
			} else {
				return null;
			}
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public DocumentContingut documentImprimible(
			final String identificador) throws ArxiuException {
		/*
		 * Les URLs de consulta son les següents:
		 *   https://intranet.caib.es/concsv/rest/printable/uuid/IDENTIFICADOR?metadata1=METADADA_1&metadata2=METADADA_2&watermark=MARCA_AIGUA
		 *   https://intranet.caib.es/concsv/rest/printable/CSV?metadata1=METADADA_1&metadata2=METADADA_2&watermark=MARCA_AIGUA
		 * A on:
		 *   - CSV és el CSV del document a consultar [OBLIGATORI]
		 *   - IDENTIFICADOR és el UUID del document a consultar [OBLIGATORI]
		 *   - METADADA_1 és la primera metadada [OPCIONAL]
		 *   - METADADA_2 és la segona metadada [OPCIONAL]
		 *   - MARCA_AIGUA és el text de la marca d'aigua que apareixerà impresa a cada fulla [OPCIONAL]
		 * Només es obligatori informa la HASH, la resta d'elements son opcionals. Si no s'informen metadades s'imprimeix l'hora i dia de la generació del document imprimible.
		 */
		try {
			InputStream is = generarVersioImprimible(
					identificador,
					null, // metadada 1
					null, // metadada 2
					null); // marca d'aigua
			DocumentContingut contingut = new DocumentContingut();
			contingut.setArxiuNom("versio_imprimible.pdf");
			contingut.setTipusMime("application/pdf");
			contingut.setContingut(IOUtils.toByteArray(is));
			contingut.setTamany(contingut.getContingut().length);
			return contingut;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error generant la versió imprimible del document",
					ex);
		}
		
	}

	@Override
	public ContingutArxiu carpetaCrear(
			final Carpeta carpeta,
			final String identificadorPare) throws ArxiuException {
		String metode = Servicios.CREATE_FOLDER;
		try {
			CreateFolderResult resposta = getArxiuClient().generarEnviarPeticio(
					metode,
					CreateFolder.class,
					new GeneradorParam<ParamCreateFolder>() {
						@Override
						public ParamCreateFolder generar() {
							ParamCreateFolder param = new ParamCreateFolder();
							param.setParent(identificadorPare);
							param.setFolder(
									ArxiuConversioHelper.toFolderNode(
											null,
											carpeta.getNom()));
							param.setRetrieveNode(Boolean.TRUE.toString());
							param.setRetrieveNode(Boolean.TRUE.toString());
							return param;
						}
					},
					ParamCreateFolder.class,
					CreateFolderResult.class);
			Carpeta carpetaCreada = ArxiuConversioHelper.folderNodeToCarpeta(
					resposta.getCreateFolderResult().getResParam());
			return crearContingutArxiu(
					carpetaCreada.getIdentificador(), 
					carpetaCreada.getNom(),
					ContingutTipus.CARPETA,
					null,
					null,
					null);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode + ": " + ex.getMessage(),
					ex);
		}
	}

	@Override
	public ContingutArxiu carpetaModificar(
			final Carpeta carpeta) throws ArxiuException {
		String metode = Servicios.SET_FOLDER;
		try {
			getArxiuClient().generarEnviarPeticio(
					metode,
					SetFolder.class,
					new GeneradorParam<ParamSetFolder>() {
						@Override
						public ParamSetFolder generar() {
							ParamSetFolder param = new ParamSetFolder();
							param.setFolder(ArxiuConversioHelper.toFolderNode(
									carpeta.getIdentificador(),
									carpeta.getNom()));
							return param;
						}
					},
					ParamSetFolder.class,
					SetFolderResult.class);
			return crearContingutArxiu(
					carpeta.getIdentificador(),
					carpeta.getNom(),
					ContingutTipus.CARPETA,
					null,
					null,
					null);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public void carpetaEsborrar(
			final String identificador) throws ArxiuException {
		String metode = Servicios.REMOVE_FOLDER;
		try {
			getArxiuClient().generarEnviarPeticio(
					metode,
					RemoveFolder.class,
					new GeneradorParam<ParamNodeId>() {
						@Override
						public ParamNodeId generar() {
							ParamNodeId param = new ParamNodeId();
							param.setNodeId(identificador);
							return param;
						}
					},
					ParamNodeId.class,
					RemoveFolderResult.class);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public Carpeta carpetaDetalls(
			final String identificador) throws ArxiuException {
		String metode = Servicios.GET_FOLDER;
		try {
			GetFolderResult resposta = getArxiuClient().generarEnviarPeticio(
					metode,
					GetFolder.class,
					new GeneradorParam<ParamNodeId>() {
						@Override
						public ParamNodeId generar() {
							ParamNodeId param = new ParamNodeId();
							param.setNodeId(identificador);
							return param;
						}
					},
					ParamNodeId.class,
					GetFolderResult.class);
			return ArxiuConversioHelper.folderNodeToCarpeta(
					resposta.getGetFolderResult().getResParam());
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public ContingutArxiu carpetaCopiar(
			final String identificador,
			final String identificadorDesti) throws ArxiuException {
		//String metode = Servicios.COPY_FOLDER;
		String metode = "/services/copyFolder";
		try {
			CopyFolderResult resposta = getArxiuClient().generarEnviarPeticio(
					metode,
					CopyFolder.class,
					new GeneradorParam<ParamNodeID_TargetParent>() {
						@Override
						public ParamNodeID_TargetParent generar() {
							ParamNodeID_TargetParent param = new ParamNodeID_TargetParent();
							param.setNodeId(identificador);
							param.setTargetParent(identificadorDesti);
							return param;
						}
					},
					ParamNodeID_TargetParent.class,
					CopyFolderResult.class);
			return crearContingutArxiu(
					resposta.getCopyFolderResult().getResParam(), 
					null,
					ContingutTipus.CARPETA,
					null,
					null,
					null);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public void carpetaMoure(
			final String identificador,
			final String identificadorDesti) throws ArxiuException {
		String metode = Servicios.MOVE_FOLDER;
		try {
			getArxiuClient().generarEnviarPeticio(
					metode,
					MoveFolder.class,
					new GeneradorParam<ParamNodeID_TargetParent>() {
						@Override
						public ParamNodeID_TargetParent generar() {
							ParamNodeID_TargetParent param = new ParamNodeID_TargetParent();
							param.setNodeId(identificador);
							param.setTargetParent(identificadorDesti);
							return param;
						}
					},
					ParamNodeID_TargetParent.class,
					MoveFolderResult.class);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"S'ha produit un error cridant el mètode " + metode,
					ex);
		}
	}

	@Override
	public List<DocumentTipusAddicional> documentTipusAddicionals() {
		return null;
	}

	@Override
	public boolean suportaVersionatExpedient() {
		return true;
	}

	@Override
	public boolean suportaVersionatDocument() {
		return true;
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

	private ContingutArxiu crearContingutArxiu(
			String identificador, 
			String nom,
			ContingutTipus tipus,
			String versio,
			ExpedientMetadades expedientMetadades,
			DocumentMetadades documentMetadades) {
		ContingutArxiu informacioItem = new ContingutArxiu(tipus);
		informacioItem.setIdentificador(identificador);
		informacioItem.setNom(nom);
		informacioItem.setExpedientMetadades(expedientMetadades);
		informacioItem.setDocumentMetadades(documentMetadades);
		informacioItem.setVersio(versio);
		return informacioItem;
	}

	private List<ContingutArxiu> expedientVersionsComu(
			final String identificador) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, UniformInterfaceException, ClientHandlerException, IOException, NoSuchMethodException, SecurityException, Exception {
		String metode = Servicios.GET_VERSION_FILE;
		GetFileVersionListResult resposta = getArxiuClient().generarEnviarPeticio(
				metode,
				GetFileVersionList.class,
				new GeneradorParam<ParamNodeId>() {
					@Override
					public ParamNodeId generar() {
						ParamNodeId param = new ParamNodeId();
						param.setNodeId(identificador);
						return param;
					}
				},
				ParamNodeId.class,
				GetFileVersionListResult.class);
		List<VersionNode> versions = resposta.getGetFileVersionListResult().getResParam();
		Collections.sort(
				versions,
				new Comparator<VersionNode>() {
					public int compare(VersionNode vn1, VersionNode vn2) {
						return vn1.getDate().compareTo(vn2.getDate());
					}
				});
		List<ContingutArxiu> continguts = new ArrayList<ContingutArxiu>();
		for (VersionNode versio: versions) {
			continguts.add(
					ArxiuConversioHelper.crearContingutArxiu(
							identificador,
							null,
							ContingutTipus.DOCUMENT,
							String.valueOf(versio.getId())));
		}
		return continguts;
	}
	private String expedientDarreraVersio(
			String identificador) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, UniformInterfaceException, ClientHandlerException, IOException, NoSuchMethodException, SecurityException, Exception {
		String darreraVersio = null;
		List<ContingutArxiu> versions = expedientVersionsComu(
				identificador);
		if (versions != null && !versions.isEmpty()) {
			darreraVersio = versions.get(versions.size() - 1).getVersio();
		}
		return darreraVersio;
	}

	private List<ContingutArxiu> documentVersionsComu(
			final String identificador) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, UniformInterfaceException, ClientHandlerException, IOException, NoSuchMethodException, SecurityException, Exception {
		String metode = Servicios.GET_VERSION_DOC;
		GetDocVersionListResult resposta = getArxiuClient().generarEnviarPeticio(
				metode,
				GetDocVersionList.class,
				new GeneradorParam<ParamNodeId>() {
					@Override
					public ParamNodeId generar() {
						ParamNodeId param = new ParamNodeId();
						param.setNodeId(identificador);
						return param;
					}
				},
				ParamNodeId.class,
				GetDocVersionListResult.class);
		List<VersionNode> versions = resposta.getGetDocVersionListResult().getResParam();
		Collections.sort(
				versions,
				new Comparator<VersionNode>() {
					public int compare(VersionNode vn1, VersionNode vn2) {
						return vn1.getDate().compareTo(vn2.getDate());
					}
				});
		List<ContingutArxiu> continguts  = new ArrayList<ContingutArxiu>();
		for (VersionNode versio: versions) {
			continguts.add(
					ArxiuConversioHelper.crearContingutArxiu(
							identificador,
							null,
							ContingutTipus.DOCUMENT,
							String.valueOf(versio.getId())));
		}
		return continguts;
	}
	private String documentDarreraVersio(
			String identificador) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, UniformInterfaceException, ClientHandlerException, IOException, NoSuchMethodException, SecurityException, Exception {
		String darreraVersio = null;
		List<ContingutArxiu> versions = documentVersionsComu(
				identificador);
		if (versions != null && !versions.isEmpty()) {
			darreraVersio = versions.get(versions.size() - 1).getVersio();
		}
		return darreraVersio;
	}

	private String findSerieDocumentalExpedientPare(
			Document document,
			String identificadorPare) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, UniformInterfaceException, ClientHandlerException, IOException {
		if (document != null && document.getMetadades() != null) {
			String serieDocumental = document.getMetadades().getSerieDocumental();
			if (serieDocumental != null) {
				return serieDocumental;
			}
		}
		
		GetFileResult expedientPare = null;
		try {
			expedientPare = getFileResult(
					identificadorPare, 
					null);
		} catch (Exception ex) {
			throw new ArxiuValidacioException(
					"No s'ha pogut trobar la sèrie documental del contingut pare (identificadorPare=" + identificadorPare + ").");
		}
		
		List<Metadata> metadatas = expedientPare.getGetFileResult().getResParam().getMetadataCollection();
		String serieDocumental = null;
		for (Metadata metadata: metadatas) {
			if (MetadatosDocumento.CODIGO_CLASIFICACION.equals(metadata.getQname())) {
				serieDocumental = (String)metadata.getValue();
				break;
			}
		}
		if (serieDocumental != null) {
			return serieDocumental;
		} else {
			throw new ArxiuValidacioException(
					"No s'ha pogut trobar la sèrie documental de l'expedient pare (identificadorPare=" + identificadorPare + ").");
		}
	}


	private GetFileResult getFileResult(
			final String identificador,
			final String versio) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, UniformInterfaceException, ClientHandlerException, IOException, NoSuchMethodException, SecurityException, Exception {
		String metode = Servicios.GET_FILE;
		return getArxiuClient().generarEnviarPeticio(
				metode,
				GetFile.class,
				new GeneradorParam<ParamNodeId>() {
					@Override
					public ParamNodeId generar() {
						ParamNodeId param = new ParamNodeId();
						String identificadorAmbVersio;
						if (versio != null) {
							identificadorAmbVersio = versio + "@" + identificador;
						} else {
							identificadorAmbVersio = identificador;
						}
						param.setNodeId(identificadorAmbVersio);
						return param;
					}
				},
				ParamNodeId.class,
				GetFileResult.class);
	}

	private GetDocumentResult getDocumentResult(
			final String identificador,
			final String versio,
			final boolean ambContingut) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, UniformInterfaceException, ClientHandlerException, IOException, NoSuchMethodException, SecurityException, Exception {
		String metode = Servicios.GET_DOC;
		return getArxiuClient().generarEnviarPeticio(
				metode,
				GetDocument.class,
				new GeneradorParam<ParamGetDocument>() {
					@Override
					public ParamGetDocument generar() {
						ParamGetDocument param = new ParamGetDocument();
						DocumentId documentId = new DocumentId();
						String identificadorAmbVersio;
						if (versio != null) {
							identificadorAmbVersio = versio + "@" + identificador;
						} else {
							identificadorAmbVersio = identificador;
						}
						documentId.setNodeId(identificadorAmbVersio);
						param.setDocumentId(documentId);
						param.setContent(String.valueOf(ambContingut));
						return param;
					}
				},
				ParamGetDocument.class,
				GetDocumentResult.class);
	}

	private void comprovarAbsenciaMetadadaCsv(
			DocumentMetadades metadades) {
		if (metadades != null && metadades.getMetadadesAddicionals() != null) {
			if (metadades.getMetadadesAddicionals().containsKey(MetadatosDocumento.CSV)) {
				throw new ArxiuValidacioException(
						"No és possible especificar la metadada " + MetadatosDocumento.CSV + " per als documents. " +
						"Aquesta metadada la gestiona l'arxiu de forma automàtica.");
			}
			if (metadades.getMetadadesAddicionals().containsKey(MetadatosDocumento.DEF_CSV)) {
				throw new ArxiuValidacioException(
						"No és possible especificar la metadada " + MetadatosDocumento.DEF_CSV + " per als documents. " +
						"Aquesta metadada la gestiona l'arxiu de forma automàtica.");
			}
		}
	}
	
	private void comprovarCsv(String csv) {
		if (csv == null) {
			throw new ArxiuValidacioException(
					"El CSV obtingut de l'Arxiu és null" + csv);			
		}
		if (!csv.matches("^([0-9a-f]{64})$")) {			
			throw new ArxiuValidacioException(
					"El CSV obtingut de l'Arxiu no té un format correcte : \"" + csv + "\"");
		}
	}


	private void comprovarFirma(
			final Document document) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, UniformInterfaceException, ClientHandlerException, IOException, NoSuchMethodException, SecurityException, Exception {
		if (document.getFirmes() != null && !document.getFirmes().isEmpty()) {
			FirmaTipus firmaTipus = null;
			for (Firma firma: document.getFirmes()) {
				if (firmaTipus == null) {
					firmaTipus = firma.getTipus();
				} else {
					if (!firmaTipus.equals(firma.getTipus())) {
						throw new ArxiuValidacioException(
								"El document no pot contenir firmes de diferents tipus");
					}
				}
			}
			// Validació temporal per evitar problema en el tancament d'Arxiu #61
			// Desactivada en la versió 2.0.20 #62
			//if (FirmaTipus.CADES_DET.equals(firmaTipus)) {
			//	throw new ArxiuValidacioException(
			//			"De moment l'Arixu no soporta firmes de tipus TF04 - CAdES detached/explicit signature");
			//}
			boolean formatComprovat = false;
			if (document.getMetadades() != null && document.getMetadades().getFormat() != null) {
				if (FirmaTipus.PADES.equals(firmaTipus)) {
					formatComprovat = true;
					if (!DocumentFormat.PDF.equals(document.getMetadades().getFormat())) {
						throw new ArxiuValidacioException(
								"Un document que no te el format PDF (" + document.getMetadades().getFormat() + ") no pot tenir una firma de tipus PADES");
					}
					if (document.getFirmes().size() > 1) {
						throw new ArxiuValidacioException(
								"Només es pot especificar una firma de tipus PAdES");
					}
					Firma firma = document.getFirmes().get(0);
					if (document.getContingut() != null && firma.getContingut() != null) {
						throw new ArxiuValidacioException(
								"Al firmar un document amb firma PAdES no es pot especificar a la vegada el contingut del document i el contingut de la firma");
					}
				}
			}
			if (document.getIdentificador() != null) {
				String metode = Servicios.GET_DOC;
				GetDocumentResult resposta = getArxiuClient().generarEnviarPeticio(
						metode,
						GetDocument.class,
						new GeneradorParam<ParamGetDocument>() {
							@Override
							public ParamGetDocument generar() {
								ParamGetDocument param = new ParamGetDocument();
								DocumentId documentId = new DocumentId();
								documentId.setNodeId(document.getIdentificador());
								param.setDocumentId(documentId);
								param.setContent(String.valueOf(false));
								return param;
							}
						},
						ParamGetDocument.class,
						GetDocumentResult.class);
				Document documentResposta = ArxiuConversioHelper.documentNodeToDocument(
						resposta.getGetDocumentResult().getResParam(),
						null);
				if (firmaTipus != null && documentResposta.getFirmes() != null && !documentResposta.getFirmes().isEmpty()) {
					Firma primeraFirma = getPrimeraFirmaNoCsv(documentResposta);
					if (primeraFirma != null && !firmaTipus.equals(primeraFirma.getTipus())) {
						throw new ArxiuValidacioException(
								"El document de l'arxiu ja està firmat i el tipus de firma especificat (" + firmaTipus + ") no coicideix amb l'existent a l'arxiu (" + primeraFirma.getTipus() + ")");
					}
				}
				if (!formatComprovat && documentResposta.getMetadades() != null && documentResposta.getMetadades().getFormat() != null) {
					if (FirmaTipus.PADES.equals(firmaTipus)) {
						if (!DocumentFormat.PDF.equals(documentResposta.getMetadades().getFormat())) {
							throw new ArxiuValidacioException(
									"Un document que no te el format PDF (" + documentResposta.getMetadades().getFormat() + ") no pot tenir una firma de tipus PADES");
						}
					}
				}
			}
		}
	}

	private Firma getPrimeraFirmaNoCsv(Document document) {
		for (Firma firma: document.getFirmes()) {
			if (firma.getTipus() != FirmaTipus.CSV) {
				return firma;
			}
		}
		return null;
	}

	private InputStream generarVersioImprimible(
			String identificador,
			String metadada1,
			String metadada2,
			String marcaAigua) throws IOException {
		String url = getPropertyConversioImprimibleUrl();
		if (url == null || url.trim().isEmpty()) {
			throw new ArxiuException("No s'ha definit la propietat plugin.arxiu.caib.conversio.imprimible.url amb la URL del servei de conversió de documents.");
		}
		WebResource webResource;
		if (url.endsWith("/")) {
			webResource = getVersioImprimibleClient().
					resource(url + identificador);
		} else {
			webResource = getVersioImprimibleClient().
					resource(url + "/" + identificador);
		}
		if (metadada1 != null) {
			webResource.queryParam("metadata1", metadada1);
		}
		if (metadada2 != null) {
			webResource.queryParam("metadata2", metadada2);
		}
		if (marcaAigua != null) {
			webResource.queryParam("watermark", marcaAigua);
		}
		return webResource.get(InputStream.class);
	}

	public static String generarConsulta(
			String queryType,
			List<ConsultaFiltre> filtres) throws ArxiuException {
		StringBuilder query = new StringBuilder();
		query.append("+TYPE:");
		query.append(queryType);
		query.append(" ");
		for (int i = 0; i < filtres.size(); i++) {
			ConsultaFiltre filtre = filtres.get(i);
			String metadada = filtre.getMetadada();
			String valor1 = filtre.getValorOperacio1();
			String valor2 = filtre.getValorOperacio2();
			String metadadaPerFiltre = metadada.replace(":", "\\:");
			ConsultaOperacio consultaOperacio = filtre.getOperacio();
			if (consultaOperacio != null) {
				switch(filtre.getOperacio()) {
				case CONTE:
				case ENTRE:
				case IGUAL:
				case MAJOR:
				case MENOR:
					query.append("+");
					break;
				case NO_CONTE:
				case NO_IGUAL:
					query.append("-");
					break;
				}
				query.append("@");
				switch(filtre.getOperacio()) {
					case IGUAL:
						query.append(metadadaPerFiltre);
						query.append(":\"" + valor1 + "\"");
						break;
					case CONTE:
						query.append(metadadaPerFiltre);
						query.append(":*" + valor1 + "*");
						break;
					case MAJOR:
						query.append(metadadaPerFiltre);
						query.append(":[" + valor1 + " TO *] -");
						query.append(metadadaPerFiltre);
						query.append(":\"" + valor1 + "\"");
						break;
					case MENOR:
						query.append(metadadaPerFiltre);
						query.append(":[* TO " + valor1 + "] -");
						query.append(metadadaPerFiltre);
						query.append(":\"" + valor1 + "\"");
						break;
					case ENTRE:
						query.append(metadadaPerFiltre);
						query.append(":[" + valor1 + " TO " + valor2 + "]");
						break;
					case NO_IGUAL:
						query.append(metadadaPerFiltre);
						query.append(":\"" + valor1 + "\"");
						break;
					case NO_CONTE:
						query.append(metadadaPerFiltre);
						query.append(":*" + valor1 + "*");
						break;
				}
				if (i < filtres.size()-1) {
					query.append(" AND ");
				}
			} else {
				throw new ArxiuException("No s'ha definit cap operació pel filtre de la metadada " + metadada);
			}
		}
		return query.toString();
	}

	private ArxiuCaibClient getArxiuClient() throws Exception {
		if (arxiuClient == null) {
			arxiuClient = new ArxiuCaibClient(
					getPropertyBaseUrl(),
					getPropertyAplicacioCodi(),
					getPropertyUsuari(),
					getPropertyContrasenya(),
					getPropertyTimeoutConnect(),
					getPropertyTimeoutRead(), 
					isDebug(),
					loggerLevel());
		}
		return arxiuClient;
	}
	private Client getVersioImprimibleClient() {
		if (versioImprimibleClient == null) {
			versioImprimibleClient = Client.create();
			versioImprimibleClient.setConnectTimeout(
					getPropertyTimeoutConnect());
			versioImprimibleClient.setReadTimeout(
					getPropertyTimeoutRead());
			String usuari = getPropertyConversioImprimibleUsuari();
			String contrasenya = getPropertyConversioImprimibleContrasenya();
			if (usuari != null) {
				versioImprimibleClient.addFilter(
						new HTTPBasicAuthFilter(usuari, contrasenya));
			}
		}
		return versioImprimibleClient;
	}

	private String getPropertyBaseUrl() throws Exception {
		return getPropertyRequired(ARXIUCAIB_BASE_PROPERTY + "base.url");
	}
	private String getPropertyAplicacioCodi() throws Exception {
		return getPropertyRequired(ARXIUCAIB_BASE_PROPERTY + "aplicacio.codi");
	}
	private String getPropertyUsuari() throws Exception {
		return getPropertyRequired(ARXIUCAIB_BASE_PROPERTY + "usuari");
	}
	private String getPropertyContrasenya() throws Exception {
		return getPropertyRequired(ARXIUCAIB_BASE_PROPERTY + "contrasenya");
	}
	private String getPropertyDefinicioCsv() {
		
		String definicioCsv = getProperty(ARXIUCAIB_BASE_PROPERTY + "csv.definicio");
		if (definicioCsv != null && !definicioCsv.isEmpty()) {
			// Propietat plugin.arxiu.caib.csv.definicio en desús
			logger.error("La propietat '" + ARXIUCAIB_BASE_PROPERTY + "csv.definicio' està deprecada."
								+ " Per favor faci ús de la propietat '" + getPropertyBase()
								+ ABSTRACT_CSV_GENERATION_DEFINITION + "'.", new Exception());
		} else {
			// Propietat de l'AbstractArxiuPlugin plugin.arxiu.caib.csv_generation_definition
			definicioCsv = getProperty(getPropertyBase() + ABSTRACT_CSV_GENERATION_DEFINITION);
		}
		return definicioCsv;
	}
	private String getPropertyConversioImprimibleUrl() {
		return getProperty(ARXIUCAIB_BASE_PROPERTY + "conversio.imprimible.url");
	}
	private String getPropertyConversioImprimibleUsuari() {
		return getProperty(ARXIUCAIB_BASE_PROPERTY + "conversio.imprimible.usuari");
	}
	private String getPropertyConversioImprimibleContrasenya() {
		return getProperty(ARXIUCAIB_BASE_PROPERTY + "conversio.imprimible.contrasenya");
	}
	
	private String loggerLevel() {
		String level = getProperty(ARXIUCAIB_BASE_PROPERTY + "debug");
		if ("true".equals(level)) {
			level = "debug"; 
		}
	  return level;
	}
	private boolean isDebug() {
		  return getProperty(ARXIUCAIB_BASE_PROPERTY + "debug") != null 
				  && "false".compareTo(getProperty(ARXIUCAIB_BASE_PROPERTY + "debug")) != 0 ;
	}
	
	
	private int getPropertyTimeoutConnect() {
		String timeout = getProperty(
				ARXIUCAIB_BASE_PROPERTY + "timeout.connect",
				JERSEY_TIMEOUT_CONNECT);
		return Integer.parseInt(timeout);
	}
	private int getPropertyTimeoutRead() {
		String timeout = getProperty(
				ARXIUCAIB_BASE_PROPERTY + "timeout.read",
				JERSEY_TIMEOUT_READ);
		return Integer.parseInt(timeout);
	}

	@Override
	public String getCsv(String identificadorDoc) throws ArxiuException {
		Document doc =  documentDetalls(identificadorDoc, null, false);
		List<Firma> firmes = doc.getFirmes();
		String csv = null;
		for (Firma firma : firmes) {
			if (firma.getTipus() == FirmaTipus.CSV) {
				csv = new String(firma.getContingut());
				break;
			}
		}
		return csv; 
	}

	@Override
	protected String getPropertyBase() {
		return ARXIUCAIB_BASE_PROPERTY;
	}

	@Override
	public String getCsvGenerationDefinition(String identificadorDoc) throws ArxiuException {
		String csvGenerationDefinitionEL = getPropertyDefinicioCsv();
		return processExpressionLanguage(identificadorDoc, csvGenerationDefinitionEL);
	}
	
	public int getItemsPerPaginaArxiu() {
		return itemsPerPaginaArxiu;
	}

	public void setItemsPerPaginaArxiu(int itemsPerPaginaArxiu) {
		this.itemsPerPaginaArxiu = itemsPerPaginaArxiu;
	}


	private static final Logger logger = LoggerFactory.getLogger(ArxiuPluginCaib.class);
}
