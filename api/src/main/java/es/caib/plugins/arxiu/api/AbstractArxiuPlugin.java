package es.caib.plugins.arxiu.api;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.fundaciobit.pluginsib.core.utils.AbstractPluginProperties;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 
 * @author anadal(u80067)
 *
 */
public abstract class AbstractArxiuPlugin extends AbstractPluginProperties implements
    IArxiuPlugin {

  public static final String ABSTRACT_ORIGINAL_FILE_URL_EXPRESSION_LANGUAGE = "original_file_url_EL";

  public static final String ABSTRACT_PRINTABLE_FILE_URL_EXPRESSION_LANGUAGE = "printable_file_url_EL";

  public static final String ABSTRACT_ENI_FILE_URL_EXPRESSION_LANGUAGE = "eni_file_url_EL";

  public static final String ABSTRACT_CSV = "csv_EL";

  public static final String ABSTRACT_CSV_VALIDATION_WEB = "csv_validation_web_EL";

  public static final String ABSTRACT_CSV_GENERATION_DEFINITION = "csv_generation_definition";

  public AbstractArxiuPlugin() {
    super();
  }

  public AbstractArxiuPlugin(String propertyKeyBase, Properties properties) {
    super(propertyKeyBase, properties);
  }

  public AbstractArxiuPlugin(String propertyKeyBase) {
    super(propertyKeyBase);
  }

  // Implementar
  protected abstract String getPropertyBase();

  @Override
  public String getOriginalFileUrl(String identificadorDoc) throws ArxiuException {

    String csvValidationUrlEL = getProperty(getPropertyBase()
        + ABSTRACT_ORIGINAL_FILE_URL_EXPRESSION_LANGUAGE);

    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("uuid", identificadorDoc);

    return processExpressionLanguage(csvValidationUrlEL, parameters);

  }

  /**
  * 
  */
  public String getPrintableFileUrl(String identificadorDoc) throws ArxiuException {

    String csvValidationUrlEL = getProperty(getPropertyBase()
        + ABSTRACT_PRINTABLE_FILE_URL_EXPRESSION_LANGUAGE);

    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("uuid", identificadorDoc);

    return processExpressionLanguage(csvValidationUrlEL, parameters);
  }

  /**
 * 
 */
  public String getEniFileUrl(String identificadorDoc) throws ArxiuException {

    String csvValidationUrlEL = getProperty(getPropertyBase()
        + ABSTRACT_ENI_FILE_URL_EXPRESSION_LANGUAGE);

    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("uuid", identificadorDoc);

    return processExpressionLanguage(csvValidationUrlEL, parameters);

  }

  @Override
  public String getCsvValidationWeb(String identificadorDoc) throws ArxiuException {

    String csvValidatioWebEL = getProperty(getPropertyBase() + ABSTRACT_CSV_VALIDATION_WEB);

    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("uuid", identificadorDoc);

    return processExpressionLanguage(csvValidatioWebEL, parameters);
  }

  @Override
  public String getCsvGenerationDefinition(String identificadorDoc) throws ArxiuException {

    String csvGenerationDefinitionEL = getProperty(getPropertyBase()
        + ABSTRACT_CSV_GENERATION_DEFINITION);

    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("uuid", identificadorDoc);

    return processExpressionLanguage(csvGenerationDefinitionEL, parameters);
  }

  public static String processExpressionLanguage(String plantilla,
      Map<String, Object> custodyParameters) throws ArxiuException {
    return processExpressionLanguage(plantilla, custodyParameters, null);
  }

  public static String processExpressionLanguage(String plantilla,
      Map<String, Object> custodyParameters, Locale locale) throws ArxiuException {
    try {
      if (custodyParameters == null) {
        custodyParameters = new HashMap<String, Object>();
      }

      Configuration configuration;

      configuration = new Configuration(Configuration.VERSION_2_3_23);
      configuration.setDefaultEncoding("UTF-8");
      if (locale != null) {
        configuration.setLocale(locale);
      }
      Template template;
      template = new Template("exampleTemplate", new StringReader(plantilla), configuration);

      Writer out = new StringWriter();
      template.process(custodyParameters, out);

      String res = out.toString();
      return res;
    } catch (Exception e) {
      final String msg = "No s'ha pogut processar l'Expression Language " + plantilla + ":"
          + e.getMessage();
      throw new ArxiuException(msg, e);
    }
  }

}
