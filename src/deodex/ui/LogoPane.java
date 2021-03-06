/*
 *  Lordroid One Deodexer To Rule Them All
 * 
 *  Copyright 2016 Rachid Boudjelida <rachidboudjelida@gmail.com>
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package deodex.ui;

import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import deodex.tools.Logger;

public class LogoPane extends JPanel {

	public static final int M_WIDTH = 800;
	public static final int M_HEIGHT = 100;
	public static final String LOG_HEADER = "[LogoPane]";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Image image;

	public LogoPane() {
		this.setSize(M_WIDTH, M_HEIGHT);
		try {
			this.image = ImageIO
					.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("images/logo.png"));
			Logger.logToStdIO(LOG_HEADER + Logger.INFO + " File images/logo.png Loaded");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(image, 0, 0, this);

	}

}
