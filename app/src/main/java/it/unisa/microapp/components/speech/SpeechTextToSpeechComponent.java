package it.unisa.microapp.components.speech;

import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.webservice.piece.WSSettings;

public class SpeechTextToSpeechComponent extends MAComponent {
	public SpeechTextToSpeechComponent(String id, String description) {
		super(id, description);

	}

	@Override
	protected String getLocationQName() {
		return "it.unisa.microapp.activities.TextToSpeechActivity";
	}

	@Override
	protected String getCompType(String id) {
		return "SPEECH_TEXTTOSPEECH";
	}

	@Override
	public WSSettings getSettings() {
		return null;
	}

	@Override
	protected void setSettings(WSSettings settings) {
	}

	@Override
	protected void updateSettings(boolean settingUpdate) {
	}

	@Override
	public boolean isUpdate() {
		return false;
	}

}
