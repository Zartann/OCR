package helper;

import java.awt.image.BufferedImage;

import javax.swing.JFrame;


@SuppressWarnings("serial")
public class ImageDisplayFrame extends JFrame{

	private ImageDisplayPannel pan;
	private final static int defaultWidth = 280, defaultHeight = 280;

	public ImageDisplayFrame(BufferedImage img, String title){
		this.setTitle( title );
		this.setSize( defaultWidth, defaultHeight );
		this.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
		this.setLocationRelativeTo( null );
		pan = new ImageDisplayPannel( img );
		this.setContentPane( pan );
		this.setVisible( true );
	}

	public void changeImage(BufferedImage img){
		pan.changeImage( img );
		repaint();
	}

}
