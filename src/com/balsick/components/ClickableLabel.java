package com.balsick.components;

import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

import com.balsick.datastructures.ComponentColors;

public class ClickableLabel extends JLabel implements MouseListener {
	
	private static final long serialVersionUID = 7744598505876513171L;
	Runnable action;
	private int status = 0;
	private static final int STATUS_NORMAL = 0;
	private static final int STATUS_PRESSED = 1;
	private static final int STATUS_HOVER = 2;
	private Color defBackground = Color.white;
	private Color defForeground = Color.black;
	
	public ClickableLabel(String text) {
		this(text, null, null, null);
	}
	
	public ClickableLabel(String text, Color foreground, Color background, Runnable action){
		super(text);
		addMouseListener(this);
		if (foreground != null)
			defForeground = foreground;
		if (background != null)
			defBackground = background;
		setAction(action);
		setForeground(defForeground);
		setBackground(defBackground);
		setOpaque(true);
		setHorizontalAlignment(JLabel.CENTER);
	}
		
	
	public void setAction(Runnable action) {
		this.action = action;
	}
	
	public void updateColors() {
		ComponentColors map = getColors();
		if (getForeground().equals(map.foreground) && getBackground().equals(map.background))
			return;
		setForeground(map.foreground);
		setBackground(map.background);
		repaint();
	}
	
	public ComponentColors getColors() {
		Color fg = null, bg = null;
		switch (status) {
			case STATUS_NORMAL:
				bg = defBackground;
				break;
			case STATUS_PRESSED:
				bg = defBackground.darker();
				break;
			case STATUS_HOVER:
				bg = defBackground.brighter();
		}
		return new ComponentColors(fg, bg);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (action != null)
			action.run();
		updateColors();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		status = STATUS_HOVER;
		updateColors();
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		status = STATUS_NORMAL;
		updateColors();
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		status = STATUS_PRESSED;
		updateColors();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		status = STATUS_NORMAL;
		if (this.contains(MouseInfo.getPointerInfo().getLocation()))
			status = STATUS_HOVER;
		updateColors();
	}
	
	
}
