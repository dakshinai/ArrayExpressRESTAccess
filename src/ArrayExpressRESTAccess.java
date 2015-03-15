import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ArrayExpressRESTAccess {

	public static String FILE_ACCESS = "http://www.ebi.ac.uk/arrayexpress/xml/v2/experiments?exptype=\"RNA+assay\"&species=\"homo+sapiens\"&keywords=";

	/**
	 * @param args
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws XPathExpressionException
	 */
	public static void main(String[] args) throws IOException,
			ParserConfigurationException, SAXException,
			XPathExpressionException {
		// TODO Auto-generated method stub

		BufferedReader br = new BufferedReader(new FileReader(
				"ArrayExpressQueries.txt"));
		String line;
		ArrayList<String> queries = new ArrayList<String>();
		while ((line = br.readLine()) != null) {
			line=line.replace(" ","+");
			line = "\""+line+"\"";
			//System.out.println(line);
			queries.add(line);
		}

		br.close();
		
		for (String query : queries) {
			String accessionNumbers = getAccessionNumbers(query);
			if (accessionNumbers.length() > 0) {
				accessionNumbers = accessionNumbers.substring(0,
						accessionNumbers.length() - 1);
			}
			
			System.out.println(query + "," + accessionNumbers);
		}
	}

	public static String getAccessionNumbers(String query) throws IOException,
			SAXException, ParserConfigurationException,
			XPathExpressionException {
		//System.out.println(FILE_ACCESS + query);
		String s = httpGet(FILE_ACCESS + query);
		//System.out.println(s);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(s));
		Document doc = builder.parse(is);

		// create an XPathFactory
		XPathFactory xFactory = XPathFactory.newInstance();

		// create an XPath object
		XPath xpath = xFactory.newXPath();

		// compile the XPath expression
		//XPathExpression expr = xpath.compile("//files/experiment/accession/text()");
		XPathExpression expr = xpath.compile("//experiments/experiment/accession/text()");
		// run the query and get a nodeset
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		
		//XPathExpression expr2 = xpath.compile("//experiments/experiment/description/text/text()");
		//Object result2 = expr2.evaluate(doc, XPathConstants.NODE);
		//Node node=(Node)result2;
		//if(node!=null)
		//System.out.println(node.getNodeValue());

		// cast the result to a DOM NodeList
		NodeList nodes = (NodeList) result;
		StringBuilder sb = new StringBuilder();
		if(nodes!=null)
		{
			sb.append(nodes.getLength()+":");
		for (int i = 0; i < nodes.getLength(); i++) {
			sb.append(nodes.item(i).getNodeValue());
			sb.append(";");
		}
		}

		return sb.toString();
	}

	public static String httpGet(String urlStr) throws IOException {
		URL url = new URL(urlStr);
		
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		if (conn.getResponseCode() != 200) {
			throw new IOException(conn.getResponseMessage());
		}

		// Buffer the result into a string
		BufferedReader rd = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();

		conn.disconnect();
		return sb.toString();
	}

}
