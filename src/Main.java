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
 * Main.java
 *
 * Creado el 25 de enero de 2006, 12:25
 *
 * Autor: Nacho Sánchez
 *
 */

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class Main extends MIDlet implements CommandListener
{       
    public static MainCanvas m_TheCanvas;
    public static Main m_TheMidlet;
    
    private static Command m_ExitCommand;
    private static Command m_SpeedCommand;
    private static Command m_ResetCommand;
    private static Command m_OKSpeedCommand;
    private static Command m_HelpCommand;
    private static Command m_OKHelpCommand;
    private static Command m_MenuCommand;
    private static Form m_SpeedForm;
    private static Form m_HelpForm;
    private static Gauge m_SpeedGauge;
      
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    
    public Main()
    {	
	m_TheMidlet = this;
    }      
    
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    
    public void startApp() throws MIDletStateChangeException
    {	
	if (m_TheCanvas == null)
	{
	    m_TheCanvas = new MainCanvas();	
	    
	    m_ExitCommand = new Command("Exit", Command.EXIT, 5);
	    m_MenuCommand = new Command("Rom Menu", Command.ITEM, 1);
	    m_ResetCommand = new Command("Reset", Command.ITEM, 3);
	    m_SpeedCommand = new Command("Speed", Command.ITEM, 2);
	    m_OKSpeedCommand = new Command("OK", Command.OK, 1);	
	    m_HelpCommand = new Command("Help", Command.HELP, 4);
	    m_OKHelpCommand = new Command("OK", Command.OK, 1);
	    
	    m_TheCanvas.addCommand(m_ExitCommand);
	    m_TheCanvas.addCommand(m_SpeedCommand);
	    m_TheCanvas.addCommand(m_MenuCommand);
	    m_TheCanvas.addCommand(m_HelpCommand);
	    m_TheCanvas.addCommand(m_ResetCommand);
	    m_TheCanvas.setCommandListener(m_TheMidlet);
	    
	    m_SpeedForm = new Form("Speed");
	    m_SpeedGauge = new Gauge("Speed", true, 1, 5);
	    m_SpeedGauge.setMaxValue(50);
	    m_SpeedGauge.setValue(48);   
	    Interpreter.m_iSpeed = 2;
	    
	    m_SpeedForm.append(m_SpeedGauge);
	    m_SpeedForm.addCommand(m_OKSpeedCommand);
	    m_SpeedForm.setCommandListener(m_TheMidlet);
	    
	    m_HelpForm = new Form("Help");
	    m_HelpForm.append("J2ME CHIP-8/SCHIP Emulator with 41 classic games, by Nacho Sánchez (Helius), 2006.\nwww.geardome.com\n\nSelect a ROM from the list using 2 and 8. Run it pressing 5.\n\nIf the game runs too slow or tot fast, try adjusting the speed.\n\nThe original keyboard is mapped this way:\nThe original numerical keys are just the phone numerical keys, A=*, B=#, C=UP, D=DOWN, E=LEFT, F=RIGHT.\n\nHave fun!!");
	    m_HelpForm.addCommand(m_OKHelpCommand);
	    m_HelpForm.setCommandListener(m_TheMidlet);
	
	    m_TheCanvas.start();
	    Display.getDisplay(m_TheMidlet).setCurrent(m_TheCanvas);
	}	
    }
    
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    
    public void commandAction(Command c, Displayable d) 
    {        
        if (c == m_ExitCommand) 
        {
	    Display.getDisplay(this).setCurrent(null);
	    notifyDestroyed();
        }
        else if (c == m_SpeedCommand) 
        {
	    m_TheCanvas.m_bShouldPause = true;
	    Display.getDisplay(m_TheMidlet).setCurrent(m_SpeedForm);   	    
        }
	else if (c == m_ResetCommand) 
        {
	    if (m_TheCanvas.m_iState == 2)
	    {
		Interpreter.m_bMustReset = true; 		
	    }
        }
	else if (c == m_HelpCommand) 
        {
	    m_TheCanvas.m_bShouldPause = true;
	    Display.getDisplay(m_TheMidlet).setCurrent(m_HelpForm);   	    
        }
        else if (c == m_OKSpeedCommand) 
        {
	    Interpreter.m_iSpeed = (50 - m_SpeedGauge.getValue());
	    m_TheCanvas.m_bShouldPause = false;
            Display.getDisplay(m_TheMidlet).setCurrent(m_TheCanvas);            
        }
	else if (c == m_OKHelpCommand) 
        {
	    m_TheCanvas.m_bShouldPause = false;
            Display.getDisplay(m_TheMidlet).setCurrent(m_TheCanvas);            
        }
	else if (c == m_MenuCommand)
	{
	    if (m_TheCanvas.m_iState == 2)
		m_TheCanvas.m_iState = 1;	    
	}
    }
    
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    
    public void pauseApp()
    {
	
    }
    
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    
    public void destroyApp(boolean unconditional)
    {	
	Display.getDisplay(this).setCurrent(null);
        notifyDestroyed();
    }
}
