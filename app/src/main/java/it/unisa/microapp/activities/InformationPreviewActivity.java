package it.unisa.microapp.activities;

import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import it.unisa.microapp.R;
import it.unisa.microapp.data.ComplexDataType;
import it.unisa.microapp.data.DataType;
import it.unisa.microapp.data.GenericData;
import it.unisa.microapp.utils.Utils;
import it.unisa.microapp.webservice.entry.MAEntry;
import it.unisa.microapp.webservice.view.GUICreator;

public class InformationPreviewActivity extends MAActivity {
	private ViewFlipper flipper;
	private TextView title;
	private Element element;
	private List<MAEntry<String, Object>> list;
	private float lastX;
	private Iterator<GenericData<?>> itData;

	@Override
	protected void initialize(Bundle savedInstanceState) {
		

	}

	@Override
	protected void prepare() {
		

	}

	@Override
	protected int onVisible() {
		return R.layout.sliderlayout;
	}

	@Override
	protected View onVisibleView() {
		
		return null;
	}

	@Override
	protected void prepareView(View v) {
		flipper = (ViewFlipper) this.findViewById(R.id.slider_flipp);
		title = (TextView) this.findViewById(R.id.slider_txt);

		flipper.removeAllViews();

		Button b = (Button) this.findViewById(R.id.slider_butt_next);
		b.setOnClickListener(new onClickL());

		b = (Button) this.findViewById(R.id.slider_butt_prev);
		b.setOnClickListener(new onClickL());
		
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		Document doc = null;		
		
		try {
			docBuilder = dbfac.newDocumentBuilder();
			doc = docBuilder.newDocument();
		} catch (ParserConfigurationException e) {
			Utils.error(e.getMessage(), e);
		} 		
		
		if(itData != null) {
			while (itData.hasNext()) {
				GenericData<?> input = itData.next();
	
				if (input instanceof ComplexDataType) {
					ComplexDataType comp = (ComplexDataType) input;
					Utils.verbose(comp.toString());
					element = deepCopy(doc, comp.getComplexElement());
					list = comp.getData();
					Utils.verbose(element.getAttribute("name"));
	
					flipper.addView(makeView());
				}
			}
			
			title.setText((String) flipper.getCurrentView().getTag());		
		}
	}	
	
	@Override
	protected void execute() {
		

	}	
	
	@Override
	public void initInputs() {
		itData = application.getData(mycomponent.getId(), DataType.OBJECT).iterator();
	}

	private View makeView() {
		ScrollView scroll = new ScrollView(this);
		GUICreator creator = new GUICreator(this);

		scroll.addView(creator.createGUIForResponse(element, list));
		scroll.setTag(element.getAttribute("name"));

		return scroll;
	}

	@Override
	public void beforeNext() {
		Iterable<GenericData<?>> it = application.getData(mycomponent.getId(), DataType.OBJECT);
		if (it != null)
			for (GenericData<?> d : it) {
				application.putData(mycomponent,d);
			}		
	}

	public boolean onTouchEvent(MotionEvent t) {
		switch (t.getAction()) {
		case MotionEvent.ACTION_DOWN:

			lastX = t.getX();
			break;
		case MotionEvent.ACTION_UP:

			float x = t.getX();

			if ((lastX - x) < -30) {
				flipper.setInAnimation(inFromLeft());
				flipper.setOutAnimation(outToRight());

				showNext();
			}

			if ((lastX - x) > 30) {
				flipper.setInAnimation(inFromRight());
				flipper.setOutAnimation(outToLeft());

				showPrev();
			}
			break;
		}

		return false;
	}

	private class onClickL implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.slider_butt_next) {
				flipper.setInAnimation(inFromRight());
				flipper.setOutAnimation(outToLeft());
				showNext();
			} else {
				flipper.setInAnimation(inFromLeft());
				flipper.setOutAnimation(outToRight());
				showPrev();
			}
		}
	}

	// TODO: da testare
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

	private void showPrev() {
		flipper.showPrevious();
		title.setText((String) flipper.getCurrentView().getTag());
	}

	private void showNext() {
		flipper.showNext();
		title.setText((String) flipper.getCurrentView().getTag());
	}

	private Animation outToLeft() {
		Animation anim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
				-1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);

		anim.setDuration(350);
		anim.setInterpolator(new AccelerateInterpolator());

		return anim;
	}

	private Animation inFromRight() {
		Animation anim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);

		anim.setDuration(350);
		anim.setInterpolator(new AccelerateInterpolator());

		return anim;
	}

	private Animation outToRight() {
		Animation anim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);

		anim.setDuration(350);
		anim.setInterpolator(new AccelerateInterpolator());

		return anim;
	}

	private Animation inFromLeft() {
		Animation anim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT,
				0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);

		anim.setDuration(350);
		anim.setInterpolator(new AccelerateInterpolator());

		return anim;
	}
	
	protected void resume(){
		 //metodi per speech Vincenzo Savarese
	}
}
