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
 * BitmapFont.java
 *
 * Creado el 9 de febrero de 2006, 13:09
 *
 * Autor: Nacho Sánchez
 *
 */

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

public class BitmapFont extends Sprite	
{    
    private int m_iCharacterWidth;
    private int m_iCharacterHeight;
    
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    
    public BitmapFont(Image theBitmapFont, int characterWidth, int characterHeight)
    {
	super(theBitmapFont, characterWidth, characterHeight);
	
	m_iCharacterWidth = characterWidth;
	m_iCharacterHeight = characterHeight;
	
	int[] sequence = new int[46];
	
	for (int i=0; i<46; i++)
	    sequence[i]=i;
	
	setFrameSequence(sequence);
	
	sequence = null;
    }
    
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    
    public void drawChar(int x, int y, char theChar)
    {
	setPosition(x, y);	
	
	///--- es una letra
	if (theChar > 64 && theChar <91)
	{
	    setFrame(14 + (theChar-65));
	}
	
	paint(MainCanvas.GFX);
    }
    
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    
    public void drawChars(int x, int y, char[] theChars)
    {
	for (int i=0; i<theChars.length; i++)
	{
	    setPosition(x + (m_iCharacterWidth * i), y);
	    
	    char tmp = theChars[i];
	    
	    ///--- es una letra
	    if (tmp > 64 && tmp <91)
	    {
		setFrame(14 + (tmp-65));
	    }

	    paint(MainCanvas.GFX);	    
	}	
    }
    
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    
    public void drawString(int x, int y, String theString)
    {
	for (int i=0; i<theString.length(); i++)
	{
	    setPosition(x + (m_iCharacterWidth * i), y);
	    
	    char tmp = theString.charAt(i);
	    
	    ///--- es una letra
	    if (tmp > 64 && tmp <91)
	    {
		setFrame(14 + (tmp-65));
	    }
	    else if (tmp == ' ')
	    {
		continue;
	    }
	    ///--- es un número
	    else if (tmp > 47 && tmp <58)
	    {
		setFrame(4 + (tmp-48));
	    }
	    ///--- es , - . /
	    else if (tmp > 43 && tmp <48)
	    {
		setFrame(tmp-44);
	    }
	    ///--- es : ;
	    else if (tmp > 57 && tmp <60)
	    {
		setFrame(40 + (tmp-58));
	    }
	    else if (tmp == '?')
	    {
		setFrame(42);
	    }
	    else if (tmp == '¿')
	    {
		setFrame(44);
	    }
	    else if (tmp == '!')
	    {
		setFrame(43);
	    }
	    else if (tmp == '¡')
	    {
		setFrame(45);
	    }

	    paint(MainCanvas.GFX);	    
	}	
    }
}
