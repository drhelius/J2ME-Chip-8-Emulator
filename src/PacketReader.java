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
 * PacketReader.java
 *
 * Creado el 27 de abril de 2006, 13:49
 *
 * Autor: Nacho Sánchez
 *
 */

import java.io.*;
import javax.microedition.lcdui.*;

public final class PacketReader
{ 
       
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    
    public final static Image loadImage(String fileName, String fxpFile, Class theClass)
    {	
	String[] file = new String[1];
	file[0] = fileName;
	byte[][] byteArray = loadFiles(file, fxpFile, theClass);	    

	return Image.createImage(byteArray[0], 0, byteArray[0].length);
    }
    
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    
    public final static Image[] loadImages(String[] fileNames, String fxpFile, Class theClass)
    {
	
	byte[][] byteArray = loadFiles(fileNames, fxpFile, theClass);
	
	Image[] imageArray = new Image[fileNames.length];
	
	for (int i=0; i < fileNames.length; i++)
	{
	    imageArray[i] = Image.createImage(byteArray[i], 0, byteArray[i].length);
	    byteArray[i] = null;
	}	

	return imageArray;	
    }
    
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    
    public final static byte[][] loadFiles(String[] fileNames, String fxpFile, Class theClass)
    {
	try
	{
	    DataInputStream theInputStream = new DataInputStream(theClass.getResourceAsStream(fxpFile));
	    
	    ///--- comprobar versión (1 byte)
	    if (theInputStream.read() != 3)
	    {		
		try { theInputStream.close(); } catch(Exception e) { }
		return null;
	    }
	    
	    ///--- numero de fichero que contiene el pak
	    int fileCount = theInputStream.readShort();
	    
	    if (fileCount <= 0)
	    {
		try { theInputStream.close(); } catch(Exception e) { }
		return null;
	    }	  
	    
	    byte[] dataArray = null;
	    
	    int[] sizeArray = new int[fileNames.length];
	    int[] offsetArray = new int[fileNames.length];
	    byte[][] fileArray = new byte[fileNames.length][];

	    int fileNameSize = 0;
	    char[] name = null;
	    byte[] nameBytes = null;
	    String finalName = null;
	    int leidos = 0;
	    int extractedBytes = 0;
	    int offset = 0;
		
	    ///--- busca los archivos y rellena su información de size y offset
	    while (fileCount > 0)
	    {	
		if (leidos == fileNames.length)
		{
		    ///--- tamaño del nombre
		    fileNameSize = theInputStream.read();
						
		    ///--- saltamos el nombre y el tamaño
		    theInputStream.skip(fileNameSize + 4);	
		}
		else
		{
		    ///--- tamaño del nombre
		    fileNameSize = theInputStream.read();
		
		    nameBytes = new byte[fileNameSize];
				
		    ///--- leemos el nombre
		    extractedBytes = 0;
		    while(extractedBytes < fileNameSize)
		    {
			extractedBytes += theInputStream.read(nameBytes, extractedBytes, fileNameSize - extractedBytes);
		    }
		
		    name = new char[fileNameSize];
		    
		    ///--- le aplicamos la transformación XOR
		    for (int i=0; i<fileNameSize; i++)
		    {
			if ((i % 2) == 0)
			    name[i] = (char)(nameBytes[i] ^ 0x7A);
			else
			    name[i] = (char)(nameBytes[i] ^ 0x35);
		    }

		    ///--- lo pasamos a cadena
		    finalName = String.valueOf(name);	    
		   
		    int i=0;	
		    for (; i<fileNames.length; i++)
		    {
			///--- si es uno de los que buscamos guardamos sus datos
			if (fileNames[i].compareTo(finalName) == 0)
			{			   
			    ///--- tamaño del fichero
			    sizeArray[i] = theInputStream.readInt();
			    
			    ///--- offset del fichero
			    offsetArray[i] = offset;
			    offset += sizeArray[i];			    
			    
			    leidos ++;		
			    
			    break;
			}			
		    }	
		    
		    ///--- no es el que buscamos
		    if (i == fileNames.length)
		    {			
			offset += theInputStream.readInt();
		    }
		}	
				
		fileCount--;	
	    }
	    
	    int skipped = 0;
	    
	    ///--- una vez tenemos los offsets buscamos en orden para extraer los archivos	    
	    for (int j=0; j<fileNames.length; j++)
	    {
		int indiceMenor = -1;
		int menor = 10000000;
				
		///--- buscamos cual tiene el menor offset de los que quedan por cargar
		for (int i=0; i<fileNames.length; i++)
		{
		    if (offsetArray[i] == -1)
			continue;
		    
		    if (offsetArray[i] < menor)
		    {
			menor = offsetArray[i];
			indiceMenor = i;
		    }
		}
		
		///--- lo quitamos para la siguiente iteracion
		offsetArray[indiceMenor] = -1;
		
		///--- nos posicionamos donde comienza dicho fichero
		theInputStream.skip(menor - skipped);
		
		dataArray = new byte[sizeArray[indiceMenor]];
		
		/// lo leemos
		extractedBytes = 0;
		while(extractedBytes < dataArray.length)
		{
		    extractedBytes += theInputStream.read(dataArray, extractedBytes, dataArray.length - extractedBytes);
		}
		
		for (int i=0; i<dataArray.length; i++)
		{
		    if ((i % 2) == 0)
			dataArray[i] ^= 0x6D;
		    else
			dataArray[i] ^= 0x17;
		}
		
		///--- cargamos la imagen
		fileArray[indiceMenor] = dataArray;
		
		///--- calculo para el neuvo offset
		skipped += (dataArray.length + menor) - skipped;	
	    }	  
	    
	    theInputStream.close();
	    
	    return fileArray;	    
	}
	catch(Exception e)
	{
	    return null;    
	}    
    }  
}
