import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class main {

	private static final String FICHERO_RESULTADO = "resultadosNuevo.xml";
	private static final String EMPATE = "X";
	private static final String GANA_VISITANTE = "2";
	private static final String GANA_LOCAL = "1";
	private static final String ATRIBUTO_RESULTADO_QUINIELA = "resultadoQuiniela";
	private static final String FICHERO_ORIGINAL = "resultados.xml";
	private static final String ETIQUETA_GOLES = "goles";
	private static final String ETIQUETA_PARTIDO = "partido";
	
	
	public static void main(String[] args) {
		try {

			Document arbolDocumento = crearArbolDOM();

			crearAtributoResultado(arbolDocumento);
			
			transformarDOMEnXml(arbolDocumento);
			System.out.println("Documento " + FICHERO_RESULTADO+ " creado correctamente.");

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}


	
	
	public static void crearAtributoResultado (Document arbol) throws IOException{

		Element partidoActual,elementoGolesLocal,elementoGolesVisitante, elementoNuevo;
		NodeList listaPartidos, listaGoles;
		String quiniela;
		int numeroGolesLocal, numeroGolesVisitante;

		
		
		Element raiz = (Element)arbol.getFirstChild();
		
		listaPartidos = raiz.getElementsByTagName("partido");
		
		for (int i = 0; i < listaPartidos.getLength(); i++) {
			
			partidoActual = (Element) listaPartidos.item(i);;

			
			//obtener Lista de los goles
			listaGoles = partidoActual.getElementsByTagName("goles");
			
			elementoGolesLocal = (Element) listaGoles.item(0);
			elementoGolesVisitante = (Element) listaGoles.item(1);
			
			
			//Crear nuevo nodo
			elementoNuevo = arbol.createElement("quiniela");
			// Primera forma (más corta)
			numeroGolesLocal = Integer.parseInt(elementoGolesLocal.getTextContent().trim());
			numeroGolesVisitante = Integer.parseInt(elementoGolesVisitante.getTextContent().trim());
			if (numeroGolesLocal > numeroGolesVisitante) {
				quiniela = GANA_LOCAL;
			} else {
				if (numeroGolesLocal < numeroGolesVisitante) {
					quiniela = GANA_VISITANTE;
				} else {
					quiniela = EMPATE;
				}
			}
			partidoActual.appendChild((Node) elementoNuevo);
			elementoNuevo.appendChild(arbol.createTextNode(quiniela));

		}
	
	}


	private static void transformarDOMEnXml(Document arbolDocumento)
			throws TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		Source source = new DOMSource(arbolDocumento);
		Result result = new StreamResult(FICHERO_RESULTADO);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(source, result);
	}


	private static Document crearArbolDOM() throws ParserConfigurationException, SAXException, IOException {
		// Extraemos el arbol de nuestro documento XML.
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document arbolDocumento = builder.parse(new File(FICHERO_ORIGINAL));
		return arbolDocumento;
	}

}
