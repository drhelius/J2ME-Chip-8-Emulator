/*
   Chip8 Emulator for J2ME by Nacho Sánchez. Visit http://www.geardome.com

   This program is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License
   as published by the Free Software Foundation; either version 2
   of the License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

   See gpl.txt for more information regarding the GNU General Public License.
*/

/*
 * MainCanvas.java
 *
 * Creado el 20 de febrero de 2006, 12:46
 *
 * Autor: Nacho Sánchez
 *
 */

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import java.io.*;
import javax.microedition.midlet.MIDlet;

public class MainCanvas extends GameCanvas implements Runnable
{  
    public static Graphics GFX;
    private static BitmapFont m_theWhiteFont;
    
    public static boolean m_bShouldPause;
    public static boolean m_bShouldStop;
    
    public static int m_iState;
    
    private static long m_lStartTime;
    private static long m_lEndTime;
    
    private static Image m_imgPortada;
    
    public static int m_iScrWd;
    public static int m_iScrHg;
    
    private static Image m_imgSelector;
    
    private static int m_iIndex;
    private static int m_iSelIndex;
  
    private final static String[] ROM_NAMES = {"15PUZZLE", "ALIEN", "ANT", "BLINKY", "BLINKY2", "BLITZ", "BREAKOUT",
    "BRIX", "CAR", "CONNECT4", "DRAGON1", "DRAGON2", "FIELD", "GUESS", "HIDDEN", "INVADERS", "JOUST23", "KALEID", "MAZE",
    "MAZE2", "MERLIN", "MINES", "MISSILE", "PIPER", "PONG", "PONG2", "PUZZLE", "RACE", "SPACEFIG", "SQUARE", "SQUASH",
    "SYZYGY", "TANK", "TETRIS", "TICTAC", "UBOAT", "UFO", "VBRIX", "VERS", "WIPEOFF", "WORM3"};

    
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    
    public MainCanvas()
    {
	super(false);	
    }
    
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////    

    public final void start()
    {
	(new Thread(this)).start();
    }
    
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////    
    
    private final void init()
    {
	this.setFullScreenMode(true);
	
	GFX = getGraphics();
	
	String[] strArray = {"logo.png", "font.png", "selector.png"};
	Image[] imageArray = PacketReader.loadImages(strArray, "/d", this.getClass());
	strArray = null;
	
	m_imgPortada = imageArray[0];	
	m_theWhiteFont = new BitmapFont(imageArray[1], 9, 9);
	
	m_imgSelector = imageArray[2];
		
	imageArray = null;
	
	Interpreter.ROM = new byte[4336];
	Interpreter.m_theCanvas = this;
	Interpreter.init();
	
	m_iScrWd = getWidth();
	m_iScrHg = getHeight();
	
	m_lStartTime = System.currentTimeMillis();	
    }
    
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    
    public final void sizeChanged(int w, int h)
    {
	m_iScrWd = w;
	m_iScrHg = h;	
    }
    
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    
    public final void run()
    {
	init();
	
	while (true)
	{    
	    if (m_bShouldStop)
	    {
		break;
	    }
	    
	    if (!m_bShouldPause)
	    {	
		switch (m_iState)
		{
		    case 0: /// portada
		    {			
			updatePortada();
			
			break;
		    }
		    case 1: /// menú
		    {
			updateMenu();
			
			break;
		    }
		    case 2: /// intérprete
		    {			
			GFX.setColor(0xffCDCDCD);
			GFX.fillRect(0, 0, MainCanvas.m_iScrWd, MainCanvas.m_iScrHg);
			
			flushGraphics();
			
			GFX.translate((MainCanvas.m_iScrWd>>1)-64, (MainCanvas.m_iScrHg>>1)-32);			
			
			GFX.setColor(Interpreter.m_iBackColor);			
			GFX.fillRect(0, 0, 128, 64);		
		
			flushGraphics();
			
			Thread.yield();	
			
			try
			{						
			    while (m_iState == 2)
			    {    			
				if (!m_bShouldPause)
				{
				    Interpreter.update();
				}

				Thread.yield();					
			    }
			}
			catch (Exception e)
			{
			    System.out.println(e.toString());
			    e.printStackTrace();
			    Main.m_TheMidlet.notifyDestroyed();
			}
			
			GFX.translate(-((MainCanvas.m_iScrWd>>1)-64), -((MainCanvas.m_iScrHg>>1)-32));
		    }
		}
		
		Thread.yield();
	    }	   
	}	    
    }
    
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    
    private final void updatePortada()
    {
	GFX.setClip(0, 0, m_iScrWd, m_iScrHg);
	GFX.setColor(0xffCDCDCD);
	GFX.fillRect(0, 0, m_iScrWd, m_iScrHg);
	
	GFX.drawImage(m_imgPortada, (m_iScrWd>>1) - 64, (m_iScrHg>>1) - 64, Graphics.TOP | Graphics.LEFT);
	
	if ((System.currentTimeMillis() - m_lStartTime) > 2500)
	{
	    m_iState++;
	    m_imgPortada = null;	    
	}

	flushGraphics();
    }
    
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    
    private final void updateMenu()
    {
	GFX.setClip(0, 0, m_iScrWd, m_iScrHg);
	GFX.setColor(0xffCDCDCD);
	GFX.fillRect(0, 0, m_iScrWd, m_iScrHg);
	
	m_theWhiteFont.drawString((m_iScrWd>>1) - 20, (m_iScrHg>>1) - 64, "ROMS:");
	
	for (int i=0; i<8; i++)
	{
	    m_theWhiteFont.drawString((m_iScrWd>>1) - 40, (m_iScrHg>>1) - 44 + (i * 10), ROM_NAMES[m_iIndex + i]);
	}
	
	GFX.drawImage(m_imgSelector, (m_iScrWd>>1) - 56, (m_iScrHg>>1) - 44 + (m_iSelIndex * 10), Graphics.TOP | Graphics.LEFT);
	
	flushGraphics();
    }    
  
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    
    public final void keyPressed(int keyCode)
    {
        int action = getGameAction(keyCode);

	if (m_iState == 1)
	{
	    switch (action)
	    {
		case UP:
		{
		    m_iSelIndex--;
		    if (m_iSelIndex<0)
		    {
			m_iSelIndex=0;
			
			m_iIndex--;
			if (m_iIndex<0)
			{
			    m_iIndex=0;			    
			}
		    }
		    break;
		}

		case DOWN:
		{
		    m_iSelIndex++;
		    
		    if (m_iSelIndex > 7)
		    {
			m_iSelIndex = 7;
			
			m_iIndex++;
			
			if (m_iIndex > (ROM_NAMES.length-8))
			{
			    m_iIndex = (ROM_NAMES.length-8);			    
			}
		    }
		    
		    break;
		}

		case FIRE:
		{
		    String[] strArray = {ROM_NAMES[m_iIndex + m_iSelIndex]};
		    byte[][] result = PacketReader.loadFiles(strArray, "/d", this.getClass());		    
		    
		    if (result[0].length != 0)
		    {
			Interpreter.reset(false);
			
			for (int i=0; i<result[0].length; i++)
			{
			    Interpreter.ROM[i + 512] = result[0][i];		    
			}		
			
			m_iState++;
		    }
		    else
		    {
			if (Interpreter.DEBUG)
			    System.out.println("Rom not found: " + ROM_NAMES[m_iIndex + m_iSelIndex]);    		
		    }
		    
		    strArray = null;
		    result = null;
		    
		    

		    break;
		}
	    } 
	    
	}
	else if (m_iState == 2)
	{
	    switch (keyCode)
	    {				
		case KEY_NUM0:
		{
		    Interpreter.KEYSTATUS[0]=1;
		    return;
		}
		
		case KEY_NUM1:
		{	
		    Interpreter.KEYSTATUS[1]=1;
		    return;
		}
		
		case KEY_NUM2:
		{	
		    Interpreter.KEYSTATUS[2]=1;
		    return;
		}
		
		case KEY_NUM3:
		{	
		    Interpreter.KEYSTATUS[3]=1;
		    return;
		}
		
		case KEY_NUM4:
		{	
		    Interpreter.KEYSTATUS[4]=1;
		    return;
		}
		
		case KEY_NUM5:
		{	
		    Interpreter.KEYSTATUS[5]=1;
		    return;
		}
		
		case KEY_NUM6:
		{
		    Interpreter.KEYSTATUS[6]=1;
		    return;
		}
		
		case KEY_NUM7:
		{
		    Interpreter.KEYSTATUS[7]=1;
		    return;
		}
		
		case KEY_NUM8:
		{
		    Interpreter.KEYSTATUS[8]=1;
		    return;
		}
		
		case KEY_NUM9:
		{
		    Interpreter.KEYSTATUS[9]=1;
		    return;
		}
		
		case KEY_STAR:
		{	
		    Interpreter.KEYSTATUS[0xA]=1;
		    return;
		}
		
		case KEY_POUND:
		{	
		    Interpreter.KEYSTATUS[0xB]=1;
		    return;
		}
	    }
	    
	    switch (action)
	    {		
		case UP:
		{
		    Interpreter.KEYSTATUS[0xC]=1;
		    return;
		}

		case DOWN:
		{	
		    Interpreter.KEYSTATUS[0xD]=1;
		    return;
		}
		
		case LEFT:
		{
		    Interpreter.KEYSTATUS[0xE]=1;
		    return;
		}
		
		case RIGHT:
		{	
		    Interpreter.KEYSTATUS[0xF]=1;
		    return;
		}		
	    } 	
	}
    }  
    
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    
    public final void keyReleased(int keyCode)
    {
	if (m_iState == 2)
	{
	    int action = getGameAction(keyCode);
	    
	    switch (keyCode)
	    {				
		case KEY_NUM0:
		{
		    Interpreter.KEYSTATUS[0]=0;
		    break;
		}
		
		case KEY_NUM1:
		{	
		    Interpreter.KEYSTATUS[1]=0;
		    break;
		}
		
		case KEY_NUM2:
		{	
		    Interpreter.KEYSTATUS[2]=0;
		    break;
		}
		
		case KEY_NUM3:
		{	
		    Interpreter.KEYSTATUS[3]=0;
		    break;
		}
		
		case KEY_NUM4:
		{	
		    Interpreter.KEYSTATUS[4]=0;
		    break;
		}
		
		case KEY_NUM5:
		{	
		    Interpreter.KEYSTATUS[5]=0;
		    break;
		}
		
		case KEY_NUM6:
		{
		    Interpreter.KEYSTATUS[6]=0;
		    break;
		}
		
		case KEY_NUM7:
		{
		    Interpreter.KEYSTATUS[7]=0;
		    break;
		}
		
		case KEY_NUM8:
		{
		    Interpreter.KEYSTATUS[8]=0;
		    break;
		}
		
		case KEY_NUM9:
		{
		    Interpreter.KEYSTATUS[9]=0;
		    break;
		}
		
		case KEY_STAR:
		{	
		    Interpreter.KEYSTATUS[0xA]=0;
		    break;
		}
		
		case KEY_POUND:
		{	
		    Interpreter.KEYSTATUS[0xB]=0;
		    break;
		}
	    }
	    
	    switch (action)
	    {		
		case UP:
		{
		    Interpreter.KEYSTATUS[0xC]=0;
		    break;
		}

		case DOWN:
		{	
		    Interpreter.KEYSTATUS[0xD]=0;
		    break;
		}
		
		case LEFT:
		{
		    Interpreter.KEYSTATUS[0xE]=0;
		    break;
		}
		
		case RIGHT:
		{	
		    Interpreter.KEYSTATUS[0xF]=0;
		    break;
		}		
	    } 	
	}
    }
}
