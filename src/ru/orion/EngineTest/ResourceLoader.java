package ru.orion.EngineTest;

import static java.io.BufferedReader.*;
import static org.lwjgl.opengl.GL11.*;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.ByteBuffer;

import org.newdawn.slick.opengl.TextureLoader;

import static org.newdawn.slick.util.BufferedImageUtil.*;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;


public class ResourceLoader {
	
	public static Texture LoadTexture(String filename) {
		Texture tex = new Texture(filename);
		tex.setId(glGenTextures());
		int width;
		int height;
		try {
			BufferedInputStream input = new BufferedInputStream(new FileInputStream("./resources/textures/"+filename));
			PNGDecoder decoder = new PNGDecoder(input);
			tex.heigth = decoder.getHeight();
			tex.width = decoder.getWidth();
			ByteBuffer buf = ByteBuffer.allocateDirect(4*decoder.getHeight()*decoder.getWidth());
			decoder.decode(buf, decoder.getWidth()*4, Format.RGBA);
			buf.flip();
			input.close();
			glBindTexture(GL_TEXTURE_2D,tex.getId());
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glPixelStorei(GL_UNPACK_ALIGNMENT,1);
			glTexImage2D(GL_TEXTURE_2D,0,GL_RGBA,tex.width,tex.heigth,0,GL_RGBA,GL_UNSIGNED_BYTE,buf);
			return tex;
		}
		catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return null;
	}
	/*public static Texture LoadTexture(String filename) {
		Texture tex = new Texture(filename);
		//tex.setId(glGenTextures());
		int width;
		int height;
		try {
			tex.setId(TextureLoader.getTexture("PNG", new FileInputStream(new File("./resources/textures/"+filename))).getTextureID());
			return tex;
		}
		catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return null;
	}*/
	
	public static String LoadShader(String fileName) {
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = null;
		String currentLine = null;

		try {
			reader = new BufferedReader(new FileReader("./resources/shaders/"+fileName));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.err.println("Unable to create a buffered reader");
			System.exit(1);
		}
		
		try {
			while ((currentLine = reader.readLine())!=null) {
				builder.append(currentLine).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Unable to add a line");
			System.exit(1);
		}
		return builder.toString();
	}
	
	/*public static Mesh loadMeshObj(String fileName) {
		BufferedReader reader = null;
		String currentLine = null;

		try {
			reader = new BufferedReader(new FileReader("./resources/models/"+fileName));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.err.println("Unable to create a buffered reader");
			System.exit(1);
		}
		
	}*/
}
