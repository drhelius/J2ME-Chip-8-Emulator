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
 * Interpreter.java
 *
 * Creado el 23 de mayo de 2006, 11:31
 *
 * Autor: Nacho Sánchez
 *
 */

import java.util.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.media.*;
import javax.microedition.lcdui.*;

public class Interpreter
{ 
    public static boolean m_bMustReset;
    public static final int m_iBackColor = 0xff000000;
    private static final int m_iForeColor = 0xff00ff00;
    public static int m_iSpeed;
    public final static boolean DEBUG = false;
    public static byte[] ROM;
    private static int[] m_FrameBuffer;
    private static long m_lLastUpdateTimers;
    public static MainCanvas m_theCanvas;
    
    private static Player m_BeepPlayer;
    
    private static int m_ScrXFrame, m_ScrYFrame;
    private static int m_iOpcode1, m_iOpcode2, m_iOpcode3, m_iOpcode4, m_iByte1, m_iByte2;
    private static int PC, SP, INDEX;
    private static int[] V;
    private static int[] HP48;
    private static int[] STACK;
    private static int DELAYTIMER;
    public static int[] KEYSTATUS; 
    private static Random m_Random;
    private static boolean m_bSuperChip;
    
    private static boolean m_bWaitingForKeyPress;
 
    private static final byte FONT8[] = {
	(byte)0x60, (byte)0xa0, (byte)0xa0, (byte)0xa0, (byte)0xc0,
	(byte)0x40, (byte)0xc0, (byte)0x40, (byte)0x40, (byte)0xe0,
	(byte)0xc0, (byte)0x20, (byte)0x40, (byte)0x80, (byte)0xe0,
	(byte)0xc0, (byte)0x20, (byte)0x40, (byte)0x20, (byte)0xc0,
	(byte)0x20, (byte)0xa0, (byte)0xe0, (byte)0x20, (byte)0x20,
	(byte)0xe0, (byte)0x80, (byte)0xc0, (byte)0x20, (byte)0xc0,
	(byte)0x40, (byte)0x80, (byte)0xc0, (byte)0xa0, (byte)0x40,
	(byte)0xe0, (byte)0x20, (byte)0x60, (byte)0x40, (byte)0x40,
	(byte)0x40, (byte)0xa0, (byte)0x40, (byte)0xa0, (byte)0x40,
	(byte)0x40, (byte)0xa0, (byte)0x60, (byte)0x20, (byte)0x40,
	(byte)0x40, (byte)0xa0, (byte)0xe0, (byte)0xa0, (byte)0xa0,
	(byte)0xc0, (byte)0xa0, (byte)0xc0, (byte)0xa0, (byte)0xc0,
	(byte)0x60, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x60,
	(byte)0xc0, (byte)0xa0, (byte)0xa0, (byte)0xa0, (byte)0xc0,
	(byte)0xe0, (byte)0x80, (byte)0xc0, (byte)0x80, (byte)0xe0,
	(byte)0xe0, (byte)0x80, (byte)0xc0, (byte)0x80, (byte)0x80
    };

    private static final byte FONTS[] = {
	(byte)0x7c, (byte)0xc6, (byte)0xce, (byte)0xde, (byte)0xd6, (byte)0xf6, (byte)0xe6, (byte)0xc6, (byte)0x7c, (byte)0x0,
	(byte)0x10, (byte)0x30, (byte)0xf0, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0xfc, (byte)0x0,
	(byte)0x78, (byte)0xcc, (byte)0xcc, (byte)0xc,  (byte)0x18, (byte)0x30, (byte)0x60, (byte)0xcc, (byte)0xfc, (byte)0x0,
	(byte)0x78, (byte)0xcc, (byte)0xc,  (byte)0xc,  (byte)0x38, (byte)0xc,  (byte)0xc,  (byte)0xcc, (byte)0x78, (byte)0x0,
	(byte)0xc,  (byte)0x1c, (byte)0x3c, (byte)0x6c, (byte)0xcc, (byte)0xfe, (byte)0xc,  (byte)0xc,  (byte)0x1e, (byte)0x0,
	(byte)0xfc, (byte)0xc0, (byte)0xc0, (byte)0xc0, (byte)0xf8, (byte)0xc,  (byte)0xc,  (byte)0xcc, (byte)0x78, (byte)0x0,
	(byte)0x38, (byte)0x60, (byte)0xc0, (byte)0xc0, (byte)0xf8, (byte)0xcc, (byte)0xcc, (byte)0xcc, (byte)0x78, (byte)0x0,
	(byte)0xfe, (byte)0xc6, (byte)0xc6, (byte)0x6,  (byte)0xc,  (byte)0x18, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x0,
	(byte)0x78, (byte)0xcc, (byte)0xcc, (byte)0xec, (byte)0x78, (byte)0xdc, (byte)0xcc, (byte)0xcc, (byte)0x78, (byte)0x0,
	(byte)0x7c, (byte)0xc6, (byte)0xc6, (byte)0xc6, (byte)0x7c, (byte)0x18, (byte)0x18, (byte)0x30, (byte)0x70, (byte)0x0,
	(byte)0x30, (byte)0x78, (byte)0xcc, (byte)0xcc, (byte)0xcc, (byte)0xfc, (byte)0xcc, (byte)0xcc, (byte)0xcc, (byte)0x0,
	(byte)0xfc, (byte)0x66, (byte)0x66, (byte)0x66, (byte)0x7c, (byte)0x66, (byte)0x66, (byte)0x66, (byte)0xfc, (byte)0x0,
	(byte)0x3c, (byte)0x66, (byte)0xc6, (byte)0xc0, (byte)0xc0, (byte)0xc0, (byte)0xc6, (byte)0x66, (byte)0x3c, (byte)0x0,
	(byte)0xf8, (byte)0x6c, (byte)0x66, (byte)0x66, (byte)0x66, (byte)0x66, (byte)0x66, (byte)0x6c, (byte)0xf8, (byte)0x0,
	(byte)0xfe, (byte)0x62, (byte)0x60, (byte)0x64, (byte)0x7c, (byte)0x64, (byte)0x60, (byte)0x62, (byte)0xfe, (byte)0x0,
	(byte)0xfe, (byte)0x66, (byte)0x62, (byte)0x64, (byte)0x7c, (byte)0x64, (byte)0x60, (byte)0x60, (byte)0xf0, (byte)0x0
    };
    
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    
    public final static void init()
    {
	m_Random = new Random();
	
	m_Random.setSeed(System.currentTimeMillis());
	
	m_FrameBuffer = new int[8192];
	
	V = new int[16];
	STACK = new int[16];
	KEYSTATUS = new int[16];
	HP48 = new int[8];
	
	try
        {
	    m_BeepPlayer = Manager.createPlayer(m_theCanvas.getClass().getResourceAsStream("/s"), "audio/x-wav");
	    m_BeepPlayer.prefetch();
	}
	catch (Exception e)
        {
            if (DEBUG)
		System.out.println("Error loading sound file");
        }
	
	reset(false);	
    }
    
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    
    public final static void loadFont()
    {	
	try
	{
	    for (int i = 4096, j=0;; i++, j++)
	    {
		ROM[i] = FONT8[j];	    
	    }
	}
	catch(ArrayIndexOutOfBoundsException e) { }
	
	try
	{
	    for (int i = 4176, j=0;; i++, j++)
	    {
		ROM[i] = FONTS[j];	    
	    }
	}
	catch(ArrayIndexOutOfBoundsException e) { }
    }
    
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    
    public final static void reset(boolean softReset)
    {	
	m_ScrXFrame = (MainCanvas.m_iScrWd / 2) - 64;
	m_ScrYFrame = (MainCanvas.m_iScrHg / 2) - 32;
	
	PC = 0x200;
	SP = 0xF;
	INDEX = 0;
	
	try
	{
	    for (int i=15;; --i)
	    {
		V[i] = 0;
		STACK[i] = 0;
		KEYSTATUS[i] = 0;
	    }
	}
	catch(ArrayIndexOutOfBoundsException e) { }
	
	try
	{
	    for (int i=7;; --i)
	    {
		HP48[i] = 0;
	    }
	}
	catch(ArrayIndexOutOfBoundsException e) { }
	
	if (!softReset)
	{
	    try
	    {
		for (int i=4335;; --i)
		{
		    ROM[i] = 0;
		}
	    }
	    catch(ArrayIndexOutOfBoundsException e) { }
	}
	
	m_bSuperChip = m_bWaitingForKeyPress = false;

	DELAYTIMER = 0;

	try
	{
	    for (int i=8191;; --i)
	    {
		m_FrameBuffer[i] = m_iBackColor;
	    }
	}
	catch(ArrayIndexOutOfBoundsException e) { }

	MainCanvas.GFX.setColor(m_iBackColor);
	MainCanvas.GFX.fillRect(0, 0, 128, 64);				
	m_theCanvas.flushGraphics(m_ScrXFrame, m_ScrYFrame, 129, 65);

	loadFont();
	
	m_lLastUpdateTimers = System.currentTimeMillis();
    }

    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    
    private static int a, cache1, cache2, cache3;
    private static int data, data2;
    private static int pixel;
    private static long actual;

    public final static void update()
    {
	if (m_bMustReset)
	{
	    reset(true);
	    m_bMustReset = false;
	}
	
	actual = System.currentTimeMillis();
		
	if (m_iSpeed > 0)
	{
	    for (int i=0; i<200*m_iSpeed; i++) a++;
	    
	    if (m_iSpeed == 50)
	    {
		for (int i=0; i<400*m_iSpeed; i++) a++;		
	    }
	}
	
	/// timers: they count down in threes using the PC's 18.2Hz 
	if ((actual - m_lLastUpdateTimers) >= 55)
	{
	    m_lLastUpdateTimers = actual;
	
	    if (DELAYTIMER >= 0)
		DELAYTIMER-=3;
	    else
		DELAYTIMER=0;
	}
	
	/// miramos si se esta esperando una tecla	
	if (m_bWaitingForKeyPress)
	{
	    int key = -1;
	    
	    try
	    {
		for (int i=15;; i--)
		{
		    if (KEYSTATUS[i] != 0)
			key = i;
		}
	    }
	    catch (ArrayIndexOutOfBoundsException e) {}

	    if (key > 0)
	    {
		V[m_iOpcode2] = key;
		m_bWaitingForKeyPress = false;
	    }
	    else
		return;
	}
	
	/// interprete	
	m_iByte1 = ROM[PC]; m_iByte1 &= 0xff;
	m_iByte2 = ROM[PC+1]; m_iByte2 &= 0xff;
	m_iOpcode1 = (m_iByte1 & 0xF0) >>> 4;
	m_iOpcode2 = m_iByte1 & 0x0F;
	m_iOpcode3 = (m_iByte2 & 0xF0) >>> 4;
	m_iOpcode4 = m_iByte2 & 0x0F;

	PC += 2;
	
	switch (m_iOpcode1)
	{
	    case 0x0:
	    {	
		switch(m_iByte2)
		{		    
		    case 0xE0:
		    {
			// Clear the screen
				
			cache1=8191;
			
			try
			{
			    while (true)
			    {
				m_FrameBuffer[cache1] = m_iBackColor;
				--cache1;
			    }				    
			}
			catch(ArrayIndexOutOfBoundsException e) { }
			
			m_theCanvas.GFX.setClip(0, 0, 128, 64);
			MainCanvas.GFX.setColor(m_iBackColor);
			MainCanvas.GFX.fillRect(0, 0, 128, 64);				
			m_theCanvas.flushGraphics(m_ScrXFrame, m_ScrYFrame, 129, 65);
			break;
		    }

		    case 0xEE:
		    {
			// Return from subroutine

			SP++;
			PC = STACK[SP];

			break;
		    }
		    
		    case 0xFB:
		    {
			// Scroll 4 pixels right  
						
			for (cache1 = 63; cache1 >=0; --cache1)
			{
			    for (cache2 = 127; cache2>=4 ; --cache2)
			    {
				m_FrameBuffer[(cache1<<7) + cache2] = m_FrameBuffer[(cache1<<7) + cache2 - 4];			    
			    }
			    
			    for (cache2 = 3; cache2>=0 ; --cache2)
			    {
				m_FrameBuffer[(cache1<<7) + cache2] = m_iBackColor;			
			    }
			}
			
			m_theCanvas.GFX.setClip(0, 0, 128, 64);
			m_theCanvas.GFX.drawRGB(m_FrameBuffer, 0, 128, 0, 0, 128, 64, false);    				
			m_theCanvas.flushGraphics(m_ScrXFrame, m_ScrYFrame, 129, 65);

			break;
		    }
		    
		    case 0xFC:
		    {
			// Scroll 4 pixels left 
			
			for (cache1 = 63; cache1 >=0; --cache1)
			{
			    for (cache2 = 0; cache2<124 ; ++cache2)
			    {
				m_FrameBuffer[(cache1<<7) + cache2] = m_FrameBuffer[(cache1<<7) + cache2 + 4];			    
			    }
			    
			    for (cache2 = 124; cache2<128 ; ++cache2)
			    {
				m_FrameBuffer[(cache1<<7) + cache2] = m_iBackColor;			
			    }
			}
			
			m_theCanvas.GFX.setClip(0, 0, 128, 64);
			m_theCanvas.GFX.drawRGB(m_FrameBuffer, 0, 128, 0, 0, 128, 64, false);    				
			m_theCanvas.flushGraphics(m_ScrXFrame, m_ScrYFrame, 129, 65);

			break;
		    }
		    
		    case 0xFD:
		    {
			// Quit the emulator 

			m_theCanvas.m_iState = 1;
			break;
		    }
		    
		    case 0xFE:
		    {
			// Enable CHIP-8 Graphic mode 

			m_bSuperChip=false;
			break;
		    }
		    
		    case 0xFF:
		    {
			// Enable SCHIP Graphic mode

			m_bSuperChip=true;
			break;
		    }

		    default:
		    {
			if (m_iOpcode3 == 0xC)
			{ 
			    // Scroll down N lines 
			    
			    while (m_iOpcode4 !=0)
			    {
				m_iOpcode4--;
				
				cache1=8063;
				cache2=8191;
				    
				try
				{				    
				    while (true)
				    {					
					m_FrameBuffer[cache2] = m_FrameBuffer[cache1];
					--cache1; --cache2;
				    }				    
				}
				catch(ArrayIndexOutOfBoundsException e) { }
				
				cache1=127;
				
				try
				{				    
				    while (true)
				    {
					m_FrameBuffer[cache1]=m_iBackColor;	
					--cache1;
				    }
				}
				catch(ArrayIndexOutOfBoundsException e) { }
				
				m_theCanvas.GFX.setClip(0, 0, 128, 64);
				m_theCanvas.GFX.drawRGB(m_FrameBuffer, 0, 128, 0, 0, 128, 64, false);    				
				m_theCanvas.flushGraphics(m_ScrXFrame, m_ScrYFrame, 129, 65);				
							
			    }			    
			}
			else
			{
			    if (DEBUG)
				ShowError();
			}
			break;	    
		    }	    
		}
		break;
	    }
	    case 0x1:
	    {	
		// Jump to address
		PC = (m_iOpcode2 << 8) + m_iByte2;
		break;
	    }
	    case 0x2:
	    {	 
		// Jump to sub
		STACK[SP] = PC;
		SP--;

		PC = (m_iOpcode2 << 8) + m_iByte2;
		break;
	    }
	    case 0x3:
	    {	 
		// Skip if reg equal
		if (V[m_iOpcode2] == m_iByte2)
		    PC += 2;
		break;
	    }
	    case 0x4:
	    {	 
		// Skip if reg not equal
		if (V[m_iOpcode2] != m_iByte2)
		    PC += 2;
		break;
	    }
	    case 0x5:
	    {		
		// Skip if reg equal reg
		if (V[m_iOpcode2] == V[m_iOpcode3])
		    PC += 2;
		break;
	    }
	    case 0x6:
	    {			
		// Move constant to reg
		V[m_iOpcode2] = m_iByte2;
		break;
	    }
	    case 0x7:
	    {	
		// Add constant to reg
		V[m_iOpcode2] += m_iByte2;
		V[m_iOpcode2] &= 0xFF;
		break;
	    }
	    case 0x8:
	    {	 
		switch(m_iOpcode4)
		{
		    case 0x0:
		    {
			// Move register to register
			V[m_iOpcode2] = V[m_iOpcode3];
			break;
		    }
	
		    case 0x1:
		    {
			// OR register with register
			V[m_iOpcode2] |= V[m_iOpcode3];
			break;
		    }
		
		    case 0x2:
		    {
			// AND register with register
			V[m_iOpcode2] &= V[m_iOpcode3];
			break;
		    }
		
		    case 0x3:
		    {
			// XOR register with register
			V[m_iOpcode2] ^= V[m_iOpcode3];
			break;
		    }
		
		    case 0x4:
		    {
			// Add register to register
			V[m_iOpcode2] += V[m_iOpcode3];

			if (V[m_iOpcode2] > 0xFF)
			{
			    V[0xF] = 1;
			    V[m_iOpcode2] &= 0xFF;
			}
			else
			    V[0xF] = 0;
		
			break;
		    }
		
		    case 0x5:
		    {
			// Sub reg from reg
			if (V[m_iOpcode2] >= V[m_iOpcode3])
			    V[0xF] = 1;
			else
			    V[0xF] = 0;

			V[m_iOpcode2] -= V[m_iOpcode3];
			
			V[m_iOpcode2] &= 0xFF;
			break;
		    }
		
		    case 0x6:
		    {
			// Shift register right
			V[0xF] = V[m_iOpcode2] & 0x1;			
			V[m_iOpcode2] >>>= 1;
			break;
		    }
		
		    case 0x7:
		    {
			// Sub reg from reg
			if (V[m_iOpcode3] >= V[m_iOpcode2])
			    V[0xF] = 1;
			else
			    V[0xF] = 0;

			V[m_iOpcode2] = V[m_iOpcode3] - V[m_iOpcode2];
			
			V[m_iOpcode2] &= 0xFF;
			break;
		    }
		
		    case 0xE:
		    {
			// Shift register left
			V[0xF] = (V[m_iOpcode2] & 0x80) >>> 7;			 
			V[m_iOpcode2] <<= 1;
			V[m_iOpcode2] &= 0xFF;
			break;
		    }	
		    
		    default:
		    {
			if (DEBUG)
			    ShowError();
			break;	    
		    }
		}
		break;
	    }
	    case 0x9:
	    {	
		// Skip if reg not equal reg
		if (V[m_iOpcode2] != V[m_iOpcode3])
		    PC += 2;
		break;
	    }
	    case 0xA:
	    {	 
		// move constant to index
		INDEX = 0xfff & ((m_iOpcode2 << 8) + m_iByte2);
		break;
	    }
	    case 0xB:
	    {	 
		// jump to address plus reg0
		PC = 0xfff & (V[0x0] + (m_iOpcode2 << 8) + m_iByte2);
		break;
	    }
	    case 0xC:
	    {	 
		// random number
		V[m_iOpcode2] = m_Random.nextInt() & m_iByte2;
		break;
	    }
	    case 0xD:
	    {	 
		// draw sprite
		
		V[0xF] = 0;	
		
		if (m_bSuperChip)
		{
		    cache1 = V[m_iOpcode2];
		    cache2 = V[m_iOpcode3];
		    
		    if (m_iOpcode4 == 0)
		    {
			// 16x16			
			
			for (int j=15; j>=0; --j)
			{
			    data = ROM[INDEX + (j<<1)];
			    data2 = ROM[INDEX + (j<<1) + 1];

			    for (int i=7; i>=0; --i)
			    {			    
				if ((data & (0x80 >>> i)) != 0)
				{
				    pixel = (((cache2+j) & 0x3F)<<7) + ((cache1+i) & 0x7F);
				   
				    if (m_FrameBuffer[pixel] == m_iForeColor)
				    {
					V[0xF] = 1;
					m_FrameBuffer[pixel] = m_iBackColor;				
				    }
				    else
				    {
					m_FrameBuffer[pixel] = m_iForeColor;			
				    }    
				}
				
				if ((data2 & (0x80 >>> i)) != 0)
				{
				    pixel = (((cache2+j) & 0x3F)<<7) + ((cache1+i+8) & 0x7F);
				    
				    if (m_FrameBuffer[pixel] == m_iForeColor)
				    {
					V[0xF] = 1;
					m_FrameBuffer[pixel] = m_iBackColor;				
				    }
				    else
				    {
					m_FrameBuffer[pixel] = m_iForeColor;			
				    }    		    
				}
			    }		    
			}

			m_theCanvas.GFX.setClip(cache1 , cache2, 16, 16);
			m_theCanvas.GFX.drawRGB(m_FrameBuffer, 0, 128, 0, 0, 128, 64, false);    
			m_theCanvas.flushGraphics(cache1 + m_ScrXFrame, cache2 + m_ScrYFrame, 17, 17);
			
		    }
		    else
		    {		    
			for (int j=m_iOpcode4-1; j>=0; --j)
			{
			    data = ROM[INDEX + j];

			    for (int i=7; i>=0; --i)
			    {			    
				if ((data & (0x80 >>> i)) != 0)
				{
				    pixel = (((cache2+j) & 0x3F)<<7) + ((cache1+i) & 0x7F);
				    
				    if (m_FrameBuffer[pixel] == m_iForeColor)
				    {
					V[0xF] = 1;
					m_FrameBuffer[pixel] = m_iBackColor;				
				    }
				    else
				    {
					m_FrameBuffer[pixel] = m_iForeColor;			
				    }    
				}		
			    }		    
			}

			m_theCanvas.GFX.setClip(cache1 , cache2, 8, m_iOpcode4);
			m_theCanvas.GFX.drawRGB(m_FrameBuffer, 0, 128, 0, 0, 128, 64, false);    
			m_theCanvas.flushGraphics(cache1 + m_ScrXFrame, cache2 + m_ScrYFrame, 9, m_iOpcode4+1);		
		    }
		}
		else
		{
		    cache1 = V[m_iOpcode2] << 1;
		    cache2 = V[m_iOpcode3] << 1;
		    
		    if (m_iOpcode4 == 0)
			m_iOpcode4=16;
		    
		    for (int j=m_iOpcode4-1; j>=0; --j)
		    {
			data = ROM[INDEX + j];

			for (int i=7; i>=0; --i)
			{			    
			    if ((data & (0x80 >>> i)) != 0)
			    {			
				pixel = (((cache2+ (j<<1)) & 0x3F)<<7) + ((cache1+ (i<<1)) & 0x7F);	
				
				if (m_FrameBuffer[pixel] == m_iForeColor)
				{
				    V[0xF] = 1;
				    m_FrameBuffer[pixel] = m_iBackColor;
				    m_FrameBuffer[pixel+1] = m_iBackColor;
				    m_FrameBuffer[pixel+128] = m_iBackColor;
				    m_FrameBuffer[pixel+129] = m_iBackColor;
				}
				else
				{
				    m_FrameBuffer[pixel] = m_iForeColor;
				    m_FrameBuffer[pixel+1] = m_iForeColor;
				    m_FrameBuffer[pixel+128] = m_iForeColor;
				    m_FrameBuffer[pixel+129] = m_iForeColor;
				}    
			    }		
			}		    
		    }
		    
		    m_theCanvas.GFX.setClip(cache1 , cache2, 16, m_iOpcode4<<1);
		    m_theCanvas.GFX.drawRGB(m_FrameBuffer, 0, 128, 0, 0, 128, 64, false);    
		    m_theCanvas.flushGraphics(cache1 + m_ScrXFrame, cache2 + m_ScrYFrame, 17, (m_iOpcode4<<1)+1);
		}		
		
		break;
	    }
	    case 0xE:
	    {
		switch(m_iOpcode3)
		{
		    case 0x9:
		    {
			// skip if key pressed
			if (KEYSTATUS[V[m_iOpcode2]] != 0)
			    PC += 2;
			break;
		    }
		
		    case 0xA:
		    {
			// skip if not key pressed
			if (KEYSTATUS[V[m_iOpcode2]] == 0)
			    PC += 2;
			break;
		    }
		    
		    default:
		    {
			if (DEBUG)
			    ShowError();
			break;	    
		    }		
		}
		break;
	    }
	    case 0xF:
	    {	 
		switch (m_iOpcode3)
		{
		    case 0x0:
		    {
			switch(m_iOpcode4)
			{
			    case 0x7:
			    {
				// Get dealy timer into reg
				V[m_iOpcode2] = DELAYTIMER;
				break;
			    }
			
			    case 0xA:
			    {
				// Wait for key press
				m_bWaitingForKeyPress = true;
				break;				
			    }
			    
			    default:
			    {
				if (DEBUG)
				    ShowError();
				break;	    
			    }
			}
			break;
		    }
		    
		    case 0x1:
		    {
			switch(m_iOpcode4)
			{
			    case 0x5:
			    {
				// Set delay timer
				DELAYTIMER = V[m_iOpcode2];
				break;
			    }
			
			    case 0x8:
			    {
				// Set sound timer
				try
				{
				    m_BeepPlayer.stop();
				    m_BeepPlayer.setMediaTime(0L);
				    m_BeepPlayer.start();
				    //Display.getDisplay(Main.m_TheMidlet).vibrate(100);
				}
				catch (Exception e) { }		
				
				break;
			    }
			
			    case 0xE:
			    {
				// Add reg to index
				INDEX += V[m_iOpcode2];
				INDEX &= 0xFFF;
				break;
			    }	
			    
			    default:
			    {
				if (DEBUG)
				    ShowError();
				break;	    
			    }
			}
			break;
		    }
			
		    case 0x2:
		    {			
			// Point index to font character CHIP-8
			INDEX = 0x1000 + (V[m_iOpcode2] * 0x5);
			break;
		    }
		   
		    case 0x3:
		    {
			switch(m_iOpcode4)
			{
			    case 0x0:
			    {
				// Point index to font character SCHIP
				INDEX = 0x1050 + (V[m_iOpcode2] * 0xa);
				break;			    
			    }
			    
			    case 0x3:
			    {
				// Store BCD
				cache1 = V[m_iOpcode2];
				cache2 = cache1 / 100;
				cache3 = (cache1 - (100*cache2))/10;	
				ROM[INDEX] = (byte)cache2;
				ROM[INDEX + 1] = (byte)cache3;
				ROM[INDEX + 2] = (byte)(cache1 - ((10*cache3) + (100*cache2)));
			    }
			    
			    default:
			    {
				if (DEBUG)
				    ShowError();			
				break;	    
			    }
			}		
			
			break;
		    }
		    
		    case 0x5:
		    {
			// Store regs at index			
			for (cache1 = m_iOpcode2; cache1 >= 0; --cache1)
			{
			    ROM[INDEX+cache1] = (byte)(V[cache1]);
			}
			break;
		    }
		   
		    case 0x6:
		    {
			// Load regs from index
			for (int cache1 = m_iOpcode2; cache1 >= 0; --cache1)
			{
			    V[cache1] = ROM[INDEX+cache1] & 0xff;
			}
			break;
		    }
		    
		    case 0x7:
		    {
			// Save V0...VX (X<8) in the HP48 flags 
			for (cache1 = m_iOpcode2; cache1 >= 0; --cache1)
			{
			    HP48[cache1] = V[cache1];
			}

			break;	
		    }
		    
		    case 0x8:
		    {			
			// Load V0...VX (X<8) from the HP48 flags  
			for (cache1 = m_iOpcode2; cache1 >= 0; --cache1)
			{
			    V[cache1] = HP48[cache1];
			}

			break;
		    }
		    
		    default:
		    {
			if (DEBUG)
			    ShowError();			
			break;	    
		    }
		}
		break;
	    }
	    
	    default:
	    {
		if (DEBUG)
		    ShowError();		
		break;	    
	    }
	}		
    }
    
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    
    private final static void ShowError()
    {
	System.out.println("Not implemented: " + "0x" + Integer.toString(m_iByte1, 16).toUpperCase() + " 0x" + Integer.toString(m_iByte2, 16).toUpperCase());
    }
}
