package it.unisa.microapp.editor;

import it.unisa.microapp.utils.Constants;

import java.io.Serializable;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class IconPiece extends Piece implements Serializable {
	private static final long serialVersionUID = -6343520742944884346L;

	public IconPiece(String idlogic, Resources act, String text, int x, int y, Grid grid) {
		super(idlogic, act, text, x, y, grid);
	}

	public int RitornaPezzo(Shape sopra, int Xa) {
		int b = 0;
		int partenza = (int) ((IconPiece) sopra).getLogicPosition().x;
		for (int i = Xa - 1; i > partenza; i--) {
			if (((IconPiece) sopra).isHoled(i))
				b++;
		}

		return (Xa - partenza - b) + 1;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
		if (selected) {
			for (Pin pin : in) {
				pin.onPinClick();
			}
			for (Pin pin : out) {
				pin.onPinClick();
			}
		}
	}

	@Override
	public boolean hasUpperInputPin() {
		return false;
	}

	@Override
	public void draw(Canvas panel, Paint paint, boolean iconified, int col, int row) {
		int oldColor = paint.getColor();
		Style oldStyle = paint.getStyle();
		int oldAlpha = paint.getAlpha();
		int nWidth = width;
		int nHeight = height;

		float nx = x + blockSpacediv2;

		paint.setColor(Color.BLACK);
		paint.setStyle(Style.STROKE);
		
		int heightblockSpacerow = (height + Constants.SPACE) * row;
		int widthblockSpacecol = (width + Constants.SPACE) * col;		
		
		// altri bordorettangoli
		panel.drawRoundRect(new RectF(nx - (widthblockSpacecol), y - (heightblockSpacerow), nx
					- (widthblockSpacecol) + nWidth, y - (heightblockSpacerow) + nHeight), radiusdiv2,
					radiusdiv2, paint);

		paint.setStyle(Style.FILL);

		if (selected) {
			paint.setColor(Color.GREEN);// colore pezzo selezionato
		} else
			paint.setColor(Color.rgb(210, 210, 210));// colore pezzo non
														// selezionato

			// rimpimento altri rettangoli
			panel.drawRoundRect(new RectF(nx - (widthblockSpacecol), y - (heightblockSpacerow), nx
					- (widthblockSpacecol) + nWidth, y - (heightblockSpacerow) + nHeight), radiusdiv2,
					radiusdiv2, paint);

		paint.setColor(Color.MAGENTA);
		paint.setAlpha(25);
		int widthdiv2 = width >> 1;

		// riempimento della meta' del pezzo con il colore specificato sopra
		panel.drawRoundRect(new RectF(nx - (widthblockSpacecol) + nWidth - widthdiv2, y
				- (heightblockSpacerow) + 3, nx - (widthblockSpacecol) + nWidth - 3, y
				- (heightblockSpacerow) + nHeight - 3), radiusdiv2, radiusdiv2, paint);

		paint.setAlpha(oldAlpha);

		if (showIcon && image != null)// disegna icone sopra pezzi
		{
			setIconSampleSize(this.iconId, (Constants.GRID_WSIZE >> 1) - 1);
			
			int iw = (image.getWidth() >> 1);
			if (selected) {
				panel.drawBitmap(image, nx - (widthblockSpacecol) + widthdiv2 - iw, y
						- (heightblockSpacerow) + 4, paint80);
			} else
				panel.drawBitmap(image, nx - (widthblockSpacecol) + widthdiv2 - iw, y
						- (heightblockSpacerow) + 4, null);
		}

		if (!text.equals(""))// scrive nome sull'icona
		{
			paint.setColor(Color.BLACK);
			paint.setTextSize(Constants.TEXT_SIZE);
			paint.setTextAlign(Align.CENTER);

			int toff = -(multiText.length - 1) * Constants.TEXT_SIZE;
			for (String s : multiText) {
				panel.drawText(s, nx - (widthblockSpacecol) + widthdiv2, y - (heightblockSpacerow)
						+ nHeight - radiusmult2 + toff, paint);
				toff = toff + Constants.TEXT_SIZE;
			}
		}

			// input pins
			int ilen = in.size();
			if (ilen > 0) {
				int hasMulti = 0;
				int hasUser = 0;
				int multiColor = 0;
				for (Pin pin : in) {
					if (pin.getType() == PinEnumerator.MULTI) {
						multiColor = pin.getColor();
						hasMulti++;
					}
					if (pin.getType() == PinEnumerator.USER) {
						hasUser++;
					}

				}

				int Uoffset = 0;
				int innerCount = 1;
				int step = width / (ilen - hasMulti + 2 - hasUser);
				if (hasMulti == 0) {
					step = width / (ilen - hasUser + 1);
				}
				int NMoffset = 0;

				for (int i = 0; i < ilen; i++) {
					NMoffset = innerCount * step;
					Pin pin = in.get(i);
					switch (pin.getType()) {
					case MULTI:
						break;
					case NORMAL: {
						innerCount++;
						paint.setColor(pin.getColor());
						paint.setStyle(Style.FILL_AND_STROKE);
						panel.drawCircle(nx - (widthblockSpacecol) + NMoffset, y
								- (heightblockSpacerow) - radiusdiv2, radius, paint);
						paint.setTextAlign(Align.CENTER);
						break;
					}
					case USER: { // triangle
						paint.setColor(pin.getColor());
						paint.setStyle(Style.FILL_AND_STROKE);
						Path path = new Path();
						int b4 = Constants.SPACE >> 2;
						path.moveTo(nx - (widthblockSpacecol) - b4, y - (heightblockSpacerow) + b4
								+ Uoffset);
						path.lineTo(nx - (widthblockSpacecol) - b4, y - (heightblockSpacerow) + b4
								+ radiusmult2 + Uoffset);
						path.lineTo(nx - (widthblockSpacecol) + b4 + radiusmult2 / 3, y
								- (heightblockSpacerow) + b4 + radius + Uoffset);
						path.close();
						panel.drawPath(path, paint);

						if (pin.isEmpty()) {
							paint.setStyle(Style.STROKE);
							path = new Path();
							path.moveTo(nx - (widthblockSpacecol) - b4 - 2, y - (heightblockSpacerow)
									+ b4 + Uoffset - 4);
							path.lineTo(nx - (widthblockSpacecol) - b4 - 2, y - (heightblockSpacerow)
									+ b4 + radius * 2 + Uoffset + 4);
							path.lineTo(nx - (widthblockSpacecol) + b4 + 4 + ((radius >> 1) / 3), y
									- (heightblockSpacerow) + b4 + radius + Uoffset);
							path.close();
							panel.drawPath(path, paint);
						}

						Uoffset += radius + blockSpacediv2 + 4;
						break;
					}
					}
				}
				if (hasMulti > 0) {
					NMoffset = innerCount * step;
					paint.setColor(multiColor);
					paint.setStyle(Style.STROKE);
					panel.drawCircle(nx - (widthblockSpacecol) + NMoffset, y - (heightblockSpacerow)
							- radiusdiv2, radius, paint);
					paint.setStyle(Style.FILL_AND_STROKE);
					panel.drawCircle(nx - (widthblockSpacecol) + NMoffset, y - (heightblockSpacerow)
							- (radius >> 2) - 1, radiusdiv2, paint);

				}
			}


		// output pins
			// output pins
			int olen = out.size();
			int step = width / (olen + 1);
			for (int i = 0; i < olen; i++) {
				int Noffset = (i + 1) * step;
				Pin pin = out.get(i);
				switch (pin.getType()) {
				case USER:
					break;
				case NORMAL: {
					paint.setColor(pin.getColor());
					paint.setStyle(Style.FILL_AND_STROKE);
					panel.drawCircle(nx - (widthblockSpacecol) + Noffset, y - (heightblockSpacerow)
							+ nHeight - radiusdiv2 + 2, radius, paint);
					break;
				}
				case MULTI: {
					paint.setColor(pin.getColor());
					paint.setStyle(Style.FILL_AND_STROKE); //TODO: STROKE
					panel.drawCircle(nx - (widthblockSpacecol) + Noffset, y - (heightblockSpacerow)
							+ nHeight - radiusdiv2 + 2, radius, paint);
					paint.setStyle(Style.FILL_AND_STROKE);
					panel.drawCircle(nx - (widthblockSpacecol) + Noffset, y - (heightblockSpacerow)
							+ nHeight - (radius >> 2) + 1, radius / 2, paint);
					break;
				}
				}

			}

		paint.setColor(oldColor);
		paint.setStyle(oldStyle);
		paint.setAlpha(oldAlpha);
	}

}
