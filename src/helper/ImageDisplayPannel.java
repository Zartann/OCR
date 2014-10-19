package helper;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;


@SuppressWarnings("serial")
public class ImageDisplayPannel extends JPanel {
	
	private BufferedImage image = new BufferedImage(28, 28, BufferedImage.TYPE_BYTE_GRAY);
	
	public ImageDisplayPannel(BufferedImage img){
		image = img;
	}

	public void paintComponent(Graphics g){
		g.setColor(Color.WHITE);
		g.clearRect(0, 0, getWidth(), getHeight());
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.drawImage(image, 0, 0, getWidth(), getHeight(),
				null);
	}

	public void changeImage(BufferedImage img){
		image = img;
		repaint();
	}
	
}
