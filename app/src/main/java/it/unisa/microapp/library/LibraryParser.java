package it.unisa.microapp.library;

import it.unisa.microapp.data.DataType;
import it.unisa.microapp.support.FileManagement;
import it.unisa.microapp.utils.Constants;
import it.unisa.microapp.utils.Utils;
import it.unisa.microapp.webservice.piece.WSContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.location.Location;
import android.location.LocationManager;

public class LibraryParser {
	private static ArrayList<String> categoryList = new ArrayList<String>();
	private static ArrayList<String> iconList = new ArrayList<String>();
	private static ArrayList<String> functionList = new ArrayList<String>();

	private static Map<String, String[]> keywordList = new HashMap<String, String[]>();

	public static Map<String, ArrayList<LibraryComponent>> parse(InputStream is) throws ParserConfigurationException, SAXException,
			IOException {
		// categoryList = new ArrayList<String>();
		Map<String, ArrayList<LibraryComponent>> components = new HashMap<String, ArrayList<LibraryComponent>>();
		DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = df.newDocumentBuilder();

		Document d = db.parse(is);

		ArrayList<LibraryComponent> lista;
		NodeList l = d.getElementsByTagName("category");

		// LISTA DELLE CATEGORY
		for (int i = 0; i < l.getLength(); i++) {
			lista = new ArrayList<LibraryComponent>();
			LibraryComponent lc = null;
			Element el = (Element) l.item(i);
			String category = el.getAttribute("name");
			if (!categoryList.contains(category)) {
				categoryList.add(category);
			}
			iconList.add(el.getAttribute("icon"));

			String keywords = el.getAttribute("keywords");

			String[] tokens = getKeyWordsToken(keywords);

			keywordList.put(category, tokens);

			NodeList functions = el.getElementsByTagName("function");

			for (int j = 0; j < functions.getLength(); j++) {
				Element func = (Element) functions.item(j);
				functionList.add(func.getAttribute("id"));

				if (category.equals("WebService") || func.getAttribute("id").startsWith("WEBSERVICE")) {
					lc = new WSLibraryComponent(func.getAttribute("id"), func.getAttribute("name"), category, func.getAttribute("icon"));
					((WSLibraryComponent) lc).setWsdl(func.getAttribute("wsdl"));
					((WSLibraryComponent) lc).setOperation(func.getAttribute("operation"));
					((WSLibraryComponent) lc).setPort(func.getAttribute("port"));
					((WSLibraryComponent) lc).setService(func.getAttribute("service"));
					((WSLibraryComponent) lc).setTns(func.getAttribute("tns"));
					((WSLibraryComponent) lc).setUri(func.getAttribute("uri"));

					getContext(lc, (Element) func.getElementsByTagName("context").item(0));
				} else
					lc = new LibraryComponent(func.getAttribute("id"), func.getAttribute("name"), category, func.getAttribute("icon"));

				NodeList nDesc = func.getElementsByTagName("description");
				if (nDesc.getLength() > 0) {
					Element desc = (Element) nDesc.item(0);
					lc.setDescription(desc.getTextContent());
				}

				String nStates = func.getAttribute("states");
				lc.setAllStates(nStates);

				// LISTA DEGLI INPUT
				NodeList inputs = func.getElementsByTagName("input");
				int lunglist = inputs.getLength();
				DataTypeMulti[] inputsArray = new DataTypeMulti[lunglist];
				for (int r = 0; r < inputs.getLength(); r++) {
					Element elem = (Element) inputs.item(r);

					String multi = elem.getAttribute("multi");
					boolean b = multi.equals("true");
					inputsArray[r] = new DataTypeMulti(DataType.valueOf(elem.getAttribute("type")), b, elem.getAttribute("name"));
				}
				lc.inputs = inputsArray;

				// LISTA DEGLI OUTPUT
				NodeList outputs = func.getElementsByTagName("output");
				lunglist = outputs.getLength();
				DataTypeMulti[] outputsArray = new DataTypeMulti[lunglist];
				for (int r = 0; r < lunglist; r++) {

					Element elem = (Element) outputs.item(r);
					String multi = elem.getAttribute("multi");
					boolean b = multi.equals("true");
					outputsArray[r] = new DataTypeMulti(DataType.valueOf(elem.getAttribute("type")), b, elem.getAttribute("name"));
				}
				lc.outputs = outputsArray;
				NodeList userin = func.getElementsByTagName("userinput");
				lunglist = userin.getLength();

				// String[] userArray = new String[lunglist];
				DataUserType[] userArray = new DataUserType[lunglist];
				for (int r = 0; r < lunglist; r++) {
					Element elem = (Element) userin.item(r);
					// BISOGNA GESTIRE IL MULTI
					userArray[r] = new DataUserType(DataType.valueOf(elem.getAttribute("type")), elem.getAttribute("name"),
							elem.getAttribute("input"));
				}
				lc.userInputs = userArray;
				lista.add(lc);
			}
			components.put(category, lista);
		}

		File compagg = new File(FileManagement.getDefaultPath(), "componentiAggiunti.xml");

		if (compagg.exists()) {

			ArrayList<LibraryComponent> lista_fc1_componenti_aggiunti;
			try {
				DocumentBuilderFactory df_fc1 = DocumentBuilderFactory.newInstance();
				DocumentBuilder db_fc1;

				db_fc1 = df_fc1.newDocumentBuilder();

				Document d_fc1 = db_fc1.parse(new FileInputStream(compagg));

				NodeList l_fc1 = d_fc1.getElementsByTagName("category");

				// LISTA DELLE CATEGORY
				for (int i = 0; i < l_fc1.getLength(); i++) {
					lista_fc1_componenti_aggiunti = new ArrayList<LibraryComponent>();
					LibraryComponent lc_fc1 = null;
					Element el_fc1 = (Element) l_fc1.item(i);
					String category = el_fc1.getAttribute("name");
					if (!categoryList.contains(category)) {
						categoryList.add(category);
					}
					iconList.add(el_fc1.getAttribute("icon"));

					String keywords = el_fc1.getAttribute("keywords");

					String[] tokens = getKeyWordsToken(keywords);

					keywordList.put(category, tokens);

					NodeList functions_fc1 = el_fc1.getElementsByTagName("function");

					for (int j = 0; j < functions_fc1.getLength(); j++) {
						Element func_fc1 = (Element) functions_fc1.item(j);
						functionList.add(func_fc1.getAttribute("id"));

						lc_fc1 = new LibraryComponent(func_fc1.getAttribute("id"), func_fc1.getAttribute("name"), category,
								func_fc1.getAttribute("icon"));

						NodeList nDesc_fc1 = func_fc1.getElementsByTagName("description");
						if (nDesc_fc1.getLength() > 0) {
							Element desc_fc1 = (Element) nDesc_fc1.item(0);
							lc_fc1.setDescription(desc_fc1.getTextContent());
						}
						// LISTA DEGLI INPUT
						NodeList inputs_fc1 = func_fc1.getElementsByTagName("input");
						int lunglist_fc1 = inputs_fc1.getLength();
						DataTypeMulti[] inputsArray_fc1 = new DataTypeMulti[lunglist_fc1];
						for (int r = 0; r < inputs_fc1.getLength(); r++) {
							Element elem_fc1 = (Element) inputs_fc1.item(r);

							String multi_fc1 = elem_fc1.getAttribute("multi");
							boolean b_fc1 = multi_fc1.equals("true");
							inputsArray_fc1[r] = new DataTypeMulti(DataType.valueOf(elem_fc1.getAttribute("type")), b_fc1,
									elem_fc1.getAttribute("name"));
						}
						lc_fc1.inputs = inputsArray_fc1;

						// LISTA DEGLI OUTPUT
						NodeList outputs_fc1 = func_fc1.getElementsByTagName("output");
						lunglist_fc1 = outputs_fc1.getLength();
						DataTypeMulti[] outputsArray_fc1 = new DataTypeMulti[lunglist_fc1];
						for (int r = 0; r < lunglist_fc1; r++) {

							Element elem_fc1 = (Element) outputs_fc1.item(r);
							String multi_fc1 = elem_fc1.getAttribute("multi");
							boolean b_fc1 = multi_fc1.equals("true");
							outputsArray_fc1[r] = new DataTypeMulti(DataType.valueOf(elem_fc1.getAttribute("type")), b_fc1,
									elem_fc1.getAttribute("name"));
						}
						lc_fc1.outputs = outputsArray_fc1;
						NodeList userin_fc1 = func_fc1.getElementsByTagName("userinput");
						lunglist_fc1 = userin_fc1.getLength();

						// String[] userArray = new String[lunglist];
						DataUserType[] userArray_fc1 = new DataUserType[lunglist_fc1];
						for (int r = 0; r < lunglist_fc1; r++) {
							Element elem_fc1 = (Element) userin_fc1.item(r);
							// BISOGNA GESTIRE IL MULTI
							userArray_fc1[r] = new DataUserType(DataType.valueOf(elem_fc1.getAttribute("type")),
									elem_fc1.getAttribute("name"), elem_fc1.getAttribute("input"));
						}
						lc_fc1.userInputs = userArray_fc1;
						lista_fc1_componenti_aggiunti.add(lc_fc1);
					}

					// recupero la lista dei componenti vecchia:
					if (components.containsKey(category)) {

						ArrayList<LibraryComponent> lista_fc1_componenti_vecchi = components.get(category);
						System.err.println(category + " ||  size : " + lista_fc1_componenti_vecchi.size());

						ArrayList<LibraryComponent> lista_fc1_finale_per_category = new ArrayList<LibraryComponent>();

						for (int k = 0; k < lista_fc1_componenti_vecchi.size(); k++) {
							lista_fc1_finale_per_category.add(lista_fc1_componenti_vecchi.get(k));
						}
						for (int k = 0; k < lista_fc1_componenti_aggiunti.size(); k++) {
							lista_fc1_finale_per_category.add(lista_fc1_componenti_aggiunti.get(k));
						}
						components.put(category, lista_fc1_finale_per_category);
					} else {
						ArrayList<LibraryComponent> lista_fc1_finale_per_category = new ArrayList<LibraryComponent>();
						for (int k = 0; k < lista_fc1_componenti_aggiunti.size(); k++) {
							lista_fc1_finale_per_category.add(lista_fc1_componenti_aggiunti.get(k));
						}
						components.put(category, lista_fc1_finale_per_category);
					}
				}

			} catch (ParserConfigurationException e) {
			} catch (FileNotFoundException e) {
			} catch (SAXException e) {
			} catch (IOException e) {
			}
		}

		return components;
	}

	public static void addCategory(String cat) {
		if (!categoryList.contains(cat)) {
			categoryList.add(cat);
			String[] tokens = new String[1];
			tokens[0] = cat;
			keywordList.put(cat, tokens);
		}
	}

	private static String[] getKeyWordsToken(String keywords) {
		List<String> l = new ArrayList<String>();
		StringTokenizer tok = new StringTokenizer(keywords);

		while (tok.hasMoreTokens())
			l.add(tok.nextToken());

		return l.toArray(new String[l.size()]);
	}

	private static void getContext(LibraryComponent lc, Element item) {
		WSContext con = new WSContext();

		Element time = (Element) item.getElementsByTagName("time").item(0);
		Element location = (Element) item.getElementsByTagName("location").item(0);

		String timeval = time.getTextContent();
		String lat = location.getAttribute("latitude");
		String longi = location.getAttribute("longitude");
		String alt = location.getAttribute("altitude");

		Location loc = new Location(LocationManager.GPS_PROVIDER);
		loc.setLatitude(Double.parseDouble(lat));
		loc.setLongitude(Double.parseDouble(longi));
		loc.setAltitude(Double.parseDouble(alt));

		con.setLocation(loc);

		if (!timeval.isEmpty())
			con.setDate(new Date(Long.parseLong(timeval)));

		((WSLibraryComponent) lc).setContext(con);
	}

	public static ArrayList<String> getCategoryList() {
		return categoryList;
	}

	public static ArrayList<String> getIconList() {
		return iconList;
	}

	public static ArrayList<String> getFunctionList() {
		return functionList;
	}

	public static String[] getKeywordsForCategory(String cat) {
		return keywordList.get(cat);
	}

	public static void getFunctionFromRepository(String category, String fileName, ArrayList<LibraryComponent> list) {
		File f = new File(FileManagement.getRepositoryPath(), Constants.WSRepository);

		if (f.exists()) {
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			try {
				docBuilder = dbfac.newDocumentBuilder();
				Document doc = docBuilder.parse(f);

				Element root = (Element) doc.getFirstChild();

				NodeList l = root.getElementsByTagName("category");
				Element el = null;

				// ricavo categoria
				for (int i = 0; i < l.getLength(); i++) {
					el = (Element) l.item(i);
					String name = el.getAttribute("name");

					if (name.equals(category)) {
						NodeList funcl = el.getElementsByTagName("function");

						for (int j = 0; j < funcl.getLength(); j++) {
							Element e = (Element) funcl.item(j);

							LibraryComponent lc = getWSComponent(e, category);
							list.add(lc);
						}	
						break;	
				}



				}
			} catch (ParserConfigurationException e) {
				Utils.error(e.getMessage(), e);
			} catch (SAXException e) {
				Utils.error(e.getMessage(), e);
			} catch (IOException e) {
				Utils.error(e.getMessage(), e);
			}

		}

		f = new File(FileManagement.getRepositoryPath(), Constants.DRepository);

		if (f.exists()) {
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			try {
				docBuilder = dbfac.newDocumentBuilder();
				Document doc = docBuilder.parse(f);

				Element root = (Element) doc.getFirstChild();

				NodeList l = root.getElementsByTagName("category");
				Element el = null;

				// ricavo categoria
				for (int i = 0; i < l.getLength(); i++) {
					el = (Element) l.item(i);
					String name = el.getAttribute("name");

					if (name.equals(category)) {
						NodeList funcl = el.getElementsByTagName("function");

						for (int j = 0; j < funcl.getLength(); j++) {
							Element e = (Element) funcl.item(j);

							LibraryComponent lc = getDComponent(e, category);
							list.add(lc);	
						}	
						break;
				}



				}
			} catch (ParserConfigurationException e) {
				Utils.error(e.getMessage(), e);
			} catch (SAXException e) {
				Utils.error(e.getMessage(), e);
			} catch (IOException e) {
				Utils.error(e.getMessage(), e);
			}

		}		
		
		
	}

	private static LibraryComponent getWSComponent(Element e, String category) {
		WSLibraryComponent ret = new WSLibraryComponent(e.getAttribute("id"), e.getAttribute("name"), category, e.getAttribute("icon"));
		ret.setWsdl(e.getAttribute("wsdl"));
		ret.setOperation(e.getAttribute("operation"));
		ret.setPort(e.getAttribute("port"));
		ret.setService(e.getAttribute("service"));
		ret.setTns(e.getAttribute("tns"));
		ret.setUri(e.getAttribute("uri"));

		// ricavo contesto
		getContext(ret, (Element) e.getElementsByTagName("context").item(0));

		// ricavo descrizione
		if (e.getElementsByTagName("description").getLength() > 0) {
			Element desc = (Element) e.getElementsByTagName("description").item(0);
			ret.setDescription(desc.getTextContent());
		} else
			ret.setDescription("");

		// LISTA DEGLI INPUT
		NodeList inputs = e.getElementsByTagName("input");
		int lunglist = inputs.getLength();
		DataTypeMulti[] inputsArray = new DataTypeMulti[lunglist];
		for (int r = 0; r < inputs.getLength(); r++) {
			Element elem = (Element) inputs.item(r);
			String multi = elem.getAttribute("multi");
			boolean b = multi.equals("true");
			inputsArray[r] = new DataTypeMulti(DataType.valueOf(elem.getAttribute("type")), b, elem.getAttribute("name"));
		}
		ret.inputs = inputsArray;

		// LISTA DEGLI OUTPUT
		NodeList outputs = e.getElementsByTagName("output");
		lunglist = outputs.getLength();
		DataTypeMulti[] outputsArray = new DataTypeMulti[lunglist];
		for (int r = 0; r < lunglist; r++) {

			Element elem = (Element) outputs.item(r);
			String multi = elem.getAttribute("multi");
			boolean b = multi.equals("true");
			outputsArray[r] = new DataTypeMulti(DataType.valueOf(elem.getAttribute("type")), b, elem.getAttribute("name"));
		}
		ret.outputs = outputsArray;
		NodeList userin = e.getElementsByTagName("userinput");
		lunglist = userin.getLength();

		// String[] userArray = new String[lunglist];
		DataUserType[] userArray = new DataUserType[lunglist];
		for (int r = 0; r < lunglist; r++) {
			Element elem = (Element) userin.item(r);
			// BISOGNA GESTIRE IL MULTI
			userArray[r] = new DataUserType(DataType.valueOf(elem.getAttribute("type")), elem.getAttribute("name"),
					elem.getAttribute("input"));
		}
		ret.userInputs = userArray;

		return ret;
	}

	private static LibraryComponent getDComponent(Element e, String category) {
		DLibraryComponent ret = new DLibraryComponent(e.getAttribute("id"), e.getAttribute("name"), category, e.getAttribute("icon"));
		ret.setName(e.getAttribute("dname"));
		ret.setState(e.getAttribute("dstate"));
		ret.setLink(e.getAttribute("dlink"));
		ret.setType(e.getAttribute("dtype"));

		// ricavo descrizione
		if (e.getElementsByTagName("description").getLength() > 0) {
			Element desc = (Element) e.getElementsByTagName("description").item(0);
			ret.setDescription(desc.getTextContent());
		} else
			ret.setDescription("");

		// LISTA DEGLI INPUT
		NodeList inputs = e.getElementsByTagName("input");
		int lunglist = inputs.getLength();
		DataTypeMulti[] inputsArray = new DataTypeMulti[lunglist];
		for (int r = 0; r < inputs.getLength(); r++) {
			Element elem = (Element) inputs.item(r);
			String multi = elem.getAttribute("multi");
			boolean b = multi.equals("true");
			inputsArray[r] = new DataTypeMulti(DataType.valueOf(elem.getAttribute("type")), b, elem.getAttribute("name"));
		}
		ret.inputs = inputsArray;

		// LISTA DEGLI OUTPUT
		NodeList outputs = e.getElementsByTagName("output");
		lunglist = outputs.getLength();
		DataTypeMulti[] outputsArray = new DataTypeMulti[lunglist];
		for (int r = 0; r < lunglist; r++) {

			Element elem = (Element) outputs.item(r);
			String multi = elem.getAttribute("multi");
			boolean b = multi.equals("true");
			outputsArray[r] = new DataTypeMulti(DataType.valueOf(elem.getAttribute("type")), b, elem.getAttribute("name"));
		}
		ret.outputs = outputsArray;
		NodeList userin = e.getElementsByTagName("userinput");
		lunglist = userin.getLength();

		// String[] userArray = new String[lunglist];
		DataUserType[] userArray = new DataUserType[lunglist];
		for (int r = 0; r < lunglist; r++) {
			Element elem = (Element) userin.item(r);
			// BISOGNA GESTIRE IL MULTI
			userArray[r] = new DataUserType(DataType.valueOf(elem.getAttribute("type")), elem.getAttribute("name"),
					elem.getAttribute("input"));
		}
		ret.userInputs = userArray;

		return ret;
	}
	
	
	@SuppressWarnings("unused")
	private static List<String> getListOfFunction(String category, String fileName) {
		List<String> ret = new LinkedList<String>();
		File f = new File(FileManagement.getRepositoryPath() + fileName + ".rep");

		if (f.exists()) {
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			try {
				docBuilder = dbfac.newDocumentBuilder();
				Document doc = docBuilder.parse(f);

				Element root = (Element) doc.getFirstChild();

				NodeList l = root.getElementsByTagName("category");
				Element el = null;

				// ricavo categoria
				for (int i = 0; i < l.getLength(); i++) {
					el = (Element) l.item(i);
					String name = el.getAttribute("name");

					if (name.equals(category))
						break;
				}

				NodeList func = el.getElementsByTagName("function");

				for (int i = 0; i < func.getLength(); i++) {
					ret.add(((Element) func.item(i)).getAttribute("id"));
				}
			} catch (ParserConfigurationException e) {
				Utils.error(e.getMessage(), e);
			} catch (SAXException e) {
				Utils.error(e.getMessage(), e);
			} catch (IOException e) {
				Utils.error(e.getMessage(), e);
			}

		}
		return ret;
	}

}
