package it.unisa.microapp.webservice;
import it.unisa.microapp.utils.Utils;

import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class TypesUtils 
{
	
	private static final String element="element";
	//private static final String complexType="complexType";
	//private static final String simpleType="simpleType";
	private static final String restriction="restriction";
	private static final String extension="extension";
	private static final String list="list";
	private static final String union="union";
	//private static final String simpleContent="simpleContent";
	//private static final String complexContent="complexContent";
	private static final String group="group";
	//private static final String all="all";
	private static final String choice="choice";
	//private static final String sequence="sequence";
	//private static final String attribute="attribute";
	private static final String enumeration="enumeration";
	
	private static TypeParser2 types;
	
	public TypesUtils(TypeParser2 t)
	{
		types=t;
	}
	
	
	
	public  Node Element2Tree(String element)throws NoElementException
	{
		return Element2Tree((Element)types.getTypeMap().get(element));
	}
	
	public  Node Element2Tree(Element e)throws NoElementException
	{
		Element root=null;
		try
		{
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            
            //creo nodo sentinella
            root=doc.createElement("param");
            
            if(e != null)
    		{
            	Utils.verbose("mapping type "+e.getAttribute("name")+" to tree");
            	preorderTraversal(e,doc,root);
    		}
            else
            	throw new NoElementException("elemento non trovato "+e);

		}catch(Exception ex)
		{
			Utils.error(""+ex.getMessage(), ex);
		}
		
		root.setAttribute("name",e.getAttribute("name"));
		
		return root;
	}

	private  void preorderTraversal(Element e, Document doc, Element root) 
	{
		
		int tipo=controllaTipo(e);
		
		String id="";
		String name=e.getAttribute("name");
		String type="";
		String base="";
		String min="";
		String max="";
		Element nodo = null;
		
		//ricavo gli attributi dell'elemento
		switch(tipo)
		{
		case 0://element
			id=e.getAttribute("ref");
			type=e.getAttribute("type");
			min=e.getAttribute("minOccurs");
			max=e.getAttribute("maxOccurs");
			
			if(!id.isEmpty())
			{
				if(types.getIdNodeMap().get(id) != null)
				{	
					if((GestisciIdType(doc,root,id,0)) < 0)
						return;
					Element elem=cercaElementconNome(root,id);
					
					if(elem != null)
					{
						elem.setAttribute("name", name);
						elem.setAttribute("min", min);
						elem.setAttribute("max", max);
					}
					name="";
				}
			}
			
			if(!type.isEmpty())
			{
				int ind=type.indexOf(":");
				
				//controllo se il tipo non e built-in
				if(types.getTypeMap().get(type.substring(ind+1)) != null)
				{
					if((GestisciIdType(doc,root,type.substring(ind+1),1)) < 0)
						return;
					Utils.debug(root.toString());
					
					Element elem=cercaElementconNome(root,type.substring(ind+1));
					
					if(elem != null)
					{
						elem.setAttribute("name", name);
						elem.setAttribute("min", min);
						elem.setAttribute("max", max);
					}
					
					name="";
				}
				
			}
			break;
		case 1://restriction
			base=e.getAttribute("base");
			String t;
			
			
			if(isPrimitive(base.substring(base.indexOf(":")+1)))
				root.setAttribute("type", base.substring(base.indexOf(":")+1));
			else
			{
				nodo=doc.createElement("element");
				nodo.setAttribute("name", base.substring(base.indexOf(":")+1));
				if((GestisciIdType(doc,nodo,base.substring(base.indexOf(":")+1),1)) < 0)
					return;
				
				Element p=(Element) root.getParentNode();
				p.removeChild(root);
				Node n=p.appendChild(nodo.getFirstChild());
				root=(Element) nodo.getFirstChild();
				//nodo.setAttribute("name", ((Element)e.getParentNode()).getAttribute("name"));
				Utils.debug(((Element)e.getParentNode()).getAttribute("name"));
				((Element)n).setAttribute("name", ((Element)e.getParentNode()).getAttribute("name"));
				((Element)n).setAttribute("min", ((Element)e.getParentNode()).getAttribute("min"));
				((Element)n).setAttribute("max", ((Element)e.getParentNode()).getAttribute("max"));
				name="";
			}
			
			break;
		case 2://extension
			type=e.getAttribute("base");
			
			if(!type.isEmpty())
			{
				int ind=type.indexOf(":");
				//controllo se il tipo non e built-in
				if(types.getTypeMap().get(type.substring(ind+1)) != null)
				{
					nodo=doc.createElement("element");
					if((GestisciIdType(doc,nodo,type.substring(ind+1),1)) < 0)
						return;
					//nodo=(Element) root.getChildNodes().item(0);
					
					//unisco i figli di nodo con root
					unisciFigli(root,(Element) nodo.getFirstChild(),doc);
					
					name="";
				}
				else if(isPrimitive(type.substring(ind+1)))
					root.setAttribute("type", type.substring(ind+1));
			}
			
				
			break;
		case 3://group
			type=e.getAttribute("ref");
			if(!type.isEmpty())
			{
				//controllo se il tipo non e built-in
				if(types.getTypeMap().get(type) != null)
					{
						nodo=doc.createElement("element");
						if((GestisciIdType(doc,nodo,type,1)) < 0)
							return;
						/*
						nodo=(Element) root.getChildNodes().item(0);
						nodo.setAttribute("name", name);
						*/
						
						//unisco i figli di nodo con root
						unisciFigli(root,(Element) nodo.getFirstChild(),doc);
						
						name="";
					}
			}
			break;
			
		case 4://choice
			root.setAttribute("layout", "choice");
			break;
		
		case 6://attribute
			//caso in cui elemento e un'array gestisco elemento attribute
			//TODO: considera solo il caso di un vettore soap Array non di una matrice
			type=e.getAttribute("wsdl:arrayType");
			if(!type.isEmpty())
			{
				int i=type.indexOf(":");
				int j=type.indexOf("[");
				
				//caso matrice della forma tipo[][]
				if(type.lastIndexOf("[") != j)
				{
					root.setAttribute("layout", "matrix");
				}
				else
					root.setAttribute("layout", "array");
				
				String val=type.substring(i+1, j);
					
				type=val;
					
				if(types.getTypeMap().get(type) != null)
				{
					doc.createElement("element");
						if((GestisciIdType(doc,root,type,1)) < 0)
							return;
				}
				
				
					root.removeAttribute("type");
				
					name="";
				
			}
			
			
			
			
			break;
		case 7://enumeration
			name=e.getAttribute("value");
			type="spinnerElement";
			root.setAttribute("layout", "spinner");
			root.removeAttribute("type");
			break;
		case 8://list
			type=e.getAttribute("itemType");
			base="List";
			/*
			name=base;
			t="";
			nodo=makeNode(doc,name,t,min,max);
			root.appendChild(nodo);
			name="";
			root=nodo;
			*/
			root.setAttribute("layout", "list");
			//root.removeAttribute("type");
			if(!type.isEmpty())
			{
				int ind=type.indexOf(":");
				//controllo se il tipo non e built-in
				if(types.getTypeMap().get(type.substring(ind+1)) != null)
				{
					if((GestisciIdType(doc,root,type.substring(ind+1),1)) < 0)
						return;
					
					Element elem=cercaElementconNome(root,type.substring(ind+1));
					
					if(elem != null)
					{
						elem.setAttribute("name", name);
						elem.setAttribute("min", min);
						elem.setAttribute("max", max);
					}
					
					name="";
				}
				else
				{
					t=type.substring(ind+1);
					nodo=makeNode(doc,"item",t,min,max);
					root.appendChild(nodo);
				}
				
			}
			
			break;
		case 9://union ************ da aggiustare *************
			type=e.getAttribute("memberTypes");
			StringTokenizer token=new StringTokenizer(type);
			
			while(token.hasMoreTokens())
			{
				String s=token.nextToken();
				int ind=s.indexOf(":");
				
				if(types.getTypeMap().get(s.substring(ind+1)) != null)
				{
					if((GestisciIdType(doc,root,type.substring(ind+1),1)) < 0)
						return;
					
					Element elem=cercaElementconNome(root,type.substring(ind+1));
					
					if(elem != null)
					{
						elem.setAttribute("name", name);
						elem.setAttribute("min", min);
						elem.setAttribute("max", max);
					}
					
					name="";
				}
				else
				{
					t=s.substring(ind+1);
					nodo=makeNode(doc,"item",t,min,max);
					root.appendChild(nodo);
				}
			}
			
		}
		
		//se il nome non e vuoto allora creo un nuovo nodo
		if(!name.isEmpty())
		{
			// creo un nuovo nodo
			nodo=makeNode(doc,name,type,min,max);
			
			//aggiungo nodo a padre
			root.appendChild(nodo);
			
			root=nodo;
		}
		
		//controllo se e nodo interno
		if(e.hasChildNodes())
		{
			NodeList ch=e.getChildNodes();
			
			//visito i suoi figli
			for(int i=0;i<ch.getLength();i++)
			{
				if(ch.item(i) instanceof Element)
				{
					Element c=(Element)ch.item(i);
					preorderTraversal(c,doc,root);
				}
			}
		}
		
		return;
		
	}

	private  Element cercaElementconNome(Element root, String name) 
	{
		NodeList list=root.getChildNodes();
		
		for(int i=0;i<list.getLength();i++)
		{
			Element e=(Element)list.item(i);
			
			if(e.getAttribute("name").equals(name))
				return e;
		}
		return null;
	}



	private  void unisciFigli(Element root, Element nodo, Document doc) 
	{
		//TODO:gestire eliminazione nodo e aggiunta figli
		NodeList ch=nodo.getChildNodes();
		for(int i=0;i<ch.getLength();i++)
		{
			Utils.debug(((Element)ch.item(i)).getAttribute("name"));
			//Node n=ch.item(i).cloneNode(true);
			Node n=deepCopy(doc,(Element) ch.item(i));
			root.appendChild(n);
		}
		
		//root.removeChild(nodo);
	}
	
	private Element deepCopy(Document doc, Element e) {
		Element elem = doc.createElement("element");

		if (e.hasAttributes()) {
			NamedNodeMap attr = e.getAttributes();

			for (int i = 0; i < attr.getLength(); i++) {
				Attr a = (Attr) attr.item(i);
				elem.setAttribute(a.getName(), a.getNodeValue());
			}
		}

		if (e.hasChildNodes()) {
			NodeList list = e.getChildNodes();

			for (int i = 0; i < list.getLength(); i++) {
				if (list.item(i) instanceof Element) {
					Element ee = (Element) list.item(i);

					elem.appendChild(deepCopy(doc, ee));
				} else {
					Text txt = (Text) list.item(i);
					elem.setTextContent(txt.getTextContent());
				}
			}
		}

		return elem;
	}

	private  boolean isPrimitive(String base) 
	{
		if(types.getTypeMap().get(base) == null)
			return true;
		else
			return false;
	}

	private  int GestisciIdType(Document doc, Element root, String id,int type) 
	{
		//controlla se c'e il richio di una ricorsione infinita
		//qualora un nodo fa riferimento ad un suo predecessore
		if(isInfRec(root,id))
			return -1;
		
		if(type == 0)
			preorderTraversal((Element) types.getIdNodeMap().get(id), doc, root);
		else
			preorderTraversal((Element) types.getTypeMap().get(id), doc, root);
		
		return 0;
	}
	
	private  Element makeNode(Document doc,String name, String type, String min, String max) 
	{
		Element nodo;
		nodo=doc.createElement("element");
		nodo.setAttribute("name", name);
		//nodo.setAttribute("weight", "0.0");
		nodo.setAttribute("type", type);
		
		if(!min.isEmpty())
			nodo.setAttribute("min", min);
		if(!max.isEmpty())
			nodo.setAttribute("max", max);
		
		
		//nodo.setAttribute("id", name);
		
		return nodo;
	}

	private static boolean isInfRec(Element root, String type) 
	{
		if(root.getParentNode() == null)
			return false;
		
		Element n=root;
		Element parent=null;
		
		
		while((parent=(Element) n.getParentNode()) != null)
		{
			String name=parent.getAttribute("name");
			
			if(name.equals(type))
				return true;
			
			n=parent;
		}
		
		return false;
	}

	private static int controllaTipo(Element e) 
	{
		String name=e.getNodeName();
		
		if(name.contains(element))
		{
			return 0;
		}
		/*
		if(name.contains(complexType))
		{
			return 4;
		}
		/*
		if(name.contains(simpleType))
		{
			return 5;
		}
		*/
		if(name.contains(restriction))
		{
			return 1;
		}
		if(name.contains(extension))
		{
			return 2;
		}
		if(name.contains(list))
		{
			return 8;
		}
		
		if(name.contains(union))
		{
			return 9;
		}
		/*
		if(name.contains(simpleContent))
		{
			return -1;
		}
		if(name.contains(complexContent))
		{
			return -1;
		}
		*/
		if(name.contains(group))
		{
			return 3;
		}
		/*
		if(name.contains(all))
		{
			return -1;
		}
		*/
		if(name.contains(choice))
		{
			return 4;
		}
		/*
		if(name.contains(sequence))
		{
			return -1;
		}
		*/
		if(name.contains("attribute"))
		{
			return 6;
		}
		if(name.contains(enumeration))
		{
			return 7;
		}
		return -1;
	}
}
