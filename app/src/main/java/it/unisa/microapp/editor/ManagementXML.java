package it.unisa.microapp.editor;

import java.io.IOException;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ManagementXML
{
	public String[] RitornaCategorie(InputStream xmlUrl)
	{
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(xmlUrl);
			doc.getDocumentElement().normalize();
			Element root = doc.getDocumentElement();
			NodeList categ = root.getElementsByTagName("category");
			int lung = categ.getLength();
			String[] string = new String[lung];
			for (int i = 0; i < lung; i++)
			{// per ogni categoria
				Element c = (Element) categ.item(i);// nodo
				string[i] = c.getAttribute("name");
			}
			return string;

		} catch (SAXException e)
		{
			// eDebug(e.toString());
			return null;
		} catch (IOException e)
		{
			// eDebug(e.toString());
			return null;
		} catch (ParserConfigurationException e)
		{
			// eDebug(e.toString());
			return null;
		} catch (FactoryConfigurationError e)
		{
			// eDebug(e.toString());
			return null;
		}
	}

	public String[] RitornaIconeCategorie(InputStream xmlUrl)
	{

		try
		{

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(xmlUrl);
			doc.getDocumentElement().normalize();
			Element root = doc.getDocumentElement();
			NodeList categ = root.getElementsByTagName("category");
			int lung = categ.getLength();
			String[] string = new String[lung];
			for (int i = 0; i < lung; i++)
			{// per ogni categoria
				Element c = (Element) categ.item(i);// nodo
				string[i] = c.getAttribute("icon");
			}
			return string;

		} catch (SAXException e)
		{
			// eDebug(e.toString());
			return null;
		} catch (IOException e)
		{
			// eDebug(e.toString());
			return null;
		} catch (ParserConfigurationException e)
		{
			// eDebug(e.toString());
			return null;
		} catch (FactoryConfigurationError e)
		{
			// eDebug(e.toString());
			return null;
		}
	}

	public String RitornaIconaCategoria(InputStream xmlUrl, String categoria)
	{

		try
		{

			String string = null;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(xmlUrl);
			doc.getDocumentElement().normalize();
			Element root = doc.getDocumentElement();
			NodeList categ = root.getElementsByTagName("category");
			int lung = categ.getLength();

			for (int i = 0; i < lung; i++)
			{// per ogni categoria
				Element c = (Element) categ.item(i);// nodo
				String cat = c.getAttribute("name");
				if (cat.compareTo(categoria) == 0)
				{
					string = c.getAttribute("icon");
					return string;
				}
			}

			return string;

		} catch (SAXException e)
		{
			// eDebug(e.toString());
			return null;
		} catch (IOException e)
		{
			// eDebug(e.toString());
			return null;
		} catch (ParserConfigurationException e)
		{
			// eDebug(e.toString());
			return null;
		} catch (FactoryConfigurationError e)
		{
			// eDebug(e.toString());
			return null;
		}
	}

	public String[] RitornaFunctionCategoria(InputStream xmlUrl, String categoria)
	{
		try
		{
			String[] string = null;

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(xmlUrl);
			doc.getDocumentElement().normalize();
			Element root = doc.getDocumentElement();
			NodeList categ = root.getElementsByTagName("category");
			int lung = categ.getLength();

			for (int i = 0; i < lung; i++)
			{// per ogni categoria
				Element c = (Element) categ.item(i);// nodo
				String cat = c.getAttribute("name");
				if (cat.compareTo(categoria) == 0)
				{
					NodeList figli = c.getElementsByTagName("function");
					int l = figli.getLength();
					string = new String[l];
					for (int j = 0; j < l; j++)
					{
						Element f = (Element) figli.item(j);
						string[j] = f.getAttribute("name");
					}
					break;
				}
			}

			return string;

		} catch (SAXException e)
		{
			// eDebug(e.toString());
			return null;
		} catch (IOException e)
		{
			// eDebug(e.toString());
			return null;
		} catch (ParserConfigurationException e)
		{
			// eDebug(e.toString());
			return null;
		} catch (FactoryConfigurationError e)
		{
			// eDebug(e.toString());
			return null;
		}
	}

	public String[][] RitornaInputFunction(InputStream xmlUrl, String categoria, String function)
	{

		try
		{
			String[][] Matrix = null;

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(xmlUrl);
			doc.getDocumentElement().normalize();
			Element root = doc.getDocumentElement();
			NodeList categ = root.getElementsByTagName("category");
			int lung = categ.getLength();

			for (int i = 0; i < lung; i++)
			{// per ogni categoria
				Element c = (Element) categ.item(i);// nodo
				String cat = c.getAttribute("name");
				if (cat.compareTo(categoria) == 0)
				{
					NodeList figli = c.getElementsByTagName("function");
					int l = figli.getLength();

					for (int j = 0; j < l; j++)
					{
						Element f = (Element) figli.item(j); // funzione
						String nomeFunc = f.getAttribute("name");
						if (nomeFunc.compareTo(function) == 0)
						{
							NodeList inputs = f.getElementsByTagName("inputs");
							if (inputs.getLength() > 0)
							{
								Element inputs2 = (Element) inputs.item(0);
								NodeList input = inputs2.getElementsByTagName("input");// input
																						// veri
								int ll = input.getLength();
								Matrix = new String[ll][3];
								for (int k = 0; k < ll; k++)
								{
									Element input1 = (Element) input.item(k);
									Matrix[k][0] = input1.getAttribute("type");
									Node param = input1.getElementsByTagName("param").item(0);
									Matrix[k][1] = param.getTextContent();
									Node name = input1.getElementsByTagName("name").item(0);
									Matrix[k][2] = name.getTextContent();

								}
								break;
							}
						}
					}
					break;
				}
			}

			return Matrix;

		} catch (SAXException e)
		{
			// eDebug(e.toString());
			return null;
		} catch (IOException e)
		{
			// eDebug(e.toString());
			return null;
		} catch (ParserConfigurationException e)
		{
			// eDebug(e.toString());
			return null;
		} catch (FactoryConfigurationError e)
		{
			// eDebug(e.toString());
			return null;
		}
	}

	public String[][] RitornaOutputFunction(InputStream xmlUrl, String categoria, String function)
	{

		try
		{
			String[][] Matrix = null;

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(xmlUrl);
			doc.getDocumentElement().normalize();
			Element root = doc.getDocumentElement();
			NodeList categ = root.getElementsByTagName("category");
			int lung = categ.getLength();

			for (int i = 0; i < lung; i++)
			{// per ogni categoria
				Element c = (Element) categ.item(i);// nodo
				String cat = c.getAttribute("name");
				if (cat.compareTo(categoria) == 0)
				{
					NodeList figli = c.getElementsByTagName("function");
					int l = figli.getLength();

					for (int j = 0; j < l; j++)
					{
						Element f = (Element) figli.item(j); // funzione
						String nomeFunc = f.getAttribute("name");
						if (nomeFunc.compareTo(function) == 0)
						{
							NodeList inputs = f.getElementsByTagName("outputs");
							if (inputs.getLength() > 0)
							{
								Element inputs2 = (Element) inputs.item(0);
								NodeList input = inputs2.getElementsByTagName("output");// output
																						// veri
								int ll = input.getLength();
								Matrix = new String[ll][3];
								for (int k = 0; k < ll; k++)
								{
									Element input1 = (Element) input.item(k);
									Matrix[k][0] = input1.getAttribute("type");
									Node param = input1.getElementsByTagName("param").item(0);
									Matrix[k][1] = param.getTextContent();
									Node name = input1.getElementsByTagName("name").item(0);
									Matrix[k][2] = name.getTextContent();

								}
								break;
							}
						}
					}
					break;
				}
			}

			return Matrix;

		} catch (SAXException e)
		{
			// eDebug(e.toString());
			return null;
		} catch (IOException e)
		{
			// eDebug(e.toString());
			return null;
		} catch (ParserConfigurationException e)
		{
			// eDebug(e.toString());
			return null;
		} catch (FactoryConfigurationError e)
		{
			// eDebug(e.toString());
			return null;
		}
	}

	public String RitornaIconaFunction(InputStream xmlUrl, String function)
	{
		try
		{
			String string = null;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(xmlUrl);
			doc.getDocumentElement().normalize();
			Element root = doc.getDocumentElement();
			NodeList categ = root.getElementsByTagName("function");
			int lung = categ.getLength();

			for (int i = 0; i < lung; i++)
			{// per ogni function
				Element c = (Element) categ.item(i);// nodo
				String cat = c.getAttribute("name");
				if (cat.compareTo(function) == 0)
				{
					Element papa = (Element) c.getParentNode();
					string = papa.getAttribute("icon");
					return string;
				}
			}

			return string;

		} catch (SAXException e)
		{
			// eDebug(e.toString());
			return null;
		} catch (IOException e)
		{
			// eDebug(e.toString());
			return null;
		} catch (ParserConfigurationException e)
		{
			// eDebug(e.toString());
			return null;
		} catch (FactoryConfigurationError e)
		{
			// eDebug(e.toString());
			return null;
		}
	}

}
